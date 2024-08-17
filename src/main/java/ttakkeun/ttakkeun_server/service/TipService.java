package ttakkeun.ttakkeun_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ttakkeun.ttakkeun_server.dto.tip.TipCreateRequestDTO;
import ttakkeun.ttakkeun_server.dto.tip.TipResponseDTO;

import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.Tip;

import ttakkeun.ttakkeun_server.entity.TipImage;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.repository.TipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TipService {

    private final TipRepository tipRepository;
    private final MemberService memberService;
    private final S3ImageService s3ImageService;

    @Transactional
    public TipResponseDTO createTip(TipCreateRequestDTO request, Long memberId, List<MultipartFile> imageFiles) {
        Member member = memberService.findMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));

        Tip tip = Tip.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .tipCategory(request.getTipCategory())
                .member(member)
                .build();

        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (MultipartFile file : imageFiles) {
                String imageUrl = s3ImageService.upload(file);
                TipImage tipImage = TipImage.builder()
                        .tipImageUrl(imageUrl)
                        .tip(tip)
                        .build();
                tip.addImage(tipImage);
            }
        }

        tipRepository.save(tip);

        return new TipResponseDTO(
                tip.getTipId(),
                tip.getTipCategory(),
                tip.getTitle(),
                tip.getContent(),
                tip.getRecommendCount(),
                tip.getCreatedAt(),
                tip.getImages().stream()
                        .map(TipImage::getTipImageUrl)
                        .collect(Collectors.toList()),
                member.getUsername(),
                tip.getIsPopular()
        );
    }

    @Transactional(readOnly = true)
    public List<TipResponseDTO> getTipsByCategory(Category category) {
        List<Tip> tips = tipRepository.findByTipCategory(category);

        List<TipResponseDTO> tipResponseDTOS = new ArrayList<>();
        for (Tip tip : tips) {
            TipResponseDTO dto = new TipResponseDTO(
                    tip.getTipId(),
                    tip.getTipCategory(),
                    tip.getTitle(),
                    tip.getContent(),
                    tip.getRecommendCount(),
                    tip.getCreatedAt(),
                    tip.getImages().stream().map(image -> image.getTipImageUrl()).collect(Collectors.toList()),
                    tip.getMember().getUsername(),
                    tip.getIsPopular()
            );
            tipResponseDTOS.add(dto);
        }
        return tipResponseDTOS;
    }

}