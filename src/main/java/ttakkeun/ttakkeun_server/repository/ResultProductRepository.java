package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ttakkeun.ttakkeun_server.entity.ResultProduct;

@Repository
public interface ResultProductRepository extends JpaRepository<ResultProduct, Integer> {
}
