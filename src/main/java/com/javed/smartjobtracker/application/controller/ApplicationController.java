package com.javed.smartjobtracker.application.controller;

import com.javed.smartjobtracker.application.dto.CreateApplicationRequest;
import com.javed.smartjobtracker.application.dto.UpdateApplicationRequest;
import com.javed.smartjobtracker.application.dto.ApplicationResponse;
import com.javed.smartjobtracker.application.dto.UpdateStatusRequest;
import com.javed.smartjobtracker.application.service.ApplicationService;
import com.javed.smartjobtracker.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.javed.smartjobtracker.application.entity.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService service;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<ApplicationResponse> create(
            @Valid @RequestBody CreateApplicationRequest request
    ) {

        Long userId = securityUtils.getCurrentUserId();

        return ResponseEntity.ok(
                service.create(request, userId)
        );
    }

    // GET ALL MY APPLICATIONS WITH PAGINATION, SORTING, AND FILTERING
    @GetMapping
    public ResponseEntity<Page<ApplicationResponse>> getApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate
    ) {

        Long userId = securityUtils.getCurrentUserId();

        Sort sortObj = Sort.unsorted();
        if (sort != null && !sort.trim().isEmpty()) {
            Sort.Direction dir = Sort.Direction.fromString(direction.toUpperCase());
            sortObj = Sort.by(dir, sort);
        }
        Pageable pageable = PageRequest.of(page, size, sortObj);

        return ResponseEntity.ok(
                service.getApplications(userId, status, company, fromDate, toDate, pageable)
        );
    }


    // GET SINGLE APPLICATION
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getById(
            @PathVariable Long id
    ) {

        Long userId = securityUtils.getCurrentUserId();

        return ResponseEntity.ok(
                service.getById(id, userId)
        );
    }


    // UPDATE APPLICATION STATUS
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request
    ) {

        Long userId = securityUtils.getCurrentUserId();

        return ResponseEntity.ok(
                service.updateStatus(
                        id,
                        userId,
                        request.getStatus()
                )
        );
    }


    // UPDATE APPLICATION
    @PutMapping("/{id}")
    public ResponseEntity<ApplicationResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateApplicationRequest request
    ) {

        Long userId = securityUtils.getCurrentUserId();

        return ResponseEntity.ok(
                service.update(id, userId, request)
        );
    }


    // DELETE APPLICATION (SOFT DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {

        Long userId = securityUtils.getCurrentUserId();

        service.delete(id, userId);

        return ResponseEntity.noContent().build();
    }
}