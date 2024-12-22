package ttakkeun.ttakkeun_server.dto.inquiry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class InquiryResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddResultDTO {
        Long inquiryId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class getResultDTO {
        String contents;
        String email;
        String inquiryType;
        List<String> imageUrl;
        LocalDateTime created_at;
    }
}
