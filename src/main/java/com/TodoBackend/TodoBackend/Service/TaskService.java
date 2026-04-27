package com.TodoBackend.TodoBackend.Service;

import com.TodoBackend.TodoBackend.Model.AppTask;
import com.TodoBackend.TodoBackend.Model.AppUser;
import com.TodoBackend.TodoBackend.Repository.TaskRepository;
import com.TodoBackend.TodoBackend.Repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public AppTask createTask(AppTask task, String email){
        task.setTaskCreatedTime(LocalDateTime.now());
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        task.setUser(user);
        return taskRepository.save(task);
    }

    public Optional<AppTask> getTaskById(Long id){
        return taskRepository.findById(id);
    }

//    public List<AppTask> getAllTasks(){
//        return taskRepository.findAll();
//    }

    public List<AppTask> getTasksByEmail(String email){
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return taskRepository.findByUser(user);
    }

    public void deleteTaskByEmail(Long taskId, String email){
        AppTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean isOwner = task.getUser().getEmail().equals(email);

        if (!isOwner) {
            throw new SecurityException("You don't have permission to delete this task");
        }

        taskRepository.deleteById(taskId);
    }
//    public void deleteTask(Long id){
//        taskRepository.deleteById(id);
//    }
}
