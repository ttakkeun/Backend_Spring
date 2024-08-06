package ttakkeun.ttakkeun_server.service;

import jakarta.persistence.EntityManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ttakkeun.ttakkeun_server.dto.GetMyDiagnoseResponseDTO;
import ttakkeun.ttakkeun_server.dto.ProductDTO;
import ttakkeun.ttakkeun_server.entity.Product;
import ttakkeun.ttakkeun_server.entity.Result;
import ttakkeun.ttakkeun_server.repository.PointRepository;
import ttakkeun.ttakkeun_server.entity.Point;
import ttakkeun.ttakkeun_server.repository.ProductRepository;
import ttakkeun.ttakkeun_server.repository.ResultRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Autowired
    public DiagnoseService(PointRepository pointRepository, ResultRepository resultRepository, ProductRepository productRepository) {
        this.pointRepository = pointRepository;
        this.resultRepository = resultRepository;
        this.productRepository = productRepository;
    }

    // 사용자 포인트 조회
    public Integer getPointsByMember(Long memberId) throws Exception {
        Optional<Point> pointOpt = pointRepository.findByMemberId(memberId);

        if (pointOpt.isPresent()) {
            Point point = pointOpt.get(); // Optinal 객체에서 point를 가져옴
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
        } else {
            // Optional 객체에서 값이 비어있는 경우 예외를 던짐
            // 0 반환에서 오류 발생하도록 수정함
            throw new NoSuchElementException("Member with ID " + memberId + " not found");
        }
    }

    // 오늘 날짜인지 확인
    private boolean isToday(LocalDateTime dateTime) {
        LocalDate today = LocalDate.now(); // 오늘 날짜
        LocalDate date = dateTime.toLocalDate(); // 오늘 날짜와 비교하기 위해 받아온 dateTime을 date 형식으로 변환
        return today.equals(date);
        // 입력받은 값이 오늘 날짜와 같다면 true 반환, 오늘 날짜와 다르다면 false 반환
    }

    // 사용자 포인트 차감 (-1)
    public Integer updatePointsByMember(Long memberId) throws Exception {
        Optional<Point> pointOpt = pointRepository.findByMemberId(memberId);

        if (pointOpt.isPresent()) {
            Point point = pointOpt.get(); // Optinal 객체에서 point를 가져옴
            Integer points = point.getPoints();

            // 진단시 포인트가 1점 차감됨
            point.setPoints(points-1);
            point.setUpdatedAt(LocalDateTime.now());
            pointRepository.save(point);
            return point.getPoints();
        } else {
            // Optional 객체에서 값이 비어있는 경우 예외를 던짐
            throw new NoSuchElementException("Member with ID " + memberId + " not found");
        }
    }

    // 진단서 상세 조회
    public GetMyDiagnoseResponseDTO getDiagnose(Long diagnoseId) throws Exception {
        Optional<Result> resultOpt = resultRepository.findByResultId(diagnoseId);

        if (resultOpt.isPresent()) {
            Result result = resultOpt.get(); // result 가져옴

            // 진단 id값으로 제품 조회
            List<Product> products = productRepository.findByResultResultId(diagnoseId);

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
        } else {
            // Optional 객체에서 값이 비어있는 경우 예외 발생
            throw new NoSuchElementException("Result with ID " + diagnoseId + " not found");
        }
    }
}
