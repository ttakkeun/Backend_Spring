package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import ttakkeun.ttakkeun_server.entity.LikeProduct;
import ttakkeun.ttakkeun_server.repository.custom.CustomLikeRepository;


@Repository
public interface LikeRepository extends JpaRepository<LikeProduct, Long>, CustomLikeRepository {
}
