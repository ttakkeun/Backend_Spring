package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.repository.custom.CustomResultRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ttakkeun.ttakkeun_server.entity.Result;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long>, CustomResultRepository {
  
    // Eager 로딩 방법으로 불러옴 (필요한 데이터만을 즉시 로드)
    @EntityGraph(attributePaths = {"ProductList"})
    Optional<Result> findByResultId(Long resultId);

}
