package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.Tip;
import ttakkeun.ttakkeun_server.entity.enums.Category;

import java.util.List;


@Repository
public interface TipRepository extends JpaRepository<Tip, Long> {
    Page<Tip> findByCategory(Category category, Pageable pageable);

    @Query("SELECT t FROM Tip t WHERE t.recommendCount <> 0 ORDER BY t.recommendCount DESC, t.createdAt DESC LIMIT 10")
    List<Tip> findByIsPopularTrue();

    // 최신 10개 팁 조회
    List<Tip> findTop10ByOrderByCreatedAt();

    List<Tip> findByMember(Member member);

    //페이징을 이용해 10개 단위로 작성한 팁 가져오기
    Page<Tip> findByMember(Member member, Pageable pageable);
}

