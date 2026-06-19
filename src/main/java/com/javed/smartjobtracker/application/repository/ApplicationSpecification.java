package com.javed.smartjobtracker.application.repository;

import com.javed.smartjobtracker.application.entity.ApplicationStatus;
import com.javed.smartjobtracker.application.entity.JobApplication;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ApplicationSpecification {

    public static Specification<JobApplication> hasStatus(ApplicationStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<JobApplication> hasCompany(String companyName) {
        return (root, query, cb) -> {
            if (companyName == null || companyName.trim().isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get("companyName")), "%" + companyName.toLowerCase() + "%");
        };
    }

    public static Specification<JobApplication> hasDateBetween(LocalDate fromDate, LocalDate toDate) {
        return (root, query, cb) -> {
            if (fromDate == null && toDate == null) {
                return null;
            }
            if (fromDate != null && toDate != null) {
                return cb.between(root.get("applicationDate"), fromDate, toDate);
            }
            if (fromDate != null) {
                return cb.greaterThanOrEqualTo(root.get("applicationDate"), fromDate);
            }
            return cb.lessThanOrEqualTo(root.get("applicationDate"), toDate);
        };
    }

    public static Specification<JobApplication> hasUserId(Long userId) {
        return (root, query, cb) -> userId == null ? null : cb.equal(root.get("userId"), userId);
    }

    public static Specification<JobApplication> isNotDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }
}
