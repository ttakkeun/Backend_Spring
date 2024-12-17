package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import feign.Param;
import jakarta.transaction.Transactional;
import ttakkeun.ttakkeun_server.entity.ResultProduct;

@Repository
public interface ResultProductRepository extends JpaRepository<ResultProduct, Integer> {

	// long countByResultResultId(Long resultId);
	//
	// @Modifying
	// @Transactional
	// @Query("DELETE FROM ResultProduct rp WHERE rp.result.resultId = :resultId")
	// int deleteResultProductByResultResultId(@Param("resultId") Long resultId);

}
