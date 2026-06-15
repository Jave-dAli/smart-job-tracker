package com.javed.smartjobtracker.fileupload.entity;

import com.javed.smartjobtracker.application.entity.JobApplication;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_uploads")
public class FileUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_application_id", nullable = false)
    private JobApplication jobApplication;

    private String fileName;
    private String filePath;
    private LocalDateTime uploadedAt;
}