package ttakkeun.ttakkeun_server.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ttakkeun.ttakkeun_server.dto.RecordListResponseDto;
import ttakkeun.ttakkeun_server.entity.Record;
import ttakkeun.ttakkeun_server.entity.enums.Category;


import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findByPetId_PetIdAndCategory(Long petId, Category category);
}
