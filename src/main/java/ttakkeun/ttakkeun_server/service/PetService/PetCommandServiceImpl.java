package ttakkeun.ttakkeun_server.service.PetService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.converter.PetConverter;
import ttakkeun.ttakkeun_server.dto.pet.PetRequestDTO;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.repository.PetRepository;

@Service
@RequiredArgsConstructor
public class PetCommandServiceImpl implements PetCommandService {

    private final PetRepository petRepository;

    @Override
    @Transactional
    public Pet add(PetRequestDTO.AddDTO request, Member member) {

        Pet newPet = PetConverter.toPet(request, member);

        return petRepository.save(newPet);
    }
}
