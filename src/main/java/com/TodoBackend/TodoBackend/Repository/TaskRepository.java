package com.TodoBackend.TodoBackend.Repository;

import com.TodoBackend.TodoBackend.Model.AppTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<AppTask, Long> {
}
