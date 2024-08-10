package ttakkeun.ttakkeun_server.dto.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class RecordResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoadQuestionResultDTO {
        String category;
        List<QuestionDTO> questions;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionDTO {
        Long questionId;
        String questionText;
    }
}
