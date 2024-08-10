package ttakkeun.ttakkeun_server.service.PetService;

import ttakkeun.ttakkeun_server.dto.pet.PetRequestDTO;
import ttakkeun.ttakkeun_server.entity.Pet;

public interface PetCommandService {
    Pet add(PetRequestDTO.AddDTO request);
}
