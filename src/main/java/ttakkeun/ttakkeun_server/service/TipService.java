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
import ttakkeun.ttakkeun_server.repository.LikeTipRepository;
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
    private final LikeTipRepository likeTipRepository;

    // 팁 생성
    @Transactional
    public TipResponseDTO createTip(TipCreateRequestDTO request, Long memberId, List<MultipartFile> imageFiles) {
        Member member = memberService.findMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));

        Tip tip = Tip.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .member(member)
                .build();


        tipRepository.save(tip);


        return new TipResponseDTO(
                tip.getTipId(),
                tip.getCategory(),
                tip.getTitle(),
                tip.getContent(),
                tip.getRecommendCount(),
                tip.getCreatedAt(),
                tip.getImages().stream()
                        .map(TipImage::getTipImageUrl)
                        .collect(Collectors.toList()),
                member.getUsername(),
                false
        );
    }


    // 팁 생성시 이미지 업로드
    @Transactional
    public List<TipImage> uploadTipImages(Long tipId, Long memberId, List<MultipartFile> images) {
        Tip tip = tipRepository.findById(tipId)
                .orElseThrow(() -> new IllegalArgumentException("Tip을 찾을 수 없습니다: " + tipId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member를 찾을 수 없습니다: " + memberId));

        if (!tip.getMember().getMemberId().equals(memberId)) {
            throw new IllegalStateException("권한이 없습니다.");
        }

        List<TipImage> tipImages = images.stream()
                .map(image -> {
                    String imageUrl = s3ImageService.upload(image);
                    TipImage tipImage = TipImage.builder()
                            .tipImageUrl(imageUrl)
                            .tip(tip)
                            .build();
                    tip.addImage(tipImage);
                    return tipImage;
                })
                .collect(Collectors.toList());

        tipRepository.save(tip);
        return tipImages;
    }


    // 카테고리별 팁 조회
    @Transactional(readOnly = true)
    public List<TipResponseDTO> getTipsByCategory(Category category, int page, int size, Member member) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Tip> tipsPage = tipRepository.findByCategory(category, pageable);

        return tipsPage.stream()
                .map(tip -> new TipResponseDTO(
                        tip.getTipId(),
                        tip.getCategory(),
                        tip.getTitle(),
                        tip.getContent(),
                        tip.getRecommendCount(),
                        tip.getCreatedAt(),
                        tip.getImages().stream().map(TipImage::getTipImageUrl).collect(Collectors.toList()),
                        tip.getMember().getUsername(),
                        likeTipRepository.existsByTipAndMember(tip, member)
                ))
                .collect(Collectors.toList());
    }


    // 전체 카테고리 조회
    @Transactional(readOnly = true)
    public List<TipResponseDTO> getAllTips(int page, int size, Member member) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Tip> tipsPage = tipRepository.findAll(pageable);

        return tipsPage.stream()
                .map(tip -> new TipResponseDTO(
                        tip.getTipId(),
                        tip.getCategory(),
                        tip.getTitle(),
                        tip.getContent(),
                        tip.getRecommendCount(),
                        tip.getCreatedAt(),
                        tip.getImages().stream().map(TipImage::getTipImageUrl).collect(Collectors.toList()),
                        tip.getMember().getUsername(),
                        likeTipRepository.existsByTipAndMember(tip, member)
                ))
                .collect(Collectors.toList());
    }


    // Best 카테고리 조회
    @Transactional(readOnly = true)
    public List<TipResponseDTO> getBestTips(Member member) {
        List<Tip> topTips = tipRepository.findByIsPopularTrue();

        return topTips.stream()
                .map(tip -> new TipResponseDTO(
                        tip.getTipId(),
                        tip.getCategory(),
                        tip.getTitle(),
                        tip.getContent(),
                        tip.getRecommendCount(),
                        tip.getCreatedAt(),
                        tip.getImages().stream().map(TipImage::getTipImageUrl).collect(Collectors.toList()),
                        tip.getMember().getUsername(),
                        likeTipRepository.existsByTipAndMember(tip, member)
                ))
                .collect(Collectors.toList());
    }
}





