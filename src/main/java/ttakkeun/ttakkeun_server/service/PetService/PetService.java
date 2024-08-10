package ttakkeun.ttakkeun_server.service.PetService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.apiPayLoad.ExceptionHandler;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.repository.MemberRepository;
import ttakkeun.ttakkeun_server.repository.PetRepository;

import java.util.List;

import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PetService {
    private final PetRepository petRepository;
    private final MemberRepository memberRepository;

    public List<Pet> getPetsByMemberId(Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new ExceptionHandler(MEMBER_NOT_FOUND));

        return petRepository.findByMemberId(memberId);
    }
}
