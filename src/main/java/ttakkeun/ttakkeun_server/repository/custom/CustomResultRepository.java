package ttakkeun.ttakkeun_server.repository.custom;

import ttakkeun.ttakkeun_server.entity.Record;

import java.util.List;
import java.util.Optional;

public interface CustomResultRepository {
    Long findLatestResultId(List<Record> records);
}
