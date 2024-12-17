package ttakkeun.ttakkeun_server.converter;

import org.springframework.stereotype.Component;
import ttakkeun.ttakkeun_server.dto.record.RecordListResponseDto;
import ttakkeun.ttakkeun_server.dto.record.RecordRequestDTO;
import ttakkeun.ttakkeun_server.dto.record.RecordResponseDTO;
import ttakkeun.ttakkeun_server.entity.ChecklistQuestion;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.entity.Record;
import ttakkeun.ttakkeun_server.entity.UserAnswer;

import java.util.List;

@Component
public class RecordConverter {
    public static Record toRecord(Pet pet, RecordRequestDTO.RecordRegisterDTO request) {
        return Record.builder()
                .pet(pet)
                .category(request.getCategory())
                .etc(request.getEtc())
                .build();
    }
    public static UserAnswer toUserAnswer(ChecklistQuestion question, Record record, RecordRequestDTO.AnswerDTO answerDTO) {
        return UserAnswer.builder()
                .question(question)
                .record(record)
                .userAnswerText(answerDTO.getAnswerText())
                .build();
    }

    public static RecordResponseDTO.QuestionAnswerDTO toQuestionAnswerDTO(
            ChecklistQuestion question,
            UserAnswer userAnswer,
            List<String> imageUrls) {
        return new RecordResponseDTO.QuestionAnswerDTO(
                question.getQuestionText(),
                userAnswer.getUserAnswerText(),
                imageUrls
        );
    }

    public static RecordResponseDTO.RegisterResultDTO toRecordResultDTO(Record record, List<RecordResponseDTO.QuestionAnswerDTO> questionAnswerDTOs) {
        return RecordResponseDTO.RegisterResultDTO.builder()
                .recordId(record.getRecordId())
                .category(record.getCategory().name())
                .answers(questionAnswerDTOs)
                .etc(record.getEtc())
                .build();
    }

    public static RecordListResponseDto t0RecordListResponseDto(Record record) {
        return new RecordListResponseDto(
                record.getRecordId(),
                record.getCreatedAt().toLocalDate(),
                record.getCreatedAt().toLocalTime()
        );
    }
}
