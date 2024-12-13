package ttakkeun.ttakkeun_server.service.DiagnoseService;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttakkeun.ttakkeun_server.dto.diagnose.*;
import ttakkeun.ttakkeun_server.entity.*;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class DiagnoseService {
    private final PointRepository pointRepository;
    private final ResultRepository resultRepository;
    private final ProductRepository productRepository;
    private final PetRepository petRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public DiagnoseService(PointRepository pointRepository, ResultRepository resultRepository, ProductRepository productRepository,
                           PetRepository petRepository, MemberRepository memberRepository) {
        this.pointRepository = pointRepository;
        this.resultRepository = resultRepository;
        this.productRepository = productRepository;
        this.petRepository = petRepository;
        this.memberRepository = memberRepository;
    }

    // 사용자 포인트 조회
    public Integer getPointsByMember(Long memberId) throws Exception {
        Optional<Point> pointOpt = pointRepository.findByMemberId(memberId);
        Member managedMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));


        Point point;

        if (!pointOpt.isPresent()) {
//            // Optional 객체에서 값이 비어있는 경우 예외를 던짐
//            // 0 반환에서 오류 발생하도록 수정함
//            throw new NoSuchElementException("Member with ID " + memberId + " has no point");


            // 포인트 객체가 없을 경우 객체를 생성함
            point = Point.builder()
                    .member(managedMember)
                    .points(10)
                    .updatedAt(LocalDateTime.now())
                    .build();

            pointRepository.save(point);

        } else {
            point = pointOpt.get(); // Optinal 객체에서 point를 가져옴
        }

        Integer points = point.getPoints();
        LocalDateTime updatedAt = point.getUpdatedAt();

        if (isToday(updatedAt)) { // 최근 포인트 업데이트 날짜가 오늘이라면 그대로 해당 포인트를 반환함
            return points;
        } else {
            // 최근 포인트 업데이트 날짜가 오늘이 아니라면 10점으로 초기화 후 반환함
            point.setPoints(10);
            point.setUpdatedAt(LocalDateTime.now());
            pointRepository.save(point);
            return 10;
        }
    }

    // 오늘 날짜인지 확인
    private boolean isToday(LocalDateTime dateTime) {
        LocalDate today = LocalDate.now(); // 오늘 날짜
        LocalDate date = dateTime.toLocalDate(); // 오늘 날짜와 비교하기 위해 받아온 dateTime을 date 형식으로 변환
        return today.equals(date);
        // 입력받은 값이 오늘 날짜와 같다면 true 반환, 오늘 날짜와 다르다면 false 반환
    }

    public GetMyDiagnoseListResponseDTO getDiagnoseListByPet(Long memberId, Long petId, Category category, int page) {

        Optional<Pet> petOpt= petRepository.findByPetIdAndMember_MemberId(petId, memberId);

        if (!petOpt.isPresent()) {
            // Optional 객체에서 값이 비어있는 경우 예외를 던짐
            throw new NoSuchElementException("Pet with ID " + petId + " not found");
        }

        // 최신순으로 페이징, 1페이지당 10개 반환
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());

        // petId와 category에 따라 진단 결과들을 페이징처리해서 불러옴
        Page<Result> results = resultRepository.findByPetIdAndCategory(petId, category, pageable);
        // DiagnoseDTO에 리스트 형식으로 진단 결과들을 담음
        List<DiagnoseDTO> diagnoseDTO = results.stream()
                .map(result -> {
                    return new DiagnoseDTO(result.getResultId(), result.getCreatedAt(), result.getScore());
                })
                .collect(Collectors.toList());

        return new GetMyDiagnoseListResponseDTO(diagnoseDTO);
    }

    // 사용자 포인트 차감 (-1)
    public Integer updatePointsByMember(Long memberId) throws Exception {
        Optional<Point> pointOpt = pointRepository.findByMemberId(memberId);

        if (!pointOpt.isPresent()) {
            // Optional 객체에서 값이 비어있는 경우 예외를 던짐
            throw new NoSuchElementException("Member with ID " + memberId + " has no point");
        }

        Point point = pointOpt.get(); // Optinal 객체에서 point를 가져옴
        Integer points = point.getPoints();

        // 진단시 포인트가 1점 차감됨
        point.setPoints(points-1);
        point.setUpdatedAt(LocalDateTime.now());
        pointRepository.save(point);
        return point.getPoints();
    }

    // 진단서 상세 조회
    public GetMyDiagnoseResponseDTO getDiagnose(Long diagnoseId) throws Exception {
        Optional<Result> resultOpt = resultRepository.findByResultId(diagnoseId);

        if (!resultOpt.isPresent()) {
            // Optional 객체에서 값이 비어있는 경우 예외 발생
            throw new NoSuchElementException("Result with ID " + diagnoseId + " not found");
        }

        Result result = resultOpt.get(); // result 가져옴

        // 진단 id값으로 제품 조회
        List<Product> products = productRepository.findByResultId(diagnoseId);

        // ProductDTO에 리스트 형식으로 추천 제품들 담음
        List<ProductDTO> productsDTO = products.stream()
                .map(product -> {
                    return new ProductDTO(product.getProductTitle(), product.getProductImage(), product.getLprice(), product.getBrand());
                })
                .collect(Collectors.toList());

        // GetMyDiagnoseResponseDTO에 전체 조회 결과 담아서 반환
        return GetMyDiagnoseResponseDTO.builder()
                .diagnose_id(result.getResultId())
                .score(result.getScore())
                .result_detail(result.getResultDetail())
                .after_care(result.getResultCare())
                .ai_products(productsDTO).build();
    }

    // 진단 삭제
    public boolean deleteDiagnose(Long diagnoseId) throws Exception {
        Optional<Result> resultOpt = resultRepository.findByResultId(diagnoseId);

        if (!resultOpt.isPresent()) {
            // Optional 객체에서 값이 비어있는 경우 예외 발생
            throw new NoSuchElementException("Result with ID " + diagnoseId + " not found");
        }

        Result result = resultOpt.get(); // result 가져옴

        boolean deletionResult = resultRepository.deleteResultByResultId(result.getResultId());

        return deletionResult;
    }
}
