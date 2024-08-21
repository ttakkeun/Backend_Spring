package ttakkeun.ttakkeun_server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.entity.LikeTip;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.Tip;
import ttakkeun.ttakkeun_server.repository.LikeTipRepository;
import ttakkeun.ttakkeun_server.repository.MemberRepository;
import ttakkeun.ttakkeun_server.repository.TipRepository;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeTipService {

    private final LikeTipRepository likeTipRepository;
    private final TipRepository tipRepository;
    private final MemberRepository memberRepository;

    // 좋아요 버튼 토글 기능
    public void toggleLikeTip(Long tipId, Long memberId) {
        Tip tip = tipRepository.findById(tipId)
                .orElseThrow(() -> new IllegalArgumentException("Tip not found with id: " + tipId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        Optional<LikeTip> tipLikeOpt = likeTipRepository.findByTipIdAndMemberId(tipId, memberId);

        if (tipLikeOpt.isPresent()) {
            likeTipRepository.delete(tipLikeOpt.get());
        } else {
            LikeTip likeTip = new LikeTip(null, tip, member);
            likeTipRepository.save(likeTip);
        }
    }

    // 좋아요 수 반환
    public int getTotalTipLikes(Long tipId) {
        Tip tip = tipRepository.findById(tipId)
                .orElseThrow(() -> new IllegalArgumentException("Tip not found with id: " + tipId));
        return likeTipRepository.countByTip(tip);
    }

    // 좋아요 상태 반환
    public boolean getTipLikeStatus(Long tipId, Long memberId) {
        Tip tip = tipRepository.findById(tipId)
                .orElseThrow(() -> new IllegalArgumentException("Tip not found with id: " + tipId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));
        return likeTipRepository.existsByTipAndMember(tip, member);
    }
}
