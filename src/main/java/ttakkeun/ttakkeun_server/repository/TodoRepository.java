package ttakkeun.ttakkeun_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ttakkeun.ttakkeun_server.entity.Pet;
import ttakkeun.ttakkeun_server.entity.Todo;
import ttakkeun.ttakkeun_server.entity.enums.Category;
import ttakkeun.ttakkeun_server.entity.enums.TodoStatus;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByTodoDateAndPet(LocalDate selectDate, Pet pet);

    long countByTodoCategoryAndPet(Category todoCategory, Pet pet);
    long countByTodoCategoryAndPetAndTodoStatus(Category todoCategory, Pet pet, TodoStatus todoStatus);
}
