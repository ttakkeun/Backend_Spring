package ttakkeun.ttakkeun_server.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttakkeun.ttakkeun_server.dto.CompletionRateDto;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.entity.enums.TodoStatus;
import ttakkeun.ttakkeun_server.repository.TodoRepository;

@Service
public class CompletionRateService {

    private final TodoRepository todoRepository;

    public CompletionRateService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Transactional(readOnly = true)
    public CompletionRateDto getCompletionRate(Long petId) {

        int earTotal = (int) todoRepository.countByCategoryAndPetId(Category.EAR, petId);
        int hairTotal = (int) todoRepository.countByCategoryAndPetId(Category.HAIR, petId);
        int clawTotal = (int) todoRepository.countByCategoryAndPetId(Category.CLAW, petId);
        int eyeTotal = (int) todoRepository.countByCategoryAndPetId(Category.EYE, petId);
        int teethTotal = (int) todoRepository.countByCategoryAndPetId(Category.TEETH, petId);

        int earCompleted = (int) todoRepository.countByCategoryAndPetIdAndStatus(Category.EAR, petId, TodoStatus.DONE);
        int hairCompleted = (int) todoRepository.countByCategoryAndPetIdAndStatus(Category.HAIR, petId, TodoStatus.DONE);
        int clawCompleted = (int) todoRepository.countByCategoryAndPetIdAndStatus(Category.CLAW, petId, TodoStatus.DONE);
        int eyeCompleted = (int) todoRepository.countByCategoryAndPetIdAndStatus(Category.EYE, petId, TodoStatus.DONE);
        int teethCompleted = (int) todoRepository.countByCategoryAndPetIdAndStatus(Category.TEETH, petId, TodoStatus.DONE);

        return new CompletionRateDto(
                earTotal, earCompleted,
                hairTotal, hairCompleted,
                clawTotal, clawCompleted,
                eyeTotal, eyeCompleted,
                teethTotal, teethCompleted
        );
    }
}
