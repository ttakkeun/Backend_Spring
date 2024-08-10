package ttakkeun.ttakkeun_server.service.PetService;

import ttakkeun.ttakkeun_server.dto.pet.PetResponseDTO;

public interface PetQueryService {
    PetResponseDTO.LoadResultDTO load(Long petId);
}
