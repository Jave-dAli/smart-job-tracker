package com.javed.smartjobtracker.application.entity;

import com.javed.smartjobtracker.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "job_applications")
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String companyName;
    private String jobTitle;
    private String status;
    private LocalDate applicationDate;
    private String notes;
}