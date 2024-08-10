package ttakkeun.ttakkeun_server.dto.pet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectDTO {
        Long petId;
        String petName;
        String petImageUrl;
        String petType;
        String birth;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectResultDTO {
        List<SelectDTO> result;
    }

}
