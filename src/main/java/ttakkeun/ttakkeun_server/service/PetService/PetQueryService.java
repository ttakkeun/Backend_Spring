package ttakkeun.ttakkeun_server.service.PetService;

import ttakkeun.ttakkeun_server.dto.pet.PetResponseDTO;
import ttakkeun.ttakkeun_server.entity.Member;

public interface PetQueryService {
    PetResponseDTO.LoadResultDTO load(Long petId, Member member);
}
