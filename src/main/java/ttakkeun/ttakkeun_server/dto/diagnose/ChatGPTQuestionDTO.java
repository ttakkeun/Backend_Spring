package ttakkeun.ttakkeun_server.dto.diagnose;

import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.entity.enums.Neutralization;
import ttakkeun.ttakkeun_server.entity.enums.PetType;

import java.time.LocalDateTime;
import java.util.List;

public record ChatGPTQuestionDTO(PetType petType, String petVariety, String birth, Neutralization neutralization, List<RecordDetailDTO> recordDetailDTO) {
    public static record RecordDetailDTO(LocalDateTime created_at, Category category, List<AnswerDTO> answerDTO, String etc){

    }

    public static record AnswerDTO(String questionText, String descriptionText, String answerText, List<String> imageURLs){}
}
