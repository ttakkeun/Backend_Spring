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

    private final PetService petService;
    private final LikeTipService likeTipService;
    private final LikeService likeService;
    private final TipRepository tipRepository;
    private final ScrapTipService scrapTipService;

    public Optional<Member> findMemberById(Long id) {
        return memberRepository.findById(id);
    }

//    public void deleteMember(Member member) {
//        //펫 삭제
//        deletePet(member);
//        //좋아요 누른 팁 삭제
//        deleteLikeTip(member);
//        //좋아요 누른 상품 삭제
//        deleteLikeProduct(member);
//        //팁 작성자 알 수 없음으로 변경
//        removeTipAuthor(member);
//        //스크랩한 팁 삭제
//        deleteScrapTip(member);
//
//        //멤버 삭제
//        memberRepository.deleteById(member.getMemberId());
//    }

    public void deleteMember(Member member) {
        try {
            log.info("Deleting pet for member: {}", member.getMemberId());
            deletePet(member);

            log.info("Deleting liked tips for member: {}", member.getMemberId());
            deleteLikeTip(member);

            log.info("Deleting liked products for member: {}", member.getMemberId());
            deleteLikeProduct(member);

            log.info("Removing tip author for member: {}", member.getMemberId());
            removeTipAuthor(member);

            log.info("Deleting scrap tips for member: {}", member.getMemberId());
            deleteScrapTip(member);

            log.info("Deleting member: {}", member.getMemberId());
            memberRepository.deleteById(member.getMemberId());

        } catch (Exception e) {
            log.error("Error deleting member: {}", member.getMemberId(), e);
            throw e;  // 예외를 다시 던져서 처리할 수 있습니다.
        }
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
        scrapTipService.deleteAllByMember(member);
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


    public Member getMemberInfo(Long memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    public void updateUsername(Long memberId, String newUsername) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        member.changeUsername(newUsername); // 엔티티의 닉네임 변경 메서드 호출
    }
}
