package ttakkeun.ttakkeun_server.converter;

import ttakkeun.ttakkeun_server.dto.pet.PetRequestDTO;
import ttakkeun.ttakkeun_server.dto.pet.PetResponseDTO;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.entity.enums.Neutralization;
import ttakkeun.ttakkeun_server.entity.enums.PetType;

public class PetConverter {

    public static PetResponseDTO.AddResultDTO toAddResultDTO(Pet pet) {
        return PetResponseDTO.AddResultDTO.builder()
                .petId(pet.getPetId())
                .build();
    }

    public static Pet toPet(PetRequestDTO.AddDTO request) {

        PetType petType = null;
        switch (request.getType()) {
            case "CAT":
                petType = PetType.CAT;
                break;
            case "DOG":
                petType = PetType.DOG;
                break;
        }


        Neutralization neutralization = null;
        if (request.getNeutralization()) {
            neutralization = Neutralization.NEUTRALIZATION;
        } else {
            neutralization = Neutralization.UNNEUTRALIZATION;
        }


        return Pet.builder()
                .petName(request.getName())
                .petType(petType)
                .petVariety(request.getVariety())
                .birth(request.getBirth())
                .neutralization(neutralization)
                .build();
    }
}
