package com.javed.smartjobtracker.application.controller;

import com.javed.smartjobtracker.application.dto.CreateApplicationRequest;
import com.javed.smartjobtracker.application.dto.ApplicationResponse;
import com.javed.smartjobtracker.application.dto.UpdateStatusRequest;
import com.javed.smartjobtracker.application.service.ApplicationService;
import com.javed.smartjobtracker.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService service;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<ApplicationResponse> create(
            @RequestBody CreateApplicationRequest request
    ) {

        Long userId = securityUtils.getCurrentUserId();

        return ResponseEntity.ok(
                service.create(request, userId)
        );
    }

    // GET ALL MY APPLICATIONS
    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> getMyApplications() {

        Long userId = securityUtils.getCurrentUserId();

        return ResponseEntity.ok(
                service.getMyApplications(userId)
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
}