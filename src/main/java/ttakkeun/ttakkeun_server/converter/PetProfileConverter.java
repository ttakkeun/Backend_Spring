package ttakkeun.ttakkeun_server.converter;

import ttakkeun.ttakkeun_server.dto.PetProfileRequestDTO;
import ttakkeun.ttakkeun_server.dto.PetProfileResponseDTO;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.entity.enums.Neutralization;
import ttakkeun.ttakkeun_server.entity.enums.PetType;

public class PetProfileConverter {

    public static PetProfileResponseDTO.AddResultDTO toAddResultDTO(Pet pet) {
        return PetProfileResponseDTO.AddResultDTO.builder()
                .petId(pet.getPetId())
                .build();
    }

    public static Pet toPet(PetProfileRequestDTO.AddDTO request) {

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
