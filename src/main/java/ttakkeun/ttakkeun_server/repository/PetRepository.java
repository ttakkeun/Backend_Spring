package ttakkeun.ttakkeun_server.repository;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.Pet;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    Optional<Pet> findById(Long id);

    Optional<Pet> findByPetIdAndMemberId(Long petId, Member member);

    @Query("SELECT p FROM Pet p WHERE p.memberId.memberId = :memberId")
    List<Pet> findByMemberId(@Param("memberId") Long memberId);
}
