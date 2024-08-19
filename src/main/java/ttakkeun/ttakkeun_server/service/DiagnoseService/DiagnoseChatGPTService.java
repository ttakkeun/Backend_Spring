package ttakkeun.ttakkeun_server.service.DiagnoseService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.dto.diagnose.ChatGPTQuestionDTO;
import ttakkeun.ttakkeun_server.dto.diagnose.PostDiagnoseRequestDTO;
import ttakkeun.ttakkeun_server.dto.diagnose.PostDiagnoseResponseDTO;
import ttakkeun.ttakkeun_server.entity.*;
import ttakkeun.ttakkeun_server.entity.Record;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.entity.enums.Neutralization;
import ttakkeun.ttakkeun_server.entity.enums.PetType;
import ttakkeun.ttakkeun_server.repository.PetRepository;
import ttakkeun.ttakkeun_server.repository.RecordRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Objects;

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

    @Autowired
    public DiagnoseChatGPTService(PetRepository petRepository, RecordRepository recordRepository) {
        this.petRepository = petRepository;
        this.recordRepository = recordRepository;
    }

    public PostDiagnoseResponseDTO postDiagnoseByRecord(Long memberId, PostDiagnoseRequestDTO postDiagnoseRequestDTO) throws Exception {
        Long petId = postDiagnoseRequestDTO.pet_id(); // request body에서 pet_id값을 가져옴
        Optional<Pet> petOpt= petRepository.findByPetIdAndMember_MemberId(petId, memberId);

        if (!petOpt.isPresent()) {
            // Optional 객체에서 값이 비어있는 경우 예외를 던짐
            throw new NoSuchElementException("Pet with ID " + petId + " not found");
        }

        Pet pet = petOpt.get();
        //makeQuestionForGPT(pet, postDiagnoseRequestDTO); // GPT에게 질문할 때 사용할 반려동물 정보, 일지 내용을 가져옴

        ChatGPTQuestionDTO questionByDTO = makeQuestionForGPT(pet, postDiagnoseRequestDTO);

        System.out.println("make question is : " + makeQuestionForGPT(pet, postDiagnoseRequestDTO));

        System.out.println("question string is : " + makeQuestionString(questionByDTO));

        return null;
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
        List<UserAnswer> answerList = record.getAnswerList(); // 일지에 해당하는 답변
        String etc = record.getEtc();

        // 일지에 해당하는 질문 내용과 답변 내용, 이미지 url들을 가져옴
        List<ChatGPTQuestionDTO.AnswerDTO> answerDTOList = answerList.stream()
                .map(this::getAnswerDetail)
                .collect(Collectors.toList());

        return new ChatGPTQuestionDTO.RecordDetailDTO(createdAt, category, answerDTOList, etc);
    }

    // 일지 중 질문, 답변과 관련된 것들을 가져옴
    public ChatGPTQuestionDTO.AnswerDTO getAnswerDetail(UserAnswer answer) {
        // answer를 통해 questionText, descriptionText, answerText, imageURLs를 가져옴

        // answerList 객체에서 가져올 것 : answerText, question 객체, imageList 객체
        // question 객체에서 가져올 것 : questionText, descriptionText
        String answerText = answer.getUserAnswerText();
        ChecklistQuestion question = answer.getQuestion();
        String questionText = question.getQuestionText();
        String descriptionText = question.getDescriptionText();
        List<Image> imageList = answer.getImages(); // 답변과 함께 저장된 images

        // image 객체에서 가져올 것 : imageUrl
        // image 객체 리스트에서 스트림 생성. imageUrl들을 리스트로 수집함
        List<String> imageURLs = imageList.stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());

        return new ChatGPTQuestionDTO.AnswerDTO(questionText, descriptionText, answerText, imageURLs);

    }

    // DTO의 내용으로 구체적인 질문 내용을 만듦
    public String makeQuestionString(ChatGPTQuestionDTO chatGPTQuestionDTO) {

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
            System.out.println("petVariety is : " + petVariety);

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

            System.out.println("question is : " + question );


        } catch(NullPointerException e) {
            log.error("반려동물 정보가 정확히 저장되어 있지 않습니다.", e);
            throw new NullPointerException("반려동물 정보가 정확히 저장되어 있지 않습니다.");
        }

        // 진단에 사용된 일지가 몇개인지 확인
        int recordCount = chatGPTQuestionDTO.recordDetailDTO().size();

        // 일지 개수만큼 반복하며 일지 내용을 question 문장에 추가함
        for (int i = 0; i < recordCount; i++) {
            if (recordCount == 0) {
                throw new IllegalArgumentException("일지가 선택되지 않았습니다. 다시 시도해주세요");
            } else {
                // 답변이 db에 없을 때 오류처리 필요
                question += "다음은 사용자가 기록한 " + i + "번째 일지의 내용이야. 이 일지는 " + chatGPTQuestionDTO.recordDetailDTO().get(i).created_at() + "에 저장되었고, " +
                        chatGPTQuestionDTO.recordDetailDTO().get(i).category() + "에 대한 기록이야. ";
                // 하나의 일지는 3개의 질문을 기록하므로
                for (int j = 0; j < 3; j++) {
                    System.out.println("j is : " + j );
                    System.out.println(chatGPTQuestionDTO.recordDetailDTO().get(i).answerDTO().get(j).questionText());
                    System.out.println(chatGPTQuestionDTO.recordDetailDTO().get(i).answerDTO().get(j).descriptionText());
                    System.out.println(chatGPTQuestionDTO.recordDetailDTO().get(i).answerDTO().get(j).answerText());
                    question += j+1 + "번째 질문은 '" + chatGPTQuestionDTO.recordDetailDTO().get(i).answerDTO().get(j).questionText() + "'이고 " +
                            "추가적인 서비스의 설명은 '" + chatGPTQuestionDTO.recordDetailDTO().get(i).answerDTO().get(j).descriptionText() + "'야. " +
                            "사용자는 이에 대해 '" + chatGPTQuestionDTO.recordDetailDTO().get(i).answerDTO().get(j).answerText() + " '라고 답변했어. ";
                    System.out.println("j is : " + j );
                }
                question += " 마지막으로 사용자는 이 일지에 대해 다음과 같이 추가적으로 기록했어. '" + chatGPTQuestionDTO.recordDetailDTO().get(i).etc() +"' \n";
            }
        }

        System.out.println("question is : " + question);

        return question;
    }


    // 이제 DTO 기반으로 질문 내용 구성하고 GPT랑 통신하기

//    public ChatGPTCompletionDTO diagnoseByChatGPT() {
//        // gpt 진단하는 클래스
//    }
}