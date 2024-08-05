package ttakkeun.ttakkeun_server.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProductApiResponseDTO {
    private Boolean isSuccess;
    private int code;
    private String message;
    private List<ProductDTO> result;
}
