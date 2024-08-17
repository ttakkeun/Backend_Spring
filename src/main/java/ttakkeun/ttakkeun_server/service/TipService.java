package ttakkeun.ttakkeun_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ttakkeun.ttakkeun_server.dto.tip.TipCreateRequestDTO;
import ttakkeun.ttakkeun_server.dto.tip.TipResponseDTO;

import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.Tip;

import ttakkeun.ttakkeun_server.entity.TipImage;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.repository.MemberRepository;
import ttakkeun.ttakkeun_server.repository.TipRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TipService {

    private final TipRepository tipRepository;
    private final MemberService memberService;
    private final S3ImageService s3ImageService;
    private final MemberRepository memberRepository;

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

    @Transactional
    public String uploadTipImage(Long tipId, Long memberId, MultipartFile multipartFile) {
        Tip tip = tipRepository.findById(tipId)
                .orElseThrow(() -> new IllegalArgumentException("Tip을 찾을 수 없습니다: " + tipId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member를 찾을 수 없습니다: " + memberId));

        if (!tip.getMember().getMemberId().equals(memberId)) {
            throw new IllegalStateException("권한이 없습니다.");
        }

        String imageUrl = s3ImageService.upload(multipartFile);

        TipImage tipImage = TipImage.builder()
                .tipImageUrl(imageUrl)
                .tip(tip)
                .build();
        tip.addImage(tipImage);
        tipRepository.save(tip);

        return imageUrl;
    }

    @Transactional(readOnly = true)
    public List<TipResponseDTO> getTipsByCategory(Category category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Tip> tipsPage = tipRepository.findByTipCategory(category, pageable);

        return tipsPage.stream()
                .map(tip -> new TipResponseDTO(
                        tip.getTipId(),
                        tip.getTipCategory(),
                        tip.getTitle(),
                        tip.getContent(),
                        tip.getRecommendCount(),
                        tip.getCreatedAt(),
                        tip.getImages().stream().map(TipImage::getTipImageUrl).collect(Collectors.toList()),
                        tip.getMember().getUsername(),
                        tip.getIsPopular()
                ))
                .collect(Collectors.toList());
    }
    }
