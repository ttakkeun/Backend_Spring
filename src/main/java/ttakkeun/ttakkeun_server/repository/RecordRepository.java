package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ttakkeun.ttakkeun_server.entity.Record;
import ttakkeun.ttakkeun_server.entity.enums.Category;


import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    Page<Record> findByPet_PetIdAndCategory(Long petId, Category category, Pageable pageable);

    Optional<Record> findByPet_PetIdAndRecordId(Long petId, Long recordId);

    Page<Record> findByPet_PetIdAndCategoryAndCreatedAtBetween(Long petId, Category category, LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable);

    Optional<Record> findByRecordId(Long recordId);

}
