package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ttakkeun.ttakkeun_server.entity.Record;
import ttakkeun.ttakkeun_server.entity.enums.Category;


import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    Page<Record> findByPetId_PetIdAndCategory(Long petId, Category category, Pageable pageable);
}
