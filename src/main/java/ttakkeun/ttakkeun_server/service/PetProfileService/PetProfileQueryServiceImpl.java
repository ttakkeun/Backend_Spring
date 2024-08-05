package ttakkeun.ttakkeun_server.service.PetProfileService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus;
import ttakkeun.ttakkeun_server.apiPayLoad.exception.handler.TempHandler;
import ttakkeun.ttakkeun_server.dto.PetProfileResponseDTO;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.entity.enums.Neutralization;
import ttakkeun.ttakkeun_server.repository.PetProfileRepository;

@Service
@RequiredArgsConstructor
public class PetProfileQueryServiceImpl implements PetProfileQueryService {
    private final PetProfileRepository petProfileRepository;

    @Override
    public PetProfileResponseDTO.LoadResultDTO load(Long petId) {
        Pet pet = petProfileRepository.findById(petId)
                .orElseThrow(() -> new TempHandler(ErrorStatus.PET_ID_NOT_AVAILABLE));

        return PetProfileResponseDTO.LoadResultDTO.builder()
                .petName(pet.getPetName())
                .petImageUrl(pet.getPetImageUrl())
                .petType(pet.getPetType().name())
                .petVariety(pet.getPetVariety())
                .birth(pet.getBirth())
                .neutralization(pet.getNeutralization() == Neutralization.NEUTRALIZATION)
                .build();
    }
}
