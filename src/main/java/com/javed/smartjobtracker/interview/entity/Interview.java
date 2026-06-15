package com.javed.smartjobtracker.interview.entity;

import com.javed.smartjobtracker.application.entity.JobApplication;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "interviews")
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_application_id", nullable = false)
    private JobApplication jobApplication;

    private LocalDateTime interviewDate;
    private String interviewType;
    private String notes;
}