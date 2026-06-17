package com.javed.smartjobtracker.application.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications",
        indexes = {
                @Index(name = "idx_job_app_user_id", columnList = "user_id"),
                @Index(name = "idx_job_app_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // IMPORTANT: keep consistency with your User entity (Long id)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotBlank
    @Column(name="company_name", length = 100, nullable = false)
    private String companyName;

    @NotBlank
    @Column(name="job_title", length = 100, nullable = false)
    private String jobTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @NotNull
    @Column(name = "application_date")
    private LocalDate applicationDate;

    @Column(length = 150)
    private String location;

    @Column(name="salary_range", length = 50)
    private String salaryRange;

    @Column(name="job_url", length = 500)
    private String jobUrl;

    @Column(length = 2000)
    private String notes;

    @Column(name="resume_file_id")
    private Long resumeFileId;

    @Column(name="cover_letter_file_id")
    private Long coverLetterFileId;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}