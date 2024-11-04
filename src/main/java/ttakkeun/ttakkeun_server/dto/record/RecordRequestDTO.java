package ttakkeun.ttakkeun_server.dto.record;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import ttakkeun.ttakkeun_server.entity.enums.Category;

import java.util.List;


public class RecordRequestDTO {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerDTO {
        private Long questionId;
        private List<String> answerText;
        private List<MultipartFile> images;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecordRegisterDTO {
        private Category category;
        private List<AnswerDTO> answers;
        private String etc;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecordImageDTO {
        private Long imageId;
        private String imageUrl;
    }
}
