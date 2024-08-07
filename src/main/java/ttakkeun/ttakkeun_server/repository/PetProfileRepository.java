package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ttakkeun.ttakkeun_server.entity.Pet;

public interface PetProfileRepository extends JpaRepository<Pet, Long> {
}
