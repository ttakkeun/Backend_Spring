package ttakkeun.ttakkeun_server.service;

import lombok.RequiredArgsConstructor;
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

    public List<RecordListResponseDto> getRecordsByCategory(Member member, Long petId, Category category) {
        if (member == null || member.getMemberId() == null) {
            throw new ExceptionHandler(MEMBER_NOT_FOUND);
        }
        System.out.println("Member ID: " + member.getMemberId());

        //memberId로 petId를 확인
        Pet pet = petRepository.findByPetIdAndMemberId(petId, member).orElseThrow(() -> new ExceptionHandler(PET_NOT_FOUND));

        List<Record> records = recordRepository.findByPetId_PetIdAndCategory(pet.getPetId(), category);
        return records.stream()
                .map(record -> new RecordListResponseDto(record.getRecordId(), record.getCreatedAt())) // DTO로 변환
                .collect(Collectors.toList());
    }

}
