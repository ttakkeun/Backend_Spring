package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ttakkeun.ttakkeun_server.entity.Point;
import ttakkeun.ttakkeun_server.entity.Result;

import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    // 가장 최근 진단 결과의 ID를 불러오는 쿼리
    @Query("SELECT r.resultId FROM Result r ORDER BY r.createdAt DESC")
    Long findLatestResultId();

    // Eager 로딩 방법으로 불러옴 (필요한 데이터만을 즉시 로드)
    @EntityGraph(attributePaths = {"ProductList"})
    Optional<Result> findByResultId(Long resultId);
}