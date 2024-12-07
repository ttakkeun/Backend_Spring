package ttakkeun.ttakkeun_server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.dto.tip.ScrapTipResponseDTO;
import ttakkeun.ttakkeun_server.entity.*;
import ttakkeun.ttakkeun_server.repository.MemberRepository;
import ttakkeun.ttakkeun_server.repository.ScrapTipRepository;
import ttakkeun.ttakkeun_server.repository.TipRepository;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ScrapTipService {
    private final ScrapTipRepository scrapTipRepository;
    private final TipRepository tipRepository;
    private final MemberRepository memberRepository;

    //사용자의 스크랩 여부 반환
    public ScrapTipResponseDTO getScrapStatus(Long tipId, Member member) {
        Tip tip = getTipById(tipId);

        return ScrapTipResponseDTO.builder()
                .isScrap(scrapTipRepository.existsByTipAndMember(tip, member))
                .build();
    }

    //스크랩 버튼 토글 기능
    public void toggleScrapTip(Long tipId, Member member) {
        Tip tip = getTipById(tipId);

        Optional<ScrapTip> scrapTipOpt = scrapTipRepository.findByTipAndMember(tip, member);

        if(scrapTipOpt.isPresent()) { //이미 존재할 경우 좋아요 테이블에서 삭제
            scrapTipRepository.delete(scrapTipOpt.get());
        } else { //존재하지 않을 경우 테이블에 추가
            ScrapTip scrapTip = new ScrapTip(null, tip, member);
            scrapTipRepository.save(scrapTip);
        }
    }

    //id에서 tip객체 받아오기
    private Tip getTipById(Long tipId) {
        return tipRepository.findById(tipId)
                .orElseThrow(() -> new IllegalArgumentException("Tip not found with id: " + tipId));
    }

    //id에서 member객체 받아오기
    private Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));
    }

    public void deleteAllByMember(Member member) {
        scrapTipRepository.deleteByMember(member);
    }
}