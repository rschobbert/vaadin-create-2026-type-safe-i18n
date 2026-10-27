package de.emesit.demo.task;

import java.time.LocalDate;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@DataJpaTest // Configures an in-memory database and JPA environment
class TaskJpaProxyTest implements WithAssertions {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should successfully match equality between real Entity and Hibernate Proxy")
    void shouldEvaluateEqualsCorrectlyWithHibernateProxy() {
        // Given: Save a task and flush it to the database
        Task realTask = new Task("Test proxy equality", LocalDate.now());
        realTask = taskRepository.saveAndFlush(realTask);

        // Clear the persistence context cache to force Hibernate to create a proxy
        entityManager.clear();

        // When: Fetch a lazy reference (this returns a Hibernate Proxy, not the real class)
        Task proxyTask = taskRepository.getReferenceById(realTask.getId());

        // Then: Verify it is actually a proxy class
        assertThat(proxyTask.getClass())
                .as("The fetched task should be a Hibernate Proxy class")
                .isNotEqualTo(Task.class);

        // Verify the equals contract from both sides
        assertThat(realTask)
                .as("Real entity should equal the proxy entity since UUIDs match")
                .isEqualTo(proxyTask);

        assertThat(proxyTask)
                .as("Proxy entity should equal the real entity (Symmetry)")
                .isEqualTo(realTask);

        // Verify hashCode contract remains identical
        assertThat(realTask.hashCode())
                .as("HashCodes of real entity and proxy must be identical")
                .isEqualTo(proxyTask.hashCode());
    }
}
