package ttakkeun.ttakkeun_server.service.PetProfileService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Pet ID입니다."));

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
