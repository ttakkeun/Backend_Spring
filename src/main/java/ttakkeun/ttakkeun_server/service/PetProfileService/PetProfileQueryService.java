package ttakkeun.ttakkeun_server.service.PetProfileService;

import ttakkeun.ttakkeun_server.dto.pet.PetProfileResponseDTO;

public interface PetProfileQueryService {
    PetProfileResponseDTO.LoadResultDTO load(Long petId);
}
