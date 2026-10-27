package de.emesit.demo.task;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.util.ProxyUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Task {
    public static final int DESCRIPTION_MAX_LENGTH = 300;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "uuid", nullable = false, length = 36)
    private String uuid = UUID.randomUUID().toString();

    @NotBlank(message = "Description must not be empty or blank")
    @Column(name = "description", nullable = false, length = DESCRIPTION_MAX_LENGTH)
    private String description = "";

    @Column(name = "due_date", nullable = true)
    private LocalDate dueDate;

    @Column(name = "created_on", nullable = true)
    private Instant createdOn;


    public Task(String description) {
        setDescription(description);
    }

    public Task(String description, LocalDate dueDate) {
        setDescription(description);
        setDueDate(dueDate);
    }

    /**
     * package private constructor to satisfy hibernate.
     */
    Task() {
    }

    @PrePersist
    private void prePersist() {
        this.createdOn = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        Objects.requireNonNull(description, "'description' must not be null'");
        if (description.length() > DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException("description=["+description+"] is longer than " + DESCRIPTION_MAX_LENGTH + " characters");
        }
        this.description = description;
    }

    public Instant getCreatedOn() {
        return createdOn;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public boolean equals(Object o) {
        if (null == o) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!getClass().equals(ProxyUtils.getUserClass(o))) {
            return false;
        }
        Task other = (Task)o;
        return getUuid() != null && getUuid().equals(other.getUuid());
    }

    @Override
    public int hashCode() {
        return getUuid().hashCode();
    }
}
