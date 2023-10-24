package br.com.gabriel.todolist.controllers;

import br.com.gabriel.todolist.repositories.ITaskRepository;
import br.com.gabriel.todolist.models.TaskModel;
import br.com.gabriel.todolist.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @Operation(security = @SecurityRequirement(name = "basicAuth"))
    @PostMapping("/")
    public ResponseEntity create (@RequestBody TaskModel taskModel, HttpServletRequest request) {
        try {
            var idUser = request.getAttribute("idUser");
            taskModel.setIdUser((UUID) idUser);

            var currentDate = LocalDateTime.now();
            if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio / data de termino deve ser maior que a data atual");
            }

            if(taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio deve ser menor que a data de termino");
            }

            var task = this.taskRepository.save(taskModel);

            return ResponseEntity.status(HttpStatus.CREATED).body(task);
        } catch (Exception error) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    @Operation(security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/")
    public ResponseEntity list (HttpServletRequest request) {
        try {
            var idUser = request.getAttribute("idUser");
            return ResponseEntity.status(HttpStatus.OK).body(this.taskRepository.findByIdUser((UUID) idUser));
        } catch (Exception error) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @Operation(security = @SecurityRequirement(name = "basicAuth"))
    @GetMapping("/{id}")
    public ResponseEntity getByid(@PathVariable UUID id, HttpServletRequest request) {
        try {
            var task = this.taskRepository.findById(id).orElse(null);

            var idUser = request.getAttribute("idUser");

            if(task == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa não encontrada!");
            }

            if(!task.getIdUser().equals(idUser)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário sem autorização para alterar essa tarefa!");
            }

            return ResponseEntity.status(HttpStatus.OK).body(task);
        } catch (Exception error) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @Operation(security = @SecurityRequirement(name = "basicAuth"))
    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request) {
        try {
            var task = this.taskRepository.findById(id).orElse(null);
            var idUser = request.getAttribute("idUser");

            if(task == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa não encontrada!");
            }

            if(!task.getIdUser().equals(idUser)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário sem autorização para alterar essa tarefa!");
            }

            Utils.copyNonNullProperties(taskModel, task);

            var taskUpdated = this.taskRepository.save(task);

            return ResponseEntity.status(HttpStatus.OK).body(taskUpdated);
        } catch (Exception error) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    @Operation(security = @SecurityRequirement(name = "basicAuth"))
    @DeleteMapping("/{id}")
    public ResponseEntity delete (@PathVariable UUID id, HttpServletRequest request) {
        try {
            var task = this.taskRepository.findById(id).orElse(null);
            var idUser = request.getAttribute("idUser");

            if(task == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa não encontrada!");
            }

            if(!task.getIdUser().equals(idUser)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário sem autorização para deletar essa tarefa!");
            }

            this.taskRepository.deleteById(task.getId());

            return ResponseEntity.status(HttpStatus.OK).body("Tarefa deletada com sucesso!");
        } catch (Exception error) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
