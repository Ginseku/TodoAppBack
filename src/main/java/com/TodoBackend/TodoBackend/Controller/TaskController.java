package com.TodoBackend.TodoBackend.Controller;

import com.TodoBackend.TodoBackend.Model.AppTask;
import com.TodoBackend.TodoBackend.Model.AppUser;
import com.TodoBackend.TodoBackend.Repository.UserRepository;
import com.TodoBackend.TodoBackend.Service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {
    private final TaskService taskService;
    private final UserRepository userRepository;

    @Autowired
    public TaskController(TaskService taskService, UserRepository userRepository) {
        this.taskService = taskService;
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<AppTask> createTask(@RequestBody AppTask task, Principal principal) {
        String email = principal.getName();

        AppTask saved = taskService.createTask(task, email);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/getAll")
    public List<AppTask> getAllTask(Principal principal){
        String email = principal.getName();
        return taskService.getTasksByEmail(email);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<AppTask> getTaskById(@PathVariable Long id){
        return taskService.getTaskById(id)
                .map(note -> new ResponseEntity<>(note, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Principal principal){
        String email = principal.getName();
        taskService.deleteTaskByEmail(id, email);
        return ResponseEntity.ok().build();
    }
}
