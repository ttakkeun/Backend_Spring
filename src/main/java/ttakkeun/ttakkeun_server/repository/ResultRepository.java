package ttakkeun.ttakkeun_server.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ResultRepository {

    private final EntityManager em;

    //가장 최근 진단결과 id불러오기
    public Long findLatestResultId() {
        return em.createQuery("SELECT r.id FROM Result r ORDER BY createdAt desc", Long.class)
                .setMaxResults(1)
                .getSingleResult();
    }
}
