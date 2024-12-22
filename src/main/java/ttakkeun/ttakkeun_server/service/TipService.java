package ttakkeun.ttakkeun_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ttakkeun.ttakkeun_server.apiPayLoad.exception.ExceptionHandler;
import ttakkeun.ttakkeun_server.converter.TipConverter;
import ttakkeun.ttakkeun_server.dto.tip.PostTipReportRequestDTO;
import ttakkeun.ttakkeun_server.dto.tip.PostTipReportResponseDTO;
import ttakkeun.ttakkeun_server.dto.tip.TipCreateRequestDTO;
import ttakkeun.ttakkeun_server.dto.tip.TipResponseDTO;

import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.ReportImage;
import ttakkeun.ttakkeun_server.entity.ReportTip;
import ttakkeun.ttakkeun_server.entity.ScrapTip;
import ttakkeun.ttakkeun_server.entity.Tip;

import ttakkeun.ttakkeun_server.entity.TipImage;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.repository.LikeTipRepository;
import ttakkeun.ttakkeun_server.repository.MemberRepository;
import ttakkeun.ttakkeun_server.repository.ReportImageRepository;
import ttakkeun.ttakkeun_server.repository.ReportTipRepository;
import ttakkeun.ttakkeun_server.repository.ScrapTipRepository;
import ttakkeun.ttakkeun_server.repository.TipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus.TIP_ID_NOT_AVAILABLE;


@Service
@RequiredArgsConstructor
public class TipService {

    private final TipRepository tipRepository;
    private final MemberService memberService;
    private final S3ImageService s3ImageService;
    private final MemberRepository memberRepository;
    private final LikeTipRepository likeTipRepository;
    private final ScrapTipService scrapTipService;
    private final ScrapTipRepository scrapTipRepository;
    private final TipConverter tipConverter;
    private final ReportTipRepository reportTipRepository;
    private final ReportImageRepository reportImageRepository;

    // 팁 생성
    @Transactional
    public TipResponseDTO createTip(TipCreateRequestDTO request, Long memberId) {
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
                tip.getMember().getUsername(),
                false,
                tip.isPopular(),
                false
        );
    }

    // 베스트 팁 업데이트
    @Transactional
    public void updateBestTips() {
        // 기존 베스트 팁들을 모두 일반 팁으로 변경
        List<Tip> currentBestTips = tipRepository.findByIsPopularTrue();
        currentBestTips.forEach(tip -> tip.setPopular(false));

        // 최신 10개의 팁을 베스트 팁으로 설정
        List<Tip> latestTips = tipRepository.findTop10ByOrderByCreatedAt();
        latestTips.forEach(tip -> tip.setPopular(true));

        tipRepository.saveAll(currentBestTips);
        tipRepository.saveAll(latestTips);
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


        return tipConverter.toDTO(tipsPage, member);
//        return tipsPage.stream()
//                .map(tip -> new TipResponseDTO(
//                        tip.getTipId(),
//                        tip.getCategory(),
//                        tip.getTitle(),
//                        tip.getContent(),
//                        tip.getRecommendCount(),
//                        tip.getCreatedAt(),
//                        tip.getImages().stream().map(TipImage::getTipImageUrl).collect(Collectors.toList()),
//                        tip.getMember() != null ? tip.getMember().getUsername() : "알수없음",
//                        likeTipRepository.existsByTipAndMember(tip, member),
//                        tip.isPopular()
//                ))
//                .collect(Collectors.toList());
    }

    // 전체 카테고리 조회
    @Transactional(readOnly = true)
    public List<TipResponseDTO> getAllTips(int page, int size, Member member) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Tip> tipsPage = tipRepository.findAll(pageable);

        return tipConverter.toDTO(tipsPage, member);
//        return tipsPage.stream()
//                .map(tip -> new TipResponseDTO(
//                        tip.getTipId(),
//                        tip.getCategory(),
//                        tip.getTitle(),
//                        tip.getContent(),
//                        tip.getRecommendCount(),
//                        tip.getCreatedAt(),
//                        tip.getImages().stream().map(TipImage::getTipImageUrl).collect(Collectors.toList()),
//                        tip.getMember().getUsername(),
//                        likeTipRepository.existsByTipAndMember(tip, member),
//                        tip.isPopular()
//                ))
//                .collect(Collectors.toList());
    }

    // Best 카테고리 조회
    @Transactional(readOnly = true)
    public List<TipResponseDTO> getBestTips(Member member) {
        List<Tip> topTips = tipRepository.findByIsPopularTrue();

        return tipConverter.toDTO(topTips, member);
//        return topTips.stream()
//                .map(tip -> new TipResponseDTO(
//                        tip.getTipId(),
//                        tip.getCategory(),
//                        tip.getTitle(),
//                        tip.getContent(),
//                        tip.getRecommendCount(),
//                        tip.getCreatedAt(),
//                        tip.getImages().stream().map(TipImage::getTipImageUrl).collect(Collectors.toList()),
//                        tip.getMember().getUsername(),
//                        likeTipRepository.existsByTipAndMember(tip, member),
//                        tip.isPopular()
//                ))
//                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteTip(Long tipId) {
        // 먼저 해당 팁이 존재하는지 확인
        Tip tip = tipRepository.findById(tipId)
                .orElseThrow(() -> new ExceptionHandler(TIP_ID_NOT_AVAILABLE));

        // S3 이미지 삭제
        tip.getImages().forEach(image -> s3ImageService.delete(image.getTipImageUrl()));

        // LikeTip 관련 데이터 삭제
        likeTipRepository.deleteAllByTip(tip);

        // 팁 삭제
        tipRepository.delete(tip);
    }

    //내가 작성한 팁 가져오기 (페이지 당 10개)
    @Transactional
    public List<TipResponseDTO> getMyTips(Member member, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Tip> myTips = tipRepository.findByMember(member, pageable).getContent();

        return tipConverter.toDTO(myTips, member);
//        return myTips.stream()
//                .map(tip -> new TipResponseDTO(
//                        tip.getTipId(),
//                        tip.getCategory(),
//                        tip.getTitle(),
//                        tip.getContent(),
//                        tip.getRecommendCount(),
//                        tip.getCreatedAt(),
//                        tip.getImages().stream().map(TipImage::getTipImageUrl).collect(Collectors.toList()),
//                        tip.getMember().getUsername(),
//                        likeTipRepository.existsByTipAndMember(tip, member),
//                        tip.isPopular()
//                ))
//                .collect(Collectors.toList());
    }

    //스크랩 한 팁 가져오기 (페이지 당 10개)
    public List<TipResponseDTO> getScrapTips(Member member, int page) {
        Pageable pageable = PageRequest.of(page, 10);
        List<ScrapTip> ScrapTips = scrapTipRepository.findByMember(member, pageable).getContent();
        List<Tip> Tips = ScrapTips.stream().map(ScrapTip::getTip).toList();

        return tipConverter.toDTO(Tips, member);
//        return Tips.stream()
//                .map(tip -> new TipResponseDTO(
//                        tip.getTipId(),
//                        tip.getCategory(),
//                        tip.getTitle(),
//                        tip.getContent(),
//                        tip.getRecommendCount(),
//                        tip.getCreatedAt(),
//                        tip.getImages().stream().map(TipImage::getTipImageUrl).collect(Collectors.toList()),
//                        tip.getMember().getUsername(),
//                        likeTipRepository.existsByTipAndMember(tip, member),
//                        tip.isPopular()
//                ))
//                .collect(Collectors.toList());
    }

    // 팁 게시글 신고하기
    public boolean postTipReport(Member member, PostTipReportRequestDTO postTipReportRequestDTO, List<MultipartFile> multipartFile) {
        Long tipId = postTipReportRequestDTO.getTip_id();
        Tip tip = tipRepository.findById(tipId)
            .orElseThrow(() -> new NoSuchElementException("Tip with ID " + tipId + " not found"));

        ReportTip reportTip = ReportTip.builder()
            .tip(tip) // 신고 대상인 팁 게시글
            .member(member) // 신고를 접수한 회원
            .reportCategory(postTipReportRequestDTO.getReport_category())
            .reportDetail(postTipReportRequestDTO.getReport_detail())
            .build();

        reportTipRepository.save(reportTip);

        if (multipartFile != null && !multipartFile.isEmpty()) {
            List<ReportImage> reportImages = multipartFile.stream()
                .filter(image -> !image.isEmpty())
                .map(image -> {
                    String reportImageUrl = s3ImageService.upload(image);
                    return ReportImage.builder()
                        .imageUrl(reportImageUrl)
                        .reportTip(reportTip)
                        .build();
                })
                .collect(Collectors.toList());

            reportImageRepository.saveAll(reportImages);
        }

        return true;
    }

}





