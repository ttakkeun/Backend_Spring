package ttakkeun.ttakkeun_server.repository;

import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.entity.Record;
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

    // Page<Result> findByRecordList_Pet_PetIdAndResultCategory(Long petId, Category category, Pageable pageable);

    @Query("SELECT r FROM Result r WHERE r.record.pet.petId = :petId AND r.resultCategory = :category")
    Page<Result> findByPetIdAndCategory(@Param("petId") Long petId, @Param("category") Category category, Pageable pageable);

    List<Result> findByRecordIsNullAndPet(Pet pet);

    List<Result> findByRecord(Record record);
}