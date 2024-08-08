package ttakkeun.ttakkeun_server.dto.pet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PetResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddResultDTO {
        Long petId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoadResultDTO {
        String petName;
        String petImageUrl;
        String petType;
        String petVariety;
        String birth;
        Boolean neutralization;
    }

}
