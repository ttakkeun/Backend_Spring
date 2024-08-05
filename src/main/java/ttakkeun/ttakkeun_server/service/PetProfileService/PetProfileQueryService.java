package ttakkeun.ttakkeun_server.service.PetProfileService;

import ttakkeun.ttakkeun_server.dto.PetProfileResponseDTO;

public interface PetProfileQueryService {
    PetProfileResponseDTO.LoadResultDTO load(Long petId);
}
