package com.TodoBackend.TodoBackend.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity
public class AppTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noteId;
    private String title;
    @Column(nullable = true)
    private String category;
    @Column(length = 1000)
    private String content;
    private LocalDateTime taskCreatedTime;
    private LocalDateTime reminderTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private AppUser user;

    public AppTask(Long noteId, String title, String category, String content, LocalDateTime taskCreatedTime, LocalDateTime reminderTime) {
        this.noteId = noteId;
        this.title = title;
        this.category = category;
        this.content = content;
        this.taskCreatedTime = taskCreatedTime;
        this.reminderTime = reminderTime;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public AppTask(String title, String category, String content, LocalDateTime taskCreatedTime, LocalDateTime reminderTime) {
        this.title = title;
        this.category = category;
        this.content = content;
        this.taskCreatedTime = taskCreatedTime;
        this.reminderTime = reminderTime;
    }

    public AppTask() {
    }

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTaskCreatedTime() {
        return taskCreatedTime;
    }

    public void setTaskCreatedTime(LocalDateTime taskCreatedTime) {
        this.taskCreatedTime = taskCreatedTime;
    }

    public LocalDateTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(LocalDateTime reminderTime) {
        this.reminderTime = reminderTime;
    }
}
