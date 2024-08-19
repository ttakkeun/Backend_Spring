//package ttakkeun.ttakkeun_server.service;
//
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import ttakkeun.ttakkeun_server.apiPayLoad.ExceptionHandler;
//import ttakkeun.ttakkeun_server.dto.record.RecordListResponseDto;
//import ttakkeun.ttakkeun_server.dto.record.RecordRequestDTO;
//import ttakkeun.ttakkeun_server.dto.record.RecordResponseDTO;
//import ttakkeun.ttakkeun_server.entity.*;
//import ttakkeun.ttakkeun_server.entity.Record;
//import ttakkeun.ttakkeun_server.entity.enums.Category;
//import ttakkeun.ttakkeun_server.repository.*;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import static ttakkeun.ttakkeun_server.apiPayLoad.code.status.ErrorStatus.*;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class RecordService {
//
//    private final RecordRepository recordRepository;
//    private final PetRepository petRepository;
//    private final ChecklistQuestionRepository checklistQuestionRepository;
//    private final UserAnswerRepository userAnswerRepository;
//    private final S3ImageService s3ImageService;
//
//    public List<RecordListResponseDto> getRecordsByCategory(Member member, Long petId, Category category, int page, int size) {
//        if (member == null || member.getMemberId() == null) {
//            throw new ExceptionHandler(MEMBER_NOT_FOUND);
//        }
//        System.out.println("Member ID: " + member.getMemberId());
//
//        //memberId로 petId를 확인
//        Pet pet = petRepository.findByPetIdAndMember(petId, member).orElseThrow(() -> new ExceptionHandler(PET_NOT_FOUND));
//
//        // 페이지 요청 생성 (createdAt 기준으로 정렬)
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
//        Page<Record> recordPage = recordRepository.findByPet_PetIdAndCategory(pet.getPetId(), category, pageable);
//
//        // DTO로 변환 및 정렬
//        return recordPage.stream()
//                .map(record -> new RecordListResponseDto(
//                        record.getRecordId(),
//                        record.getCreatedAt().toLocalDate(),  // LocalDate 추출
//                        record.getCreatedAt().toLocalTime()   // LocalTime 추출
//                ))
//                .sorted((r1, r2) -> {
//                    int dateComparison = r2.getCreatedAtDate().compareTo(r1.getCreatedAtDate());
//                    if (dateComparison != 0) {
//                        return dateComparison;
//                    }
//                    return r2.getCreatedAtTime().compareTo(r1.getCreatedAtTime());
//                })
//                .collect(Collectors.toList());
//    }
//
//    public List<RecordResponseDTO.QuestionDTO> getQuestionsByCategory(Category category) {
//        List<ChecklistQuestion> questions = checklistQuestionRepository.findByQuestionCategory(category);
//
//        return questions.stream()
//                .map(question -> RecordResponseDTO.QuestionDTO.builder()
//                        .questionId(question.getQuestionId())
//                        .questionText(question.getQuestionText())
//                        .descriptionText(question.getDescriptionText())
//                        .answers(question.getChecklistAnswers().stream()
//                                .map(answer -> RecordResponseDTO.AnswersDTO.builder()
//                                        .answerText(answer.getChecklistAnswerText())
//                                        .build())
//                                .collect(Collectors.toList()))
//                        .isDupe(question.getIsDupe())
//                        .build())
//                .collect(Collectors.toList());
//    }
//
//    @Transactional
//    public RecordResponseDTO.RegisterResultDTO registerRecord(Long petId, RecordRequestDTO.RecordRegisterDTO request) {
//        Pet pet = petRepository.findById(petId)
//                .orElseThrow(() -> new ExceptionHandler(PET_ID_NOT_AVAILABLE));
//
//        Record record = Record.builder()
//                .pet(pet)
//                .category(request.getCategory())
//                .etc(request.getEtc())
//                .build();
//
//        List<UserAnswer> answerList = new ArrayList<>();
//        for (RecordRequestDTO.AnswerDTO answerDTO : request.getAnswers()) {
//            ChecklistQuestion question = checklistQuestionRepository.findById(answerDTO.getQuestionId())
//                    .orElseThrow(() -> new ExceptionHandler(QUESTION_NOT_FOUND));
//
//            // UserAnswer를 여러 개 생성하지 않고, 하나의 UserAnswer에 모든 답변을 리스트로 저장
//            UserAnswer answer = UserAnswer.builder()
//                    .question(question)
//                    .record(record)
//                    .userAnswerText(answerDTO.getAnswerText()) // 리스트를 직접 저장
//                    .build();
//
//            answerList.add(answer);
//        }
//
//        record.getAnswerList().addAll(answerList);
//        recordRepository.save(record);
//        userAnswerRepository.saveAll(answerList);
//
//        return new RecordResponseDTO.RegisterResultDTO(record.getRecordId());
//    }
//
//
//    @Transactional
//    public List<RecordRequestDTO.RecordImageDTO> uploadImages(Long recordId, Long questionId, List<MultipartFile> files) {
//
//        Record record = recordRepository.findById(recordId).orElseThrow(()-> new ExceptionHandler(RECORD_NOT_FOUND));
//
//        System.out.println("uploadImages");
//        // ChecklistAnswer 조회 (recordId와 questionId로 조회)
//        log.info("여기기기기기");
//        UserAnswer answer = userAnswerRepository.findByRecord_recordIdAndQuestion_questionId(record.getRecordId(), questionId)
//                .orElseThrow(() -> new ExceptionHandler(ANSWER_NOT_FOUND));
//
//        log.info("Number of images in RecordImageDTO: {}", files.size());
//
//        // MultipartFile 리스트를 Image 엔티티로 변환하여 저장
//        List<Image> imageEntities = files.stream()
//                .map(file -> {
//                    log.info("Uploading image {}", file);
//                    String imageUrl = s3ImageService.upload(file); // 파일 저장 로직
//                    return Image.builder().imageUrl(imageUrl).build();
//                })
//                .collect(Collectors.toList());
//
//        // ChecklistAnswer에 이미지 추가
//        log.info("Upload image {}", imageEntities);
//        answer.addImages(imageEntities);
//
//        // 변경 사항 저장
//        userAnswerRepository.save(answer);
//
//        return imageEntities.stream()
//                .map(image -> RecordRequestDTO.RecordImageDTO.builder()
//                        .imageId(image.getImageId()) // imageId를 설정합니다.
//                        .imageUrl(image.getImageUrl())
//                        .build())
//                .collect(Collectors.toList());
//    }
//
//    public RecordResponseDTO.DetailResultDTO getRecordDetails(Long petId, Long recordId) {
//        // Record 조회
//        Record record = recordRepository.findByPet_PetIdAndRecordId(petId, recordId)
//                .orElseThrow(() -> new ExceptionHandler(RECORD_NOT_FOUND));
//
//        // UserAnswer 리스트를 question_id 기준으로 그룹화
//        Map<Long, List<UserAnswer>> groupedAnswers = record.getAnswerList().stream()
//                .collect(Collectors.groupingBy(answer -> answer.getQuestion().getQuestionId()));
//
//
//        // 그룹화된 데이터를 QuestionAnswerDTO 리스트로 변환
//        List<RecordResponseDTO.QuestionAnswerDTO> questionAnswerList = groupedAnswers.entrySet().stream()
//                .map(entry -> {
//                    ChecklistQuestion question = entry.getValue().get(0).getQuestion(); // 동일한 질문 가져오기
//                    List<String> answers = entry.getValue().stream()
//                            .flatMap(userAnswer -> userAnswer.getUserAnswerText().stream()) // 모든 답변을 리스트로
//                            .collect(Collectors.toList());
//                    List<String> images = entry.getValue().stream()
//                            .flatMap(userAnswer -> userAnswer.getImages().stream()) // 이미지 URL 리스트
//                            .map(Image::getImageUrl)
//                            .collect(Collectors.toList());
//                    return RecordResponseDTO.QuestionAnswerDTO.builder()
//                            .question(question.getQuestionText())
//                            .answer(answers)
//                            .images(images)
//                            .build();
//                })
//                .collect(Collectors.toList());
//
//        // createdAt에서 date와 time 추출
//        LocalDateTime createdAt = record.getCreatedAt();
//        String date = createdAt != null ? createdAt.toLocalDate().toString() : "N/A";
//        String time = createdAt != null
//                ? createdAt.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
//                : "N/A"; // 시간 포맷팅
//
//        // DetailResultDTO 빌드
//        return RecordResponseDTO.DetailResultDTO.builder()
//                .category(record.getCategory().name())      // 카테고리 추가
//                .questions(questionAnswerList)              // 질문-답변-이미지 리스트 추가
//                .etc(record.getEtc())                       // 기타 사항 추가
//                .date(date)
//                .time(time)
//                .build();
//    }
//
//    public RecordResponseDTO.DeleteResultDTO deleteRecord(Long recordId) {
//        Optional<Record> record = recordRepository.findById(recordId);
//        if (record.isPresent()) {
//            recordRepository.deleteById(recordId);
//            return RecordResponseDTO.DeleteResultDTO.builder()
//                    .message("일지 삭제에 성공하였습니다.")
//                    .build();
//        } else {
//            throw new ExceptionHandler(RECORD_NOT_FOUND);
//        }
//    }
//
//    public List<RecordListResponseDto> getRecordsAtDate(Member member, Long petId, Category category, int page, int size, String date) {
//        if (member == null || member.getMemberId() == null) {
//            throw new ExceptionHandler(MEMBER_NOT_FOUND);
//        }
//        System.out.println("Member ID: " + member.getMemberId());
//
//        //memberId로 petId를 확인
//        Pet pet = petRepository.findByPetIdAndMember(petId, member).orElseThrow(() -> new ExceptionHandler(PET_NOT_FOUND));
//
//        // 입력된 날짜를 LocalDate로 변환
//        LocalDate targetDate = LocalDate.parse(date);
//
//        // 날짜의 시작과 끝을 계산
//        LocalDateTime startOfDay = targetDate.atStartOfDay();
//        LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX);
//
//        // 페이지 요청 생성 (createdAt 기준으로 정렬)
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
//
//        // Repository에서 해당 날짜의 기록 조회
//        Page<Record> recordPage = recordRepository.findByPet_PetIdAndCategoryAndCreatedAtBetween(pet.getPetId(), category, startOfDay, endOfDay, pageable);
//
//        // DTO로 변환 및 정렬
//        return recordPage.stream()
//                .map(record -> new RecordListResponseDto(
//                        record.getRecordId(),
//                        record.getCreatedAt().toLocalDate(),  // LocalDate 추출
//                        record.getCreatedAt().toLocalTime()   // LocalTime 추출
//                ))
//                .sorted((r1, r2) -> {
//                    int dateComparison = r2.getCreatedAtDate().compareTo(r1.getCreatedAtDate());
//                    if (dateComparison != 0) {
//                        return dateComparison;
//                    }
//                    return r2.getCreatedAtTime().compareTo(r1.getCreatedAtTime());
//                })
//                .collect(Collectors.toList());
//    }
//}
