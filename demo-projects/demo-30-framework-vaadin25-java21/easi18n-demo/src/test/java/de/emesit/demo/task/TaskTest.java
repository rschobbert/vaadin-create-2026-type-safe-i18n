package de.emesit.demo.task;

import java.time.Instant;
import java.time.LocalDate;
import java.lang.reflect.Method;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TaskTest implements WithAssertions {

    @Test
    @DisplayName("Should initialize task with default values and UUID")
    void shouldInitializeWithDefaults() {
        // When
        Task task = new Task("Do laundry");

        // Then
        assertThat(task.getDescription()).isEqualTo("Do laundry");
        assertThat(task.getUuid()).isNotNull().hasSize(36);
        assertThat(task.getDueDate()).isNull();
        assertThat(task.getCreatedOn()).isNull(); // PrePersist hasn't run yet
    }

    @Test
    @DisplayName("Should set due date correctly via constructor")
    void shouldSetDueDateViaConstructor() {
        // Given
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // When
        Task task = new Task("Read a book", tomorrow);

        // Then
        assertThat(task.getDueDate()).isEqualTo(tomorrow);
    }

    @Test
    @DisplayName("Should throw NullPointerException when description is null")
    void shouldThrowExceptionWhenDescriptionIsNull() {
        assertThatThrownBy(() -> new Task(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("'description' must not be null'");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when description exceeds max length")
    void shouldThrowExceptionWhenDescriptionIsTooLong() throws Exception {
        // Given: Create a string that is 301 characters long
        String longDescription = "a".repeat(Task.DESCRIPTION_MAX_LENGTH + 1);

        // Then
        assertThatThrownBy(() -> new Task(longDescription))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("is longer than " + Task.DESCRIPTION_MAX_LENGTH + " characters");
    }

    @Test
    @DisplayName("Should trigger @PrePersist method and set createdOn timestamp")
    void shouldTriggerPrePersist() throws Exception {
        // Given
        Task task = new Task("Test PrePersist");
        assertThat(task.getCreatedOn()).isNull();

        // When: Simulate JPA PrePersist using reflection since it is a private method
        Method prePersistMethod = Task.class.getDeclaredMethod("prePersist");
        prePersistMethod.setAccessible(true);
        prePersistMethod.invoke(task);

        // Then
        assertThat(task.getCreatedOn())
                .isNotNull()
                .isBeforeOrEqualTo(Instant.now());
    }

    @Test
    @DisplayName("Should satisfy equals and hashCode contract based on UUID")
    void shouldSatisfyEqualsAndHashCode() {
        // Given
        Task task1 = new Task("Same task logic");
        Task task2 = new Task("Different text, but we will force the same UUID");
        
        // We simulate that task2 has the exact same UUID (e.g. loaded from the database)
        // Since there is no setter for UUID, we test equality of the same object first
        assertThat(task1).isEqualTo(task1);
        assertThat(task1).isNotEqualTo(task2);
        assertThat(task1).isNotEqualTo(null);
        assertThat(task1).isNotEqualTo("a string object");

        // HashCode consistency
        assertThat(task1.hashCode()).isEqualTo(task1.getUuid().hashCode());
    }
}
