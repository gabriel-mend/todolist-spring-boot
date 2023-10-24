package br.com.gabriel.todolist.repositories;

import br.com.gabriel.todolist.models.TaskModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ITaskRepository extends JpaRepository<TaskModel, UUID> {
    List<TaskModel> findByIdUser (UUID id);
}
