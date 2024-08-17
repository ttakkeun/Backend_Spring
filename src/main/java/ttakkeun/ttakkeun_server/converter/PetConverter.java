package ttakkeun.ttakkeun_server.converter;

import ttakkeun.ttakkeun_server.dto.pet.PetRequestDTO;
import ttakkeun.ttakkeun_server.dto.pet.PetResponseDTO;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.entity.enums.Neutralization;
import ttakkeun.ttakkeun_server.entity.enums.PetType;

public class PetConverter {

    public static PetResponseDTO.AddResultDTO toAddResultDTO(Pet pet) {
        return PetResponseDTO.AddResultDTO.builder()
                .petId(pet.getPetId())
                .build();
    }

    public static Pet toPet(PetRequestDTO.AddDTO request, Member member) {

        PetType petType = switch (request.getType()) {
            case "CAT" -> PetType.CAT;
            case "DOG" -> PetType.DOG;
            default -> null;
        };


        Neutralization neutralization = request.getNeutralization()
                ? Neutralization.NEUTRALIZATION
                : Neutralization.UNNEUTRALIZATION;


        return Pet.builder()
                .petName(request.getName())
                .petType(petType)
                .petVariety(request.getVariety())
                .birth(request.getBirth())
                .neutralization(neutralization)
                .member(member)
                .build();
    }

    public static PetResponseDTO.SelectDTO toSelectDTO(Pet pet) {
        return PetResponseDTO.SelectDTO.builder()
                .petId(pet.getPetId())
                .petName(pet.getPetName())
                .petImageUrl(pet.getPetImageUrl())
                .petType(pet.getPetType().name())
                .birth(pet.getBirth())
                .build();
    }
}
