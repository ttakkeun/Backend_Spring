package ttakkeun.ttakkeun_server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.entity.LikeTip;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.entity.Tip;
import ttakkeun.ttakkeun_server.repository.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final PointRepository pointRepository;

    private final PetService petService;
    private final LikeTipService likeTipService;
    private final LikeService likeService;
    private final TipRepository tipRepository;

    public Optional<Member> findMemberById(Long id) {
        return memberRepository.findById(id);
    }

    public void deleteMember(Member member) {
        //펫 삭제
        deletePet(member);
        //좋아요 누른 팁 삭제
        deleteLikeTip(member);
        //좋아요 누른 상품 삭제
        deleteLikeProduct(member);
        //팁 작성자 알 수 없음으로 변경
        removeTipAuthor(member);
        //스크랩한 팁 삭제(미완성)
        deleteScrapTip(member);

        //멤버 삭제
        memberRepository.deleteById(member.getMemberId());
    }

    public void deletePet(Member member) {
        List<Pet> petList = petRepository.findByMemberId(member.getMemberId());
        for (Pet pet : petList) {
            petService.deletePet(pet.getPetId());
        }
    }

    public void deleteLikeTip(Member member) {
        likeTipService.deleteAllByMember(member);
    }

    public void deleteScrapTip(Member member) {
        //ScrapTipService.deleteAllByMember(member);
    }

    public void removeTipAuthor(Member member) {
        List<Tip> tips = tipRepository.findByMember(member);
        for (Tip tip : tips) {
            tip.setMember(null);
        }
        tipRepository.saveAll(tips);
    }

    public void deleteLikeProduct(Member member) {
        likeService.deleteAllByMember(member);
    }
}
