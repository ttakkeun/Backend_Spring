package ttakkeun.ttakkeun_server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ttakkeun.ttakkeun_server.apiPayLoad.exception.ExceptionHandler;
import ttakkeun.ttakkeun_server.apiPayLoad.exception.MemberHandler;
import ttakkeun.ttakkeun_server.apiPayLoad.exception.PetHandler;
import ttakkeun.ttakkeun_server.apiPayLoad.exception.RecordHandler;
import ttakkeun.ttakkeun_server.converter.ImageConverter;
import ttakkeun.ttakkeun_server.converter.RecordConverter;
import ttakkeun.ttakkeun_server.dto.record.RecordListResponseDto;
import ttakkeun.ttakkeun_server.dto.record.RecordRequestDTO;
import ttakkeun.ttakkeun_server.dto.record.RecordResponseDTO;
import ttakkeun.ttakkeun_server.entity.*;
import ttakkeun.ttakkeun_server.entity.Record;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final PetRepository petRepository;
    private final ChecklistQuestionRepository checklistQuestionRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final ImageRepository imageRepository;
    private final S3ImageService s3ImageService;
    private final ResultRepository resultRepository;

    public List<RecordListResponseDto> getRecordsByCategory(Member member, Long petId, Category category, int page, int size) {
        if (member == null || member.getMemberId() == null) {
            throw new MemberHandler(MEMBER_NOT_FOUND);
        }
        //memberId로 petId를 확인
        Pet pet = petRepository.findByPetIdAndMember(petId, member).orElseThrow(() -> new PetHandler(PET_NOT_FOUND));

        // 페이지 요청 생성 (createdAt 기준으로 정렬)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Record> recordPage = recordRepository.findByPet_PetIdAndCategory(pet.getPetId(), category, pageable);

        // DTO로 변환 및 정렬
        return recordPage.stream()
                .map(RecordConverter::t0RecordListResponseDto).sorted((r1, r2) -> {
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
                .map(question -> RecordResponseDTO.QuestionDTO.builder()
                        .questionId(question.getQuestionId())
                        .questionText(question.getQuestionText())
                        .descriptionText(question.getDescriptionText())
                        .answers(question.getChecklistAnswers().stream()
                                .map(answer -> RecordResponseDTO.AnswersDTO.builder()
                                        .answerText(answer.getChecklistAnswerText())
                                        .build())
                                .collect(Collectors.toList()))
                        .isDupe(question.getIsDupe())
                        .build())
                .collect(Collectors.toList());
    }

    public RecordResponseDTO.RegisterResultDTO createRecord(Long petId, RecordRequestDTO.RecordRegisterDTO request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new PetHandler(PET_ID_NOT_AVAILABLE));

        Record record = RecordConverter.toRecord(pet, request);
        recordRepository.save(record);

        List<RecordResponseDTO.QuestionAnswerDTO> questionAnswerDTOs = request.getAnswers().stream().map(answerDTO -> {
            ChecklistQuestion question = checklistQuestionRepository.findById(answerDTO.getQuestionId())
                    .orElseThrow(() -> new ExceptionHandler(QUESTION_NOT_FOUND));

            // UserAnswer 엔티티 빌드
            UserAnswer userAnswer = RecordConverter.toUserAnswer(question, record, answerDTO);
            userAnswerRepository.save(userAnswer); // UserAnswer 저장

            // Image 엔티티 빌드 및 저장
            List<Image> images = answerDTO.getImages().stream()
                    .filter(imageFile -> imageFile != null && !imageFile.isEmpty()) // 이미지가 비어있지 않은 경우만 처리
                    .map(imageFile -> {
                        String imageUrl = s3ImageService.upload(imageFile); // S3에 이미지 업로드 후 URL 획득
                        return ImageConverter.toImage(imageUrl, userAnswer);
                    })
                    .collect(Collectors.toList());

            imageRepository.saveAll(images); // Image 엔티티들 저장

            List<String> imageUrls = images.stream()
                    .map(Image::getImageUrl)
                    .collect(Collectors.toList());

            // QuestionAnswerDTO 생성
            return RecordConverter.toQuestionAnswerDTO(question, userAnswer, imageUrls);
        }).toList();

        return RecordConverter.toRecordResultDTO(record, questionAnswerDTOs);
    }

    public RecordResponseDTO.DetailResultDTO getRecordDetails(Long petId, Long recordId) {
        // Record 조회
        Record record = recordRepository.findByPet_PetIdAndRecordId(petId, recordId)
                .orElseThrow(() -> new RecordHandler(RECORD_NOT_FOUND));

        // UserAnswer 리스트를 question_id 기준으로 그룹화
        Map<Long, List<UserAnswer>> groupedAnswers = record.getAnswerList().stream()
                .collect(Collectors.groupingBy(answer -> answer.getQuestion().getQuestionId()));


        // 그룹화된 데이터를 QuestionAnswerDTO 리스트로 변환
        List<RecordResponseDTO.QuestionAnswerDTO> questionAnswerList = groupedAnswers.entrySet().stream()
                .map(entry -> {
                    ChecklistQuestion question = entry.getValue().get(0).getQuestion(); // 동일한 질문 가져오기
                    List<String> answers = entry.getValue().stream()
                            .flatMap(userAnswer -> userAnswer.getUserAnswerText().stream()) // 모든 답변을 리스트로
                            .collect(Collectors.toList());
                    List<String> images = entry.getValue().stream()
                            .flatMap(userAnswer -> userAnswer.getImages().stream()) // 이미지 URL 리스트
                            .map(Image::getImageUrl)
                            .collect(Collectors.toList());
                    return RecordResponseDTO.QuestionAnswerDTO.builder()
                            .question(question.getQuestionText())
                            .answer(answers)
                            .images(images)
                            .build();
                })
                .collect(Collectors.toList());

        // createdAt에서 date와 time 추출
        LocalDateTime createdAt = record.getCreatedAt();
        String date = createdAt != null ? createdAt.toLocalDate().toString() : "N/A";
        String time = createdAt != null
                ? createdAt.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                : "N/A"; // 시간 포맷팅

        // DetailResultDTO 빌드
        return RecordResponseDTO.DetailResultDTO.builder()
                .category(record.getCategory().name())      // 카테고리 추가
                .questions(questionAnswerList)              // 질문-답변-이미지 리스트 추가
                .etc(record.getEtc())                       // 기타 사항 추가
                .date(date)
                .time(time)
                .build();
    }

    public RecordResponseDTO.DeleteResultDTO deleteRecord(Long recordId) {
        Optional<Record> recordOptional = recordRepository.findById(recordId);

        if (recordOptional.isPresent()) {
            Record record = recordOptional.get();

            List<Result> results = resultRepository.findByRecord(record);

            for (Result result : results) {
                result.setRecord(null);
                resultRepository.save(result);
            }

            recordRepository.deleteById(recordId);
            return RecordResponseDTO.DeleteResultDTO.builder()
                    .message("일지 삭제에 성공하였습니다.")
                    .build();
        } else {
            throw new RecordHandler(RECORD_NOT_FOUND);
        }
    }

    public List<RecordListResponseDto> getRecordsAtDate(Member member, Long petId, Category category, String date) {
        if (member == null || member.getMemberId() == null) {
            throw new ExceptionHandler(MEMBER_NOT_FOUND);
        }
        System.out.println("Member ID: " + member.getMemberId());

        //memberId로 petId를 확인
        Pet pet = petRepository.findByPetIdAndMember(petId, member).orElseThrow(() -> new ExceptionHandler(PET_NOT_FOUND));

        // 입력된 날짜를 LocalDate로 변환
        LocalDate targetDate = LocalDate.parse(date);

        // 날짜의 시작과 끝을 계산
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX);

        // Repository에서 해당 날짜의 기록 조회
        List<Record> recordPage = recordRepository.findByPet_PetIdAndCategoryAndCreatedAtBetween(pet.getPetId(), category, startOfDay, endOfDay);

        // DTO로 변환 및 정렬
        return recordPage.stream()
                .map(RecordConverter::t0RecordListResponseDto)
                .sorted((r1, r2) -> {
                    int dateComparison = r2.getCreatedAtDate().compareTo(r1.getCreatedAtDate());
                    if (dateComparison != 0) {
                        return dateComparison;
                    }
                    return r2.getCreatedAtTime().compareTo(r1.getCreatedAtTime());
                })
                .collect(Collectors.toList());
    }

}
