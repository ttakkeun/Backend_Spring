package ttakkeun.ttakkeun_server.dto.diagnose;

import lombok.Builder;

import java.util.List;

@Builder
public record GetMyDiagnoseResponseDTO(Long diagnose_id, Integer score, String result_detail, String after_care, List<ProductDTO> ai_products) {
}
