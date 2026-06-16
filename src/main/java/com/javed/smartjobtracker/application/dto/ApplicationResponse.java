package com.javed.smartjobtracker.application.dto;

import com.javed.smartjobtracker.application.entity.ApplicationStatus;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {

    private Long id;
    private String companyName;
    private String jobTitle;
    private LocalDate applicationDate;
    private ApplicationStatus status;

    private String location;
    private String salaryRange;
    private String jobUrl;
    private String notes;

    private Instant createdAt;
    private Instant updatedAt;
}