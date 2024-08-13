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
        String descriptionText;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterResultDTO {
        private Long recordId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailResultDTO {
        String question1;
        String answer1;
        List<String> image1;
        String question2;
        String answer2;
        List<String> image2;
        String question3;
        String answer3;
        List<String> image3;
        String etc;
    }
}
