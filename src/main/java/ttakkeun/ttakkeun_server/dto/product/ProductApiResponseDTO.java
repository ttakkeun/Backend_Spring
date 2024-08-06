package ttakkeun.ttakkeun_server.dto.product;

import lombok.Builder;
import lombok.Getter;
import ttakkeun.ttakkeun_server.dto.diagnose.ProductDTO;

import java.util.List;

@Getter
@Builder
public class ProductApiResponseDTO {
    private Boolean isSuccess;
    private int code;
    private String message;
    private List<ProductDTO> result;
}
