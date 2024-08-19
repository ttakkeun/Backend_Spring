package ttakkeun.ttakkeun_server.service.DiagnoseService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpHead;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.config.ChatGPTConfig;
import ttakkeun.ttakkeun_server.dto.diagnose.*;
import ttakkeun.ttakkeun_server.entity.*;
import ttakkeun.ttakkeun_server.entity.Record;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.entity.enums.Neutralization;
import ttakkeun.ttakkeun_server.entity.enums.PetType;
import ttakkeun.ttakkeun_server.repository.PetRepository;
import ttakkeun.ttakkeun_server.repository.RecordRepository;
import ttakkeun.ttakkeun_server.repository.ResultRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ttakkeun.ttakkeun_server.entity.enums.Neutralization.NEUTRALIZATION;
import static ttakkeun.ttakkeun_server.entity.enums.Neutralization.UNNEUTRALIZATION;
import static ttakkeun.ttakkeun_server.entity.enums.PetType.CAT;
import static ttakkeun.ttakkeun_server.entity.enums.PetType.DOG;

@Transactional
@Service
@Slf4j
public class DiagnoseChatGPTService {
    private final PetRepository petRepository;
    private final RecordRepository recordRepository;
    private final ChatGPTConfig chatGPTConfig;
    private final ResultRepository resultRepository;

    @Autowired
    public DiagnoseChatGPTService(PetRepository petRepository, RecordRepository recordRepository, ChatGPTConfig chatGPTConfig, ResultRepository resultRepository) {
        this.petRepository = petRepository;
        this.recordRepository = recordRepository;
        this.chatGPTConfig = chatGPTConfig;
        this.resultRepository = resultRepository;
    }

    @Value("${openai.url.model}")
    private String modelUrl;

    @Value("${openai.url.model-list}")
    private String modelListUrl;

    @Value("${openai.url.prompt}")
    private String promptUrl;

    public PostDiagnoseResponseDTO postDiagnoseByRecord(Long memberId, PostDiagnoseRequestDTO postDiagnoseRequestDTO) throws Exception {
        Long petId = postDiagnoseRequestDTO.pet_id(); // request body에서 pet_id값을 가져옴
        Optional<Pet> petOpt= petRepository.findByPetIdAndMember_MemberId(petId, memberId);

        if (!petOpt.isPresent()) {
            // Optional 객체에서 값이 비어있는 경우 예외를 던짐
            throw new NoSuchElementException("Pet with ID " + petId + " not found");
        }

        Pet pet = petOpt.get();

        // GPT에게 질문할 때 사용할 반려동물 정보, 일지 내용을 가져옴
        ChatGPTQuestionDTO questionByDTO = makeQuestionForGPT(pet, postDiagnoseRequestDTO);

        // 질문 문장 구성
        String questionString = makeQuestionString(questionByDTO);
        System.out.println("question is : " + questionString);

        // ChatGPT 진단 수행
        String chatGPTRequestDTO = diagnoseByChatGPT(questionString);

        // 이후 ChatGPT의 답변에서 필요한 값을 추출하고 DB에 POST해야 함

        // 값을 저장할 HashMap
        Map<String, String> extractedValues = new HashMap<>();

        // 받은 답변에서 대괄호 안의 등호 오른쪽 값을 추출해냄
        Pattern pattern = Pattern.compile("\\[(\\w+)=(.*?)\\]");
        Matcher matcher = pattern.matcher(chatGPTRequestDTO);

        // 모든 일치 항목 찾기
        while (matcher.find()) {
            extractedValues.put(matcher.group(1), matcher.group(2));
        }

        int score = Integer.parseInt(extractedValues.get("score"));
        String detail = extractedValues.get("detail");
        String care = extractedValues.get("care");
        String product1 = extractedValues.get("product1");
        String product2 = extractedValues.get("product2");
        String product3 = extractedValues.get("product3");
        String product4 = extractedValues.get("product4");
        String product5 = extractedValues.get("product5");

        Result result = Result.builder()
                .score(score)
                .resultDetail(detail)
                .resultCare(care)
                .build();

        // record, category 추가해야 함
        //                 .record(record)
        //                .resultCategory(category)

        // 결과 저장
        Result savedResult = resultRepository.save(result);

        // DTO에 제품명 반환을 위해 products 리스트 구성
        List<String> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);
        products.add(product3);
        products.add(product4);
        products.add(product5);

        return new PostDiagnoseResponseDTO(score, detail, care, products);
    }

    public ChatGPTQuestionDTO makeQuestionForGPT(Pet pet, PostDiagnoseRequestDTO postDiagnoseRequestDTO) throws Exception {

        // pet 객체에서 가져와야하는 반려동물 정보 : petVariety, birth, neutralization, petType
        PetType petType = pet.getPetType(); // 고양이인지 강아지인지 (CAT, DOG)
        String petVariety = pet.getPetVariety(); // 종
        String birth = pet.getBirth(); // 생년월일
        Neutralization neutralization = pet.getNeutralization(); // 중성화 여부 (NEUTRALIZATION, UNNEUTRALIZATION)

        // PostDiagnoseRequestDTO의 records에서 record_id추출 후 QuestionDetailDTO 구성
        List<ChatGPTQuestionDTO.RecordDetailDTO> recordDetailDTOList = postDiagnoseRequestDTO.records().stream()
                .map(this::getRecordDetail)
                .collect(Collectors.toList());

        return new ChatGPTQuestionDTO(petType, petVariety, birth, neutralization, recordDetailDTOList);
    }

    // ChatGPT에게 질문시 사용할 일지 내용을 가져옴
    public ChatGPTQuestionDTO.RecordDetailDTO getRecordDetail(PostDiagnoseRequestDTO.Record record_by_list) {
        try {
            // 질문을 구성하기 위해 일지 관련해서 필요한 것 : category, etc, questionText, descriptionText, answerText, imageUrl

            Long recordId = record_by_list.record_id(); // 선택된 일지의 id를 가져옴
            System.out.println("recordId is : " + recordId);

            Optional<Record> recordOpt = recordRepository.findByRecordId(recordId);

            if (!recordOpt.isPresent()) {
                // Optional 객체에서 값이 비어있는 경우 예외를 던짐
                throw new NoSuchElementException("Record with ID " + recordId + " not found");
            }

            // record 엔티티에서 가져올 것 : category, answerList 객체, etc
            Record record = recordOpt.get();
            LocalDateTime createdAt = record.getCreatedAt();
            Category category = record.getCategory();
            List<UserAnswer> answerList = record.getAnswerList(); // 일지에 해당하는 사용자의 답변을 가져옴
            String etc = record.getEtc();

            // 일지에 해당하는 질문 내용과 답변 내용, 이미지 url들을 가져옴
            List<ChatGPTQuestionDTO.AnswerDTO> answerDTOList = answerList.stream()
                    .map(this::getAnswerDetail)
                    .collect(Collectors.toList());

            return new ChatGPTQuestionDTO.RecordDetailDTO(createdAt, category, answerDTOList, etc);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("에러 발생");
        }
    }

    // 일지 중 질문, 답변과 관련된 것들을 가져옴
    public ChatGPTQuestionDTO.AnswerDTO getAnswerDetail(UserAnswer answer) {
        // answer를 통해 questionText, descriptionText, answerText, imageURLs를 가져옴

        // answerList 객체에서 가져올 것 : answerText, question 객체, imageList 객체
        // question 객체에서 가져올 것 : questionText, descriptionText
        List<String> answerText = answer.getUserAnswerText();
        ChecklistQuestion question = answer.getQuestion();
        String questionText = question.getQuestionText();
        String descriptionText = question.getDescriptionText();
        List<Image> imageList = answer.getImages(); // 답변과 함께 저장된 images

        // image 객체에서 가져올 것 : imageUrl
        // image 객체 리스트에서 스트림 생성. imageUrl들을 리스트로 수집함
        List<String> imageURLs = imageList.stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());

        // 현재 userAnswerText가 List<String>이므로 String으로 변환해서 처리
        String answerTexts = String.join(",", answerText);

        return new ChatGPTQuestionDTO.AnswerDTO(questionText, descriptionText, answerTexts, imageURLs);
    }

    // DTO의 내용으로 구체적인 질문 내용을 만듦
    public String makeQuestionString(ChatGPTQuestionDTO chatGPTQuestionDTO) {

        // 여기에 지금 answerDTO 값이 null로 들어옴!!!!
        System.out.println("make Question String에 들어온 chatGptQuestionDTO는 : " + chatGPTQuestionDTO);

        String question;

        try {

            String petType = switch (chatGPTQuestionDTO.petType()) {
                case CAT -> "고양이";
                case DOG -> "강아지";
                default -> "에러 발생";
                // default -> throw new NullPointerException("반려동물 정보가 정확히 저장되어 있지 않습니다.");
                // ENUM PetType은 CAT, DOG만 있으므로 이외의 경우는 모두 null로 간주하고 처리하였음
            };

            String petVariety = Objects.requireNonNull(chatGPTQuestionDTO.petVariety(), "반려동물 정보가 정확히 저장되어 있지 않습니다.");

            // 생년월일은 null 허용
            String birth = Objects.requireNonNull(chatGPTQuestionDTO.birth(), "반려동물 정보가 정확히 저장되어 있지 않습니다.");

            String neutralization = switch (chatGPTQuestionDTO.neutralization()) {
                case NEUTRALIZATION -> "된 상태야";
                case UNNEUTRALIZATION -> "되지 않은 상태야";
                default -> "에러 발생";
                // default -> throw new NullPointerException("반려동물 정보가 정확히 저장되어 있지 않습니다.");
                // ENUM neutralization은 NEUTRALIZATION, UNNEUTRALIZATION만 있으므로 이외의 경우는 모두 null로 간주하고 처리하였음
            };


            question = "다음과 같은 질문에 답변해줘. 사용자의 반려동물에 대한 기록을 보고 해당 반려동물에 상태에 대해 판단할거야. " +
                    "사용자의 반려동물은 " + petType + "이고, 종은 " + petVariety + "야. 생년월일은 " +
                    birth + "이고 중성화는 " + neutralization + ". 이제 사용자가 기록한 일지의 내용을 알려줄게." +
                    " 일지는 최소 1개부터 최대 5개까지 제시될 거고, 만약 일지가 여러 개라면 여러 개의 일지 내용을 분석해서 진단을 내려줘. \n";


        } catch(NullPointerException e) {
            log.error("반려동물 정보가 정확히 저장되어 있지 않습니다.", e);
            throw new NullPointerException("반려동물 정보가 정확히 저장되어 있지 않습니다.");
        }

        // 진단에 사용된 일지가 몇개인지 확인
        int recordCount = chatGPTQuestionDTO.recordDetailDTO().size();

        String category = null;

        // 일지 개수만큼 반복하며 일지 내용을 question 문장에 추가함
        for (int i = 0; i < recordCount; i++) {
            if (recordCount == 0) {
                throw new IllegalArgumentException("일지가 선택되지 않았습니다. 다시 시도해주세요");
            } else {
                // 답변이 db에 없을 때 오류처리 필요
                Category categoryEnum = chatGPTQuestionDTO.recordDetailDTO().get(i).category();
                category = categoryEnum.name();
                question += "다음은 사용자가 기록한 " + i+1 + "번째 일지의 내용이야. 이 일지는 " + chatGPTQuestionDTO.recordDetailDTO().get(i).created_at() + "에 저장되었고, " +
                        category + "에 대한 기록이야. ";
                // 하나의 일지는 3개의 질문을 기록하므로
                for (int j = 0; j < 3; j++) {
                    question += j+1 + "번째 질문은 '" + chatGPTQuestionDTO.recordDetailDTO().get(i).answerDTO().get(j).questionText() + "'이고 " +
                            "추가적인 서비스의 설명은 '" + chatGPTQuestionDTO.recordDetailDTO().get(i).answerDTO().get(j).descriptionText() + "'야. " +
                            "사용자는 이에 대해 '" + chatGPTQuestionDTO.recordDetailDTO().get(i).answerDTO().get(j).answerText() + " '라고 답변했어. ";
                }
                question += " 마지막으로 사용자는 이 일지에 대해 다음과 같이 추가적으로 기록했어. '" + chatGPTQuestionDTO.recordDetailDTO().get(i).etc() +"' \n";
            }
        }

        question += "이제 위 기록을 통해 반려동물의 " + category + "건강상태 진단을 내려줘. 내가 알고 싶은 건 100점 만점으로 평가한 점수, 진단에 대한 세부 설명, " +
                "추후 관리법, 추천 제품 5개야. 추천 제품은 이름만 알려주면 되고, 한국에 사는 사용자가 구매할 수 있도록 한국에서 판매하는 제품을 추천해줘. " +
                "만약 너가 모든 제품의 이름이 영어로 되어있는 제품을 추천한다면, 잘못된 추천일 가능성이 높아. \n" +
                "나는 너가 한 답변을 DB에 저장해야 하니 다음과 같은 형식을 반드시 맞춰서 답변해줘. 내가 제시한 형식과 동일하게 대괄호, score와 같은 변수명, 등호를 포함하도록 유의해야 돼. " +
                "등호의 오른쪽에는 너의 답변을 넣어주면 돼. \n" +
                "'위 기록의 점수는 [score=점수]점입니다. 세부 설명은 다음과 같습니다. [detail=세부 설명] 추후 관리법은 다음과 같습니다. [care=추후 관리법] 추천 제품은 다음과 같습니다. " +
                "1. [product1=첫 번째 추천 제품] 2. [product2=두 번째 추천 제품] 3. [product3=세 번째 추천 제품] 4. [product4=네 번째 추천 제품] 5. [product5=다섯 번째 추천 제품]'";



        return question;
    }


    // ChatGPT 진단 수행
    public String diagnoseByChatGPT(String question) {
        try {

            // gpt 진단하는 클래스
            Map<String, Object> resultMap = new HashMap<>();

            // 토큰 정보가 포함된 Header를 가져옴
            HttpHeaders headers = chatGPTConfig.httpHeaders();

            // ChatGPTCompletionDTO 구성
            // role을 사용자로 지정하고 content에는 question값을 넣어줌
            ChatGPTCompletionDTO chatGPTCompletionDTO = new ChatGPTCompletionDTO("user", question);

            // String으로 받아온 questions를 DTO에 넣어서 DTO 구성
            ChatGPTRequestDTO chatGPTRequestDTO = new ChatGPTRequestDTO("gpt-4o-mini", List.of(chatGPTCompletionDTO));
            System.out.println("chatGPTRequestDTO is  : " + chatGPTRequestDTO);

            // ObjectMapper를 사용하여 DTO를 JSON 문자열로 변환
            // ChatGPT api를 사용하려면 JSON 형태로 통신해야 함
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPayload = objectMapper.writeValueAsString(chatGPTRequestDTO);

            // 통신을 위한 RestTemplate 구성
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);
            System.out.println("Request URL: " + promptUrl);
            System.out.println("Request Headers: " + headers);
            System.out.println("Request Body: " + jsonPayload);

            // 여기에서 오류 발생
            ResponseEntity<String> response = chatGPTConfig.restTemplate()
                    .exchange(promptUrl, HttpMethod.POST, requestEntity, String.class);

            System.out.println("Response Status: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());


            // String -> HashMap 역직렬화
            resultMap = objectMapper.readValue(response.getBody(), new TypeReference<>() {});

            String content = null;

            // 통신 결과에서 GPT의 응답 내용만 추출해냄
            List<Map<String, Object>> choices = (List<Map<String, Object>>) resultMap.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> firstChoice = choices.get(0);
                Map<String, String> message = (Map<String, String>) firstChoice.get("message");
                content = message.get("content");
                log.debug("Extracted content: " + content);  // 로그로 content 출력
            }

            return content;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.debug("JsonMappingException :: " + e.getMessage(), e);
            throw new RuntimeException("오류가 발생했습니다. 서버 관리자에게 문의해주세요" + e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            // try문 내의 if문 실행되지 않을 경우 content값이 null이므로 에러 발생
            log.debug("RuntimeException :: " + e.getMessage(), e);
            throw new RuntimeException("오류가 발생했습니다. 서버 관리자에게 문의해주세요" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("알 수 없는 오류가 발생했습니다." + e.getMessage(), e);
            throw new RuntimeException("알 수 없는 오류가 발생했습니다. 서버 관리자에게 문의해주세요" + e.getMessage());
        }
    }
}