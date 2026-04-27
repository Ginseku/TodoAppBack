package com.TodoBackend.TodoBackend.Repository;

import com.TodoBackend.TodoBackend.Model.AppTask;
import com.TodoBackend.TodoBackend.Model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<AppTask, Long> {
    List<AppTask> findByUser(AppUser user);
}
