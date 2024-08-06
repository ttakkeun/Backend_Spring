package ttakkeun.ttakkeun_server.service.PetProfileService;

import ttakkeun.ttakkeun_server.dto.pet.PetProfileRequestDTO;
import ttakkeun.ttakkeun_server.entity.Pet;

public interface PetProfileCommandService {
    Pet add(PetProfileRequestDTO.AddDTO request);
}
