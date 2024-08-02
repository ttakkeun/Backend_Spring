package ttakkeun.ttakkeun_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.dto.RecordListResponseDto;
import ttakkeun.ttakkeun_server.entity.Record;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.repository.RecordRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecordService {

    private RecordRepository recordRepository;

    public List<RecordListResponseDto> getRecordsByCategory(Long petId, Category category) {
        List<Record> records = recordRepository.findByPetId_PetIdAndCategory(petId, category);
        return records.stream()
                .map(record -> new RecordListResponseDto(record.getRecordId(), record.getCreatedAt())) // DTO로 변환
                .collect(Collectors.toList());
    }

}
