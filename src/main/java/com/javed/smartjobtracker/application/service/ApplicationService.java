package com.javed.smartjobtracker.application.service;

import com.javed.smartjobtracker.application.dto.CreateApplicationRequest;
import com.javed.smartjobtracker.application.dto.ApplicationResponse;
import com.javed.smartjobtracker.application.entity.JobApplication;
import com.javed.smartjobtracker.application.entity.ApplicationStatus;
import com.javed.smartjobtracker.application.repository.ApplicationRepository;
import com.javed.smartjobtracker.application.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository repository;

    public ApplicationResponse create(CreateApplicationRequest req, Long userId) {

        JobApplication app = JobApplication.builder()
                .userId(userId)
                .companyName(req.getCompanyName())
                .jobTitle(req.getJobTitle())
                .applicationDate(req.getApplicationDate())
                .status(ApplicationStatus.APPLIED)
                .location(req.getLocation())
                .salaryRange(req.getSalaryRange())
                .jobUrl(req.getJobUrl())
                .notes(req.getNotes())
                .resumeFileId(req.getResumeFileId())
                .coverLetterFileId(req.getCoverLetterFileId())
                .build();

        return map(repository.save(app));
    }

    public List<ApplicationResponse> getMyApplications(Long userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    public ApplicationResponse updateStatus(Long id, Long userId, ApplicationStatus newStatus) {

        JobApplication app = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        app.getStatus().validateTransition(newStatus);

        app.setStatus(newStatus);

        return map(repository.save(app));
    }

    private ApplicationResponse map(JobApplication app) {
        return ApplicationResponse.builder()
                .id(app.getId())
                .companyName(app.getCompanyName())
                .jobTitle(app.getJobTitle())
                .applicationDate(app.getApplicationDate())
                .status(app.getStatus())
                .location(app.getLocation())
                .salaryRange(app.getSalaryRange())
                .jobUrl(app.getJobUrl())
                .notes(app.getNotes())
                .createdAt(app.getCreatedAt())
                .updatedAt(app.getUpdatedAt())
                .build();
    }

    public ApplicationResponse getById(Long id, Long userId) {
        JobApplication app = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() ->
                        new RuntimeException("Application not found")
                );

        return map(app);
    }
}