package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ttakkeun.ttakkeun_server.entity.Result;
import ttakkeun.ttakkeun_server.repository.custom.CustomResultRepository;

@Repository
public interface ResultRepository extends JpaRepository<Result, Integer>, CustomResultRepository {
}
