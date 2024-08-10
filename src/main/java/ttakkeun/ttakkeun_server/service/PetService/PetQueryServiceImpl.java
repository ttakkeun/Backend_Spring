package ttakkeun.ttakkeun_server.service.PetService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus;
import ttakkeun.ttakkeun_server.apiPayLoad.exception.handler.TempHandler;
import ttakkeun.ttakkeun_server.dto.pet.PetResponseDTO;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.entity.enums.Neutralization;
import ttakkeun.ttakkeun_server.repository.PetRepository;

@Service
@RequiredArgsConstructor
public class PetQueryServiceImpl implements PetQueryService {
    private final PetRepository petRepository;

    @Override
    public PetResponseDTO.LoadResultDTO load(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new TempHandler(ErrorStatus.PET_ID_NOT_AVAILABLE));

        return PetResponseDTO.LoadResultDTO.builder()
                .petName(pet.getPetName())
                .petImageUrl(pet.getPetImageUrl())
                .petType(pet.getPetType().name())
                .petVariety(pet.getPetVariety())
                .birth(pet.getBirth())
                .neutralization(pet.getNeutralization() == Neutralization.NEUTRALIZATION)
                .build();
    }
}
