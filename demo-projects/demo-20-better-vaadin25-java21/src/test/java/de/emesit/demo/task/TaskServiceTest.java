package de.emesit.demo.task;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest implements WithAssertions {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Captor
    private ArgumentCaptor<Task> taskCaptor;

    @Test
    @DisplayName("Should successfully create and flush a new task")
    void shouldCreateTaskSuccessfully() {
        // Given
        String description = "Finish i18n library";
        LocalDate dueDate = LocalDate.now().plusDays(7);
        Task expectedTask = new Task(description, dueDate);

        // Mocking repository behavior
        when(taskRepository.saveAndFlush(any(Task.class))).thenReturn(expectedTask);

        // When
        Task createdTask = taskService.create(description, dueDate);

        // Then
        assertThat(createdTask)
                .as("The created task should not be null")
                .isNotNull();
        assertThat(createdTask.getDescription()).isEqualTo(description);
        assertThat(createdTask.getDueDate()).isEqualTo(dueDate);

        // Verify repository interactions and capture the passed argument
        verify(taskRepository).saveAndFlush(taskCaptor.capture());
        Task capturedTask = taskCaptor.getValue();
        
        assertThat(capturedTask.getDescription())
                .as("The saved task description should match the input")
                .isEqualTo(description);
        assertThat(capturedTask.getDueDate())
                .as("The saved task due date should match the input")
                .isEqualTo(dueDate);
    }

    @Test
    @DisplayName("Should return a list of all existing tasks")
    void shouldListAllTasks() {
        // Given
        List<Task> mockTasks = List.of(
            new Task("Task 1", LocalDate.now()),
            new Task("Task 2", LocalDate.now().plusDays(1))
        );
        when(taskRepository.findAll()).thenReturn(mockTasks);

        // When
        List<Task> result = taskService.list();

        // Then
        assertThat(result)
                .as("The resulting list should contain exactly the mocked tasks")
                .hasSize(2)
                .containsExactlyElementsOf(mockTasks);
                
        verify(taskRepository).findAll();
    }

    @Test
    @DisplayName("Should return the correct count of all tasks")
    void shouldCountTasks() {
        // Given
        long expectedCount = 42L;
        when(taskRepository.count()).thenReturn(expectedCount);

        // When
        long actualCount = taskService.count();

        // Then
        assertThat(actualCount)
                .as("The total task count should match the repository response")
                .isEqualTo(expectedCount);
                
        verify(taskRepository).count();
    }
}
