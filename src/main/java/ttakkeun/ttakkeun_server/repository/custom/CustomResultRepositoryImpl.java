package ttakkeun.ttakkeun_server.repository.custom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomResultRepositoryImpl implements CustomResultRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Long findLatestResultId() {
        return em.createQuery("SELECT r.id FROM Result r ORDER BY r.createdAt DESC", Long.class)
                .setMaxResults(1)
                .getSingleResult();
    }
}
