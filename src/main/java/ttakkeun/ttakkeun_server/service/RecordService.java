package ttakkeun.ttakkeun_server.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.apiPayLoad.ExceptionHandler;
import ttakkeun.ttakkeun_server.dto.record.RecordListResponseDto;
import ttakkeun.ttakkeun_server.dto.record.RecordRequestDTO;
import ttakkeun.ttakkeun_server.dto.record.RecordResponseDTO;
import ttakkeun.ttakkeun_server.entity.*;
import ttakkeun.ttakkeun_server.entity.Record;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.repository.ChecklistAnswerRepository;
import ttakkeun.ttakkeun_server.repository.ChecklistQuestionRepository;
import ttakkeun.ttakkeun_server.repository.PetRepository;
import ttakkeun.ttakkeun_server.repository.RecordRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus.*;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final PetRepository petRepository;
    private final ChecklistQuestionRepository checklistQuestionRepository;
    private final ChecklistAnswerRepository checklistAnswerRepository;

    public List<RecordListResponseDto> getRecordsByCategory(Member member, Long petId, Category category, int page, int size) {
        if (member == null || member.getMemberId() == null) {
            throw new ExceptionHandler(MEMBER_NOT_FOUND);
        }
        System.out.println("Member ID: " + member.getMemberId());

        //memberId로 petId를 확인
        Pet pet = petRepository.findByPetIdAndMember(petId, member).orElseThrow(() -> new ExceptionHandler(PET_NOT_FOUND));

        // 페이지 요청 생성 (createdAt 기준으로 정렬)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Record> recordPage = recordRepository.findByPet_PetIdAndCategory(pet.getPetId(), category, pageable);

        // DTO로 변환 및 정렬
        return recordPage.stream()
                .map(record -> new RecordListResponseDto(
                        record.getRecordId(),
                        record.getCreatedAt().toLocalDate(),  // LocalDate 추출
                        record.getCreatedAt().toLocalTime()   // LocalTime 추출
                ))
                .sorted((r1, r2) -> {
                    int dateComparison = r2.getCreatedAtDate().compareTo(r1.getCreatedAtDate());
                    if (dateComparison != 0) {
                        return dateComparison;
                    }
                    return r2.getCreatedAtTime().compareTo(r1.getCreatedAtTime());
                })
                .collect(Collectors.toList());
    }

    public List<RecordResponseDTO.QuestionDTO> getQuestionsByCategory(Category category) {
        List<ChecklistQuestion> questions = checklistQuestionRepository.findByQuestionCategory(category);

        return questions.stream()
                .map(question -> new RecordResponseDTO.QuestionDTO(question.getQuestionId(), question.getQuestionText(), question.getDescriptionText()))
                .collect(Collectors.toList());
    }

    @Transactional
    public RecordResponseDTO.RegisterResultDTO registerRecord(Long petId, RecordRequestDTO.RecordRegisterDTO request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ExceptionHandler(PET_ID_NOT_AVAILABLE));

        Record record = Record.builder()
                .pet(pet)
                .category(request.getCategory())
                .etc(request.getEtc())
                .build();

        List<ChecklistAnswer> answerList = new ArrayList<>();
        for (RecordRequestDTO.AnswerDTO answerDTO : request.getAnswers()) {
            ChecklistQuestion question = checklistQuestionRepository.findById(answerDTO.getQuestionId())
                    .orElseThrow(() -> new ExceptionHandler(QUESTION_NOT_FOUND));

            ChecklistAnswer answer = ChecklistAnswer.builder()
                    .answerText(answerDTO.getAnswerText())
                    .question(question)
                    .record(record)
                    .build();

            answerList.add(answer);
            checklistAnswerRepository.save(answer);
        }

        record.getAnswerList().addAll(answerList);
        recordRepository.save(record);

        return RecordResponseDTO.RegisterResultDTO.builder()
                .recordId(record.getRecordId())
                .build();
    }


    @Transactional
    public RecordResponseDTO.DetailResultDTO getRecordDetails(Long petId, Long recordId) {
        Record record = recordRepository.findByPet_PetIdAndRecordId(petId, recordId)
                .orElseThrow(() -> new ExceptionHandler(RECORD_NOT_FOUND));

        List<ChecklistAnswer> answers = record.getAnswerList();

        RecordResponseDTO.DetailResultDTO.DetailResultDTOBuilder dtoBuilder = RecordResponseDTO.DetailResultDTO.builder();

        dtoBuilder.category(record.getCategory().name());

        if(!answers.isEmpty()) {
            dtoBuilder.question1(answers.get(0).getQuestion().getQuestionText())
                    .answer1(answers.get(0).getAnswerText())
                    .image1(answers.get(0).getImageList().stream()
                            .map(image -> image.getImageUrl())
                            .collect(Collectors.toList()));
        }

        if (answers.size() > 1) {
            dtoBuilder.question2(answers.get(1).getQuestion().getQuestionText())
                    .answer2(answers.get(1).getAnswerText())
                    .image2(answers.get(1).getImageList().stream()
                            .map(image -> image.getImageUrl())
                            .collect(Collectors.toList()));
        }

        if (answers.size() > 2) {
            dtoBuilder.question3(answers.get(2).getQuestion().getQuestionText())
                    .answer3(answers.get(2).getAnswerText())
                    .image3(answers.get(2).getImageList().stream()
                            .map(image -> image.getImageUrl())
                            .collect(Collectors.toList()));
        }

        dtoBuilder.etc(record.getEtc());

        return dtoBuilder.build();
    }
}
