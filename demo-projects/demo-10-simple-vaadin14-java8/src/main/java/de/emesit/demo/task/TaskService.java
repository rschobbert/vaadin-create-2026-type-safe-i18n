package de.emesit.demo.task;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Task create(String description, LocalDate dueDate) {
        Task task = new Task(description, dueDate);
        return taskRepository.saveAndFlush(task);
    }

    @Transactional(readOnly = true)
    public List<Task> list() {
        return taskRepository.findAll();
    }

    @Transactional(readOnly = true)
    public long count() {
        return taskRepository.count();
    }
}
