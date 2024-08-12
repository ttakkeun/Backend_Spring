package ttakkeun.ttakkeun_server.dto.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ttakkeun.ttakkeun_server.entity.enums.Category;

import java.util.List;


public class RecordRequestDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerDTO {
        private Long questionId;
        private String answerText;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecordRegisterDTO {
        private Category category;
        private List<AnswerDTO> answers;
    }
}
