package com.javed.smartjobtracker.application.service;

import com.javed.smartjobtracker.application.dto.CreateApplicationRequest;
import com.javed.smartjobtracker.application.dto.UpdateApplicationRequest;
import com.javed.smartjobtracker.application.dto.ApplicationResponse;
import com.javed.smartjobtracker.application.entity.JobApplication;
import com.javed.smartjobtracker.application.entity.ApplicationStatus;
import com.javed.smartjobtracker.application.repository.ApplicationRepository;
import com.javed.smartjobtracker.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.javed.smartjobtracker.application.repository.ApplicationSpecification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        return repository.findByUserIdAndDeletedAtIsNull(userId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    public Page<ApplicationResponse> getApplications(Long userId, ApplicationStatus status, String company, LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        Sort mappedSort = Sort.unsorted();
        if (pageable.getSort().isSorted()) {
            List<Sort.Order> mappedOrders = new ArrayList<>();
            for (Sort.Order order : pageable.getSort()) {
                String property = order.getProperty();
                if (!property.equals("dateApplied") && !property.equals("companyName")
                        && !property.equals("position") && !property.equals("createdAt")) {
                    throw new IllegalArgumentException("Invalid sort field: " + property);
                }
                String mappedProperty = switch (property) {
                    case "dateApplied" -> "applicationDate";
                    case "position" -> "jobTitle";
                    default -> property;
                };
                mappedOrders.add(new Sort.Order(order.getDirection(), mappedProperty));
            }
            mappedSort = Sort.by(mappedOrders);
        }
        Pageable mappedPageable = org.springframework.data.domain.PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                mappedSort
        );

        Specification<JobApplication> spec = Specification.where(ApplicationSpecification.hasUserId(userId))
                .and(ApplicationSpecification.isNotDeleted());

        if (status != null) {
            spec = spec.and(ApplicationSpecification.hasStatus(status));
        }
        if (company != null && !company.trim().isEmpty()) {
            spec = spec.and(ApplicationSpecification.hasCompany(company));
        }
        if (fromDate != null || toDate != null) {
            spec = spec.and(ApplicationSpecification.hasDateBetween(fromDate, toDate));
        }

        return repository.findAll(spec, mappedPageable).map(this::map);
    }

    public ApplicationResponse updateStatus(Long id, Long userId, ApplicationStatus newStatus) {

        JobApplication app = repository.findByIdAndUserIdAndDeletedAtIsNull(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        app.getStatus().validateTransition(newStatus);

        app.setStatus(newStatus);

        return map(repository.save(app));
    }

    public ApplicationResponse update(Long id, Long userId, UpdateApplicationRequest request) {

        JobApplication app = repository.findByIdAndUserIdAndDeletedAtIsNull(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        app.setCompanyName(request.getCompanyName());
        app.setJobTitle(request.getJobTitle());
        app.setApplicationDate(request.getApplicationDate());
        app.setLocation(request.getLocation());
        app.setSalaryRange(request.getSalaryRange());
        app.setJobUrl(request.getJobUrl());
        app.setNotes(request.getNotes());
        app.setResumeFileId(request.getResumeFileId());
        app.setCoverLetterFileId(request.getCoverLetterFileId());

        return map(repository.save(app));
    }

    public void delete(Long id, Long userId) {

        JobApplication app = repository.findByIdAndUserIdAndDeletedAtIsNull(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        app.setDeletedAt(LocalDateTime.now());

        repository.save(app);
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
                .resumeFileId(app.getResumeFileId())
                .coverLetterFileId(app.getCoverLetterFileId())
                .createdAt(app.getCreatedAt())
                .updatedAt(app.getUpdatedAt())
                .build();
    }

    public ApplicationResponse getById(Long id, Long userId) {
        JobApplication app = repository.findByIdAndUserIdAndDeletedAtIsNull(id, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Application not found")
                );

        return map(app);
    }
}