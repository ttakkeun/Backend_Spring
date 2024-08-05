package ttakkeun.ttakkeun_server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttakkeun.ttakkeun_server.repository.PointRepository;
import ttakkeun.ttakkeun_server.entity.Point;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Transactional
@Service
public class DiagnoseService {
    private final PointRepository pointRepository;

    @Autowired
    public DiagnoseService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

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

    private boolean isToday(LocalDateTime dateTime) {
        LocalDate today = LocalDate.now(); // 오늘 날짜
        LocalDate date = dateTime.toLocalDate(); // 오늘 날짜와 비교하기 위해 받아온 dateTime을 date 형식으로 변환
        return today.equals(date);
        // 입력받은 값이 오늘 날짜와 같다면 true 반환, 오늘 날짜와 다르다면 false 반환
    }

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
            // 0 반환에서 오류 발생하도록 수정함
            throw new NoSuchElementException("Member with ID " + memberId + " not found");
        }
    }
}
