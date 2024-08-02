package ttakkeun.ttakkeun_server.service.PetProfileService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.converter.PetProfileConverter;
import ttakkeun.ttakkeun_server.dto.PetProfileRequestDTO;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.repository.PetProfileRepository;

@Service
@RequiredArgsConstructor
public class PetProfileCommandServiceImpl implements PetProfileCommandService {

    private final PetProfileRepository petProfileRepository;

    @Override
    @Transactional
    public Pet add(PetProfileRequestDTO.AddDTO request) {

        Pet newPet = PetProfileConverter.toPet(request);

        return petProfileRepository.save(newPet);
    }
}
