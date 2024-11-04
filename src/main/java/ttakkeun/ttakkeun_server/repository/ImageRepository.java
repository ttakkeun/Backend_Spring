package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ttakkeun.ttakkeun_server.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
