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
        try {
            Long petId = postDiagnoseRequestDTO.pet_id(); // request body에서 pet_id값을 가져옴
            Optional<Pet> petOpt = petRepository.findByPetIdAndMember_MemberId(petId, memberId);

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

            // ChatGPT 진단
             String diagnoseResponse = diagnoseByChatGPT(questionString);

             System.out.println("diagnoseResponse is : " + diagnoseResponse);

            // 이후 ChatGPT의 답변에서 필요한 값을 추출하고 DB에 POST해야 함

            // 값을 저장할 HashMap
            Map<String, String> extractedValues = new HashMap<>();

            // 받은 답변에서 대괄호 안의 등호 오른쪽 값을 추출해냄
            Pattern pattern = Pattern.compile("\\[(\\w+)=(.*?)\\]");
            Matcher matcher = pattern.matcher(diagnoseResponse);

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

             /*
             detail과 care의 내용이 올바르게 들어왔는지 확인
             가끔 지정한 형식대로 [detail=(실제 세부 설명)] [care=(실제 추후 관리법)]이 아니라
             [detail=세부설명] (실제 세부 설명) [care=추후관리법] (실제 추후 관리법)과 같이 잘못된 답변 형식을 받아올 때가 있음
             이로 인해 실제 세부 설명과 실제 추후 관리법이 아니라, "세부설명" "추후관리법"이라는 글자만 반환하는 문제 발생
             이 부분을 해결하기 위해 detail과 care값이 "세부설명", "추후관리법"이 아니라 제대로 받아와졌는지 확인하고,
             만약 "세부설명", "추후관리법"이 받아와졌다면 괄호 뒤의 내용을 저장하도록 수정함
             */

            // detail 값이 잘못 저장되었을 경우
            if (detail.equals("세부설명")) {
                // 잘못된 형식의 세부 설명 다시 추출
                Pattern incorrectDetailPattern = Pattern.compile("\\[detail=세부설명\\]\\s*(.+?)\\s*(?=\\[|$)", Pattern.DOTALL);
                // [detail=세부설명] 뒤의 내용을 추출, [을 만나면 추출을 종료하게 됨
                Matcher incorrectDetailMatcher = incorrectDetailPattern.matcher(diagnoseResponse);
                if (incorrectDetailMatcher.find()) {
                    detail = incorrectDetailMatcher.group(1);
                }
            }

            // care 값이 잘못 저장되었을 경우
            if (care.equals("추후관리법")) {
                // 잘못된 형식의 추후 관리법 다시 추출
                // Pattern incorrectCarePattern = Pattern.compile("\\[care=추후관리법\\]\\s*(.+?)\\s*(?=\\[|$)", Pattern.DOTALL);
                Pattern incorrectCarePattern = Pattern.compile("\\[care=추후관리법\\]\\s*(.+?)\\s*(?=추천 제품은 다음과 같습니다\\.|$)", Pattern.DOTALL);
                // [care=추후관리법] 뒤의 내용을 추출, '추천 제품은 다음과 같습니다.'라는 문구를 만나면 추출을 종료하게 됨
                // 만약 세부설명처럼 [을 만났을 때 추출을 종료하도록 하면 "추천 제품은 다음과 같습니다."도 care에 같이 저장되는 문제가 발생함.
                // 추후관리법 뒤에는 추천제품 내용이 오므로 해당 문구를 만나면 종료하도록 하였음
                Matcher incorrectCareMatcher = incorrectCarePattern.matcher(diagnoseResponse);
                if (incorrectCareMatcher.find()) {
                    care = incorrectCareMatcher.group(1);
                }
            }


            System.out.println("detail is : " + detail);
            System.out.println("care is : " + care);


            Long recordId = postDiagnoseRequestDTO.records().get(0).record_id();

            Optional<Record> recordOpt = recordRepository.findByRecordId(recordId);

            if (!recordOpt.isPresent()) {
                // Optional 객체에서 값이 비어있는 경우 예외를 던짐
                throw new NoSuchElementException("Record with ID " + recordId + " not found");
            }

            Record record = recordOpt.get();
            Category category = record.getCategory();

            // record는 객체를 넣어줘야 함
            // record에는 여러 개가 들어와도 하나만 넣음
            // postDiagnoseRequestDTO에서 첫 번째 record_id값을 가져와서 해당 객체를 참조하도록 하고,
            // 해당 record 객체의 카테고리 값으로 category도 저장함
            Result result = Result.builder()
                    .score(score)
                    .resultDetail(detail)
                    .resultCare(care)
                    .record(record)
                    .resultCategory(category)
                    .pet(pet)   // pet_id 저장
                    .build();


            // 결과 저장
            Result savedResult = resultRepository.save(result);

            // 저장된 result의 id를 가져옴
            Long resultId = savedResult.getResultId();

            // DTO에 제품명 반환을 위해 products 리스트 구성
            List<String> products = new ArrayList<>();
            products.add(product1);
            products.add(product2);
            products.add(product3);
            products.add(product4);
            products.add(product5);

            return new PostDiagnoseResponseDTO(resultId, score, detail, care, products);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            log.error("데이터 조회에 실패했습니다." + e.getMessage(), e);
            throw new NoSuchElementException("데이터 조회에 실패했습니다. 서버 관리자에게 문의해주세요" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("알 수 없는 오류가 발생했습니다." + e.getMessage(), e);
            throw new RuntimeException("알 수 없는 오류가 발생했습니다. 서버 관리자에게 문의해주세요" + e.getMessage());
        }
    }

    public ChatGPTQuestionDTO makeQuestionForGPT(Pet pet, PostDiagnoseRequestDTO postDiagnoseRequestDTO) throws Exception {
        try {

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
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            log.error("데이터 조회 오류가 발생했습니다." + e.getMessage(), e);
            throw new NoSuchElementException("데이터 조회 오류가 발생했습니다. 서버 관리자에게 문의해주세요" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("알 수 없는 오류가 발생했습니다." + e.getMessage(), e);
            throw new RuntimeException("알 수 없는 오류가 발생했습니다. 서버 관리자에게 문의해주세요" + e.getMessage());
        }
    }

    // ChatGPT에게 질문시 사용할 일지 내용을 가져옴
    public ChatGPTQuestionDTO.RecordDetailDTO getRecordDetail(PostDiagnoseRequestDTO.Record record_by_list) {
        try {
            // 질문을 구성하기 위해 일지 관련해서 필요한 것 : category, etc, questionText, descriptionText, answerText, imageUrl

            Long recordId = record_by_list.record_id(); // 선택된 일지의 id를 가져옴
            // System.out.println("recordId is : " + recordId);

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
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            log.error("데이터 조회 오류가 발생했습니다." + e.getMessage(), e);
            throw new NoSuchElementException("데이터 조회 오류가 발생했습니다. 서버 관리자에게 문의해주세요" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("알 수 없는 오류가 발생했습니다." + e.getMessage(), e);
            throw new RuntimeException("알 수 없는 오류가 발생했습니다. 서버 관리자에게 문의해주세요" + e.getMessage());
        }
    }

    // 일지 중 질문, 답변과 관련된 것들을 가져옴
    public ChatGPTQuestionDTO.AnswerDTO getAnswerDetail(UserAnswer answer) {
        try {
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
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            log.error("데이터 조회 오류가 발생했습니다." + e.getMessage(), e);
            throw new NoSuchElementException("데이터 조회 오류가 발생했습니다. 서버 관리자에게 문의해주세요" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("알 수 없는 오류가 발생했습니다." + e.getMessage(), e);
            throw new RuntimeException("알 수 없는 오류가 발생했습니다. 서버 관리자에게 문의해주세요" + e.getMessage());
        }
    }

    // DTO의 내용으로 구체적인 질문 내용을 만듦
    public String makeQuestionString(ChatGPTQuestionDTO chatGPTQuestionDTO) {
        try {

            // System.out.println("make Question String에 들어온 chatGptQuestionDTO는 : " + chatGPTQuestionDTO);


            String petType = switch (chatGPTQuestionDTO.petType()) {
                case CAT -> "고양이";
                case DOG -> "강아지";
                default -> throw new NullPointerException("반려동물 정보가 정확히 저장되어 있지 않습니다.");
                // ENUM PetType은 CAT, DOG만 있으므로 이외의 경우는 모두 null로 간주하고 처리하였음
            };

//            String petVariety = Objects.requireNonNull(chatGPTQuestionDTO.petVariety(), "반려동물 정보가 정확히 저장되어 있지 않습니다.");
//
//            // 생년월일은 null 허용
//            String birth = Objects.requireNonNull(chatGPTQuestionDTO.birth(), "반려동물 정보가 정확히 저장되어 있지 않습니다.");
//
//            String neutralization = switch (chatGPTQuestionDTO.neutralization()) {
//                case NEUTRALIZATION -> "된 상태야";
//                case UNNEUTRALIZATION -> "되지 않은 상태야";
//                default -> "에러 발생";
//                // default -> throw new NullPointerException("반려동물 정보가 정확히 저장되어 있지 않습니다.");
//                // ENUM neutralization은 NEUTRALIZATION, UNNEUTRALIZATION만 있으므로 이외의 경우는 모두 null로 간주하고 처리하였음
//            };


            // 진단에 사용된 일지가 몇개인지 확인
            int recordCount = chatGPTQuestionDTO.recordDetailDTO().size();

            String question = "";

            Category categoryEnum = chatGPTQuestionDTO.recordDetailDTO().get(0).category();
            String category = categoryEnum.name();

            question = petType + " " + category + "일지를 기반으로 반려동물 건강을 진단해줘\n";

            // 일지 개수만큼 반복하며 일지 내용을 question 문장에 추가함
            for (int i = 0; i < recordCount; i++) {
                if (recordCount == 0) {
                    throw new IllegalArgumentException("일지가 선택되지 않았습니다. 다시 시도해주세요");
                } else if (recordCount > 1) {
                    // 일지가 2개 이상인 경우에만 일지 번호 작성
                    question += "일지 " + (i + 1) + "\n";
                }

                // 하나의 일지는 3개의 질문을 기록하므로
                for (int j = 0; j < 3; j++) {
                    question += "Q." + chatGPTQuestionDTO.recordDetailDTO().get(i).answerDTO().get(j).questionText() + " " +
                            "A. " + chatGPTQuestionDTO.recordDetailDTO().get(i).answerDTO().get(j).answerText() + "\n";

                    List<String> imageURL = chatGPTQuestionDTO.recordDetailDTO().get(i).answerDTO().get(j).imageURLs();

                    if (imageURL.size() > 0) {
                        // 이미지가 있는 경우에만 question에 링크 삽입
                        String imageURLs = String.join(", ", imageURL);
                        question += "이미지:" + imageURLs + "\n";
                    }
                }

                if (chatGPTQuestionDTO.recordDetailDTO().get(i).etc() != null) {
                    // null이 아닌 경우에만 추가기록 작성
                    question += "추가기록:" + chatGPTQuestionDTO.recordDetailDTO().get(i).etc();
                }

                question += "\n";
            }

            question += "아래형식을 반드시지켜서 등호의오른쪽에 답변내용을 넣어줘. 점수는100점만점으로숫자만넣어줘. 추천제품은 구체적인제품명을 영어는최대한적지말고 한국어로알려줘\n" +
                    "위 기록의 점수는 [score=점수]점입니다. [detail=세부설명] [care=추후관리법] 추천 제품은 다음과 같습니다. " +
                    "[product1=추천제품] [product2=추천제품] [product3=추천제품] [product4=추천제품] [product5=추천제품]";


            // Q.귀 안쪽에 귀지가 많이 쌓여있나요? A.귀지가 많아요.,귀지의 색이 이상해요.
            //Q.귀에서 악취가 나나요? A.악취가 나지 않아요.
            //Q.귀를 흔들거나 과도하게 긁나요? A.평소와 같아요.
            //추가기록:Example details 1
            //아래 형식을 반드시 지켜서 등호의 오른쪽에 답변내용을 넣어줘. 점수는100점만점으로숫자만넣어줘
            //위 기록의 점수는 [score=점수]점입니다. [detail=세부설명] [care=추후관리법] 추천 제품은 다음과 같습니다. [product1=추천제품] [product2=추천제품] [product3=추천제품] [product4=추천제품] [product5=추천제품]

            return question;
        } catch (NullPointerException e) {
            e.printStackTrace();
            log.error("데이터 조회 오류가 발생했습니다." + e.getMessage(), e);
            throw new NullPointerException("데이터 조회 오류가 발생했습니다. 서버 관리자에게 문의해주세요" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("알 수 없는 오류가 발생했습니다." + e.getMessage(), e);
            throw new RuntimeException("알 수 없는 오류가 발생했습니다. 서버 관리자에게 문의해주세요" + e.getMessage());
        }
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
            ChatGPTRequestDTO chatGPTRequestDTO = new ChatGPTRequestDTO("gpt-4o", List.of(chatGPTCompletionDTO));
            // System.out.println("chatGPTRequestDTO is  : " + chatGPTRequestDTO);

            // ObjectMapper를 사용하여 DTO를 JSON 문자열로 변환
            // ChatGPT api를 사용하려면 JSON 형태로 통신해야 함
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonPayload = objectMapper.writeValueAsString(chatGPTRequestDTO);

            // 통신을 위한 RestTemplate 구성
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);
//            System.out.println("Request URL: " + promptUrl);
//            System.out.println("Request Headers: " + headers);
//            System.out.println("Request Body: " + jsonPayload);

            ResponseEntity<String> response = chatGPTConfig.restTemplate()
                    .exchange(promptUrl, HttpMethod.POST, requestEntity, String.class);

//            System.out.println("Response Status: " + response.getStatusCode());
//            System.out.println("Response Body: " + response.getBody());


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

        } catch (NoSuchElementException e) {
            e.printStackTrace();
            log.error("데이터 조회 오류가 발생했습니다." + e.getMessage(), e);
            throw new NoSuchElementException("데이터 조회 오류가 발생했습니다. 서버 관리자에게 문의해주세요" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("알 수 없는 오류가 발생했습니다." + e.getMessage(), e);
            throw new RuntimeException("알 수 없는 오류가 발생했습니다. 서버 관리자에게 문의해주세요" + e.getMessage());
        }
    }

}