package ttakkeun.ttakkeun_server.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ttakkeun.ttakkeun_server.dto.tip.TipResponseDTO;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.Tip;
import ttakkeun.ttakkeun_server.entity.TipImage;
import ttakkeun.ttakkeun_server.repository.LikeTipRepository;
import ttakkeun.ttakkeun_server.repository.ScrapTipRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TipConverter {

    private final LikeTipRepository likeTipRepository;
    private final ScrapTipRepository scrapTipRepository;

    //Page<Tip> -> TipResponseDTO
    public List<TipResponseDTO> toDTO(Page<Tip> tips, Member member) {
        return tips.stream()
                .map(tip -> new TipResponseDTO(
                        tip.getTipId(),
                        tip.getCategory(),
                        tip.getTitle(),
                        tip.getContent(),
                        tip.getRecommendCount(),
                        tip.getCreatedAt(),
                        tip.getImages().stream().map(TipImage::getTipImageUrl).collect(Collectors.toList()),
                        tip.getMember() != null ? tip.getMember().getUsername() : "알수없음",
                        likeTipRepository.existsByTipAndMember(tip, member),
                        tip.isPopular(),
                        scrapTipRepository.existsByTipAndMember(tip, member)
                ))
                .collect(Collectors.toList());
    }

    //List<Tip> -> TipResponseDTO
    public List<TipResponseDTO> toDTO(List<Tip> tips, Member member) {
        return tips.stream()
                .map(tip -> new TipResponseDTO(
                        tip.getTipId(),
                        tip.getCategory(),
                        tip.getTitle(),
                        tip.getContent(),
                        tip.getRecommendCount(),
                        tip.getCreatedAt(),
                        tip.getImages().stream().map(TipImage::getTipImageUrl).collect(Collectors.toList()),
                        tip.getMember() != null ? tip.getMember().getUsername() : "알수없음",
                        likeTipRepository.existsByTipAndMember(tip, member),
                        tip.isPopular(),
                        scrapTipRepository.existsByTipAndMember(tip, member)
                ))
                .collect(Collectors.toList());
    }
}
