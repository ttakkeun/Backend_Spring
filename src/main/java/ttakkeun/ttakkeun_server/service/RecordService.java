package ttakkeun.ttakkeun_server.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.bv.size.SizeValidatorForArray;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.apiPayLoad.ExceptionHandler;
import ttakkeun.ttakkeun_server.dto.record.RecordListResponseDto;
import ttakkeun.ttakkeun_server.entity.Member;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.entity.Record;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.repository.PetRepository;
import ttakkeun.ttakkeun_server.repository.RecordRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus.MEMBER_NOT_FOUND;
import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus.PET_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final PetRepository petRepository;

    public List<RecordListResponseDto> getRecordsByCategory(Member member, Long petId, Category category, int page, int size) {
        if (member == null || member.getMemberId() == null) {
            throw new ExceptionHandler(MEMBER_NOT_FOUND);
        }
        System.out.println("Member ID: " + member.getMemberId());

        //memberId로 petId를 확인
        Pet pet = petRepository.findByPetIdAndMemberId(petId, member).orElseThrow(() -> new ExceptionHandler(PET_NOT_FOUND));

        // 페이지 요청 생성 (createdAt 기준으로 정렬)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Record> recordPage = recordRepository.findByPetId_PetIdAndCategory(pet.getPetId(), category, pageable);

        // DTO로 변환 및 정렬
        return recordPage.stream()
                .map(record -> new RecordListResponseDto(
                        record.getRecordId(),
                        record.getUpdatedAt().toLocalDate(),  // LocalDate 추출
                        record.getUpdatedAt().toLocalTime()   // LocalTime 추출
                ))
                .sorted((r1, r2) -> {
                    int dateComparison = r2.getUpdatedAtDate().compareTo(r1.getUpdatedAtDate());
                    if (dateComparison != 0) {
                        return dateComparison;
                    }
                    return r2.getUpdatedAtTime().compareTo(r1.getUpdatedAtTime());
                })
                .collect(Collectors.toList());
    }

}
