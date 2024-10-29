package ttakkeun.ttakkeun_server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.repository.MemberRepository;
import ttakkeun.ttakkeun_server.repository.PetRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PetRepository petRepository;

    private final PetService petService;

    public Optional<Member> findMemberById(Long id) {
        return memberRepository.findById(id);
    }

    public void deleteMember(Member member) {
        List<Pet> petList = petRepository.findByMemberId(member.getMemberId());
        for (Pet pet : petList) {
            petService.deletePet(pet.getPetId());
        }

        memberRepository.deleteById(member.getMemberId());
    }
}
