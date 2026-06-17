package com.javed.smartjobtracker.application.service;

import com.javed.smartjobtracker.application.dto.ApplicationResponse;
import com.javed.smartjobtracker.application.dto.CreateApplicationRequest;
import com.javed.smartjobtracker.application.dto.UpdateApplicationRequest;
import com.javed.smartjobtracker.application.entity.ApplicationStatus;
import com.javed.smartjobtracker.application.entity.JobApplication;
import com.javed.smartjobtracker.application.repository.ApplicationRepository;
import com.javed.smartjobtracker.common.exception.InvalidStatusTransitionException;
import com.javed.smartjobtracker.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository repository;

    @InjectMocks
    private ApplicationService service;

    @Test
    void create_success() {
        CreateApplicationRequest req = new CreateApplicationRequest();
        req.setCompanyName("Google");
        req.setJobTitle("Software Engineer");
        req.setApplicationDate(LocalDate.now());

        JobApplication app = JobApplication.builder()
                .id(1L)
                .userId(1L)
                .companyName("Google")
                .jobTitle("Software Engineer")
                .applicationDate(req.getApplicationDate())
                .status(ApplicationStatus.APPLIED)
                .build();

        when(repository.save(any(JobApplication.class))).thenReturn(app);

        ApplicationResponse res = service.create(req, 1L);

        assertNotNull(res);
        assertEquals(1L, res.getId());
        assertEquals("Google", res.getCompanyName());
        assertEquals("Software Engineer", res.getJobTitle());
        assertEquals(ApplicationStatus.APPLIED, res.getStatus());
    }

    @Test
    void findById_success() {
        JobApplication app = JobApplication.builder()
                .id(1L)
                .userId(1L)
                .companyName("Google")
                .jobTitle("Software Engineer")
                .applicationDate(LocalDate.now())
                .status(ApplicationStatus.APPLIED)
                .build();

        when(repository.findByIdAndUserIdAndDeletedAtIsNull(1L, 1L)).thenReturn(Optional.of(app));

        ApplicationResponse res = service.getById(1L, 1L);

        assertNotNull(res);
        assertEquals(1L, res.getId());
        assertEquals("Google", res.getCompanyName());
    }

    @Test
    void findById_notFound() {
        when(repository.findByIdAndUserIdAndDeletedAtIsNull(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(1L, 1L));
    }

    @Test
    void update_success() {
        JobApplication existing = JobApplication.builder()
                .id(1L)
                .userId(1L)
                .companyName("Google")
                .jobTitle("Software Engineer")
                .applicationDate(LocalDate.now())
                .status(ApplicationStatus.APPLIED)
                .build();

        UpdateApplicationRequest req = new UpdateApplicationRequest();
        req.setCompanyName("Alphabet");
        req.setJobTitle("Senior Software Engineer");
        req.setApplicationDate(LocalDate.now().minusDays(1));
        req.setLocation("New York");
        req.setSalaryRange("100k-150k");
        req.setJobUrl("https://careers.google.com");
        req.setNotes("Referral");
        req.setResumeFileId(10L);
        req.setCoverLetterFileId(20L);

        when(repository.findByIdAndUserIdAndDeletedAtIsNull(1L, 1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(JobApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApplicationResponse res = service.update(1L, 1L, req);

        assertNotNull(res);
        assertEquals("Alphabet", res.getCompanyName());
        assertEquals("Senior Software Engineer", res.getJobTitle());
        assertEquals("New York", res.getLocation());
        assertEquals("100k-150k", res.getSalaryRange());
        assertEquals("https://careers.google.com", res.getJobUrl());
        assertEquals("Referral", res.getNotes());
        assertEquals(10L, res.getResumeFileId());
        assertEquals(20L, res.getCoverLetterFileId());
    }

    @Test
    void update_notFound() {
        UpdateApplicationRequest req = new UpdateApplicationRequest();
        when(repository.findByIdAndUserIdAndDeletedAtIsNull(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, 1L, req));
    }

    @Test
    void delete_success() {
        JobApplication existing = JobApplication.builder()
                .id(1L)
                .userId(1L)
                .companyName("Google")
                .jobTitle("Software Engineer")
                .applicationDate(LocalDate.now())
                .status(ApplicationStatus.APPLIED)
                .build();

        when(repository.findByIdAndUserIdAndDeletedAtIsNull(1L, 1L)).thenReturn(Optional.of(existing));

        service.delete(1L, 1L);

        assertNotNull(existing.getDeletedAt());
        verify(repository).save(existing);
    }

    @Test
    void delete_notFound() {
        when(repository.findByIdAndUserIdAndDeletedAtIsNull(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L, 1L));
    }

    @Test
    void valid_status_transition() {
        JobApplication existing = JobApplication.builder()
                .id(1L)
                .userId(1L)
                .companyName("Google")
                .jobTitle("Software Engineer")
                .applicationDate(LocalDate.now())
                .status(ApplicationStatus.APPLIED)
                .build();

        when(repository.findByIdAndUserIdAndDeletedAtIsNull(1L, 1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(JobApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApplicationResponse res = service.updateStatus(1L, 1L, ApplicationStatus.SHORTLISTED);

        assertNotNull(res);
        assertEquals(ApplicationStatus.SHORTLISTED, res.getStatus());
    }

    @Test
    void invalid_status_transition() {
        JobApplication existing = JobApplication.builder()
                .id(1L)
                .userId(1L)
                .companyName("Google")
                .jobTitle("Software Engineer")
                .applicationDate(LocalDate.now())
                .status(ApplicationStatus.APPLIED)
                .build();

        when(repository.findByIdAndUserIdAndDeletedAtIsNull(1L, 1L)).thenReturn(Optional.of(existing));

        assertThrows(InvalidStatusTransitionException.class, () -> service.updateStatus(1L, 1L, ApplicationStatus.OFFER));
    }

    @Test
    void getMyApplications() {
        JobApplication app1 = JobApplication.builder()
                .id(1L)
                .userId(1L)
                .companyName("Google")
                .jobTitle("Software Engineer")
                .applicationDate(LocalDate.now())
                .status(ApplicationStatus.APPLIED)
                .build();
        JobApplication app2 = JobApplication.builder()
                .id(2L)
                .userId(1L)
                .companyName("Meta")
                .jobTitle("Product Manager")
                .applicationDate(LocalDate.now())
                .status(ApplicationStatus.APPLIED)
                .build();

        when(repository.findByUserIdAndDeletedAtIsNull(1L)).thenReturn(List.of(app1, app2));

        List<ApplicationResponse> list = service.getMyApplications(1L);

        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("Google", list.get(0).getCompanyName());
        assertEquals("Meta", list.get(1).getCompanyName());
    }
}
