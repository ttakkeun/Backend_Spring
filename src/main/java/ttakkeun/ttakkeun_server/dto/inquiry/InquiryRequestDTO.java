package ttakkeun.ttakkeun_server.dto.inquiry;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import ttakkeun.ttakkeun_server.entity.enums.InquiryType;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryRequestDTO {
    @NotNull
    private String contents;

    @NotNull
    private String email;

    @NotNull
    private InquiryType inquiryType;
}
