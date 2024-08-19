package ttakkeun.ttakkeun_server.repository.custom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ttakkeun.ttakkeun_server.entity.Record;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomResultRepositoryImpl implements CustomResultRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Long findLatestResultId(List<Record> records) {
        return em.createQuery("SELECT r.id FROM Result r WHERE r.record IN :records ORDER BY r.createdAt DESC", Long.class)
                .setParameter("records", records)
                .setMaxResults(1)
                .getSingleResult();
    }
}
