package com.javed.smartjobtracker.application.service;

import com.javed.smartjobtracker.application.dto.DashboardResponse;
import com.javed.smartjobtracker.application.dto.DashboardStats;
import com.javed.smartjobtracker.application.repository.DashboardRepository;
import com.javed.smartjobtracker.user.entity.Role;
import com.javed.smartjobtracker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    public DashboardResponse getDashboard(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }

        User user = (User) authentication.getPrincipal();
        boolean isAdmin = user.getRole() == Role.ADMIN;

        DashboardStats stats = isAdmin 
                ? dashboardRepository.getSystemStats() 
                : dashboardRepository.getUserStats(user.getId());

        long total = stats.getTotalApplications();
        long applied = stats.getAppliedCount();
        long shortlisted = stats.getShortlistedCount();
        long assessment = stats.getAssessmentCount();
        long interview = stats.getInterviewCount();
        long offer = stats.getOfferCount();
        long accepted = stats.getAcceptedCount();
        long rejected = stats.getRejectedCount();

        long responses = total - applied;
        double responseRate = total == 0 ? 0.0 : ((double) responses / total) * 100.0;

        long offers = offer + accepted;
        double offerRate = total == 0 ? 0.0 : ((double) offers / total) * 100.0;

        Map<String, Long> applicationsByStatus = Map.of(
                "APPLIED", applied,
                "SHORTLISTED", shortlisted,
                "ASSESSMENT", assessment,
                "INTERVIEW", interview,
                "OFFER", offer,
                "ACCEPTED", accepted,
                "REJECTED", rejected
        );

        return DashboardResponse.builder()
                .totalApplications(total)
                .applicationsByStatus(applicationsByStatus)
                .interviewCount(interview)
                .rejectedCount(rejected)
                .offerCount(offer)
                .acceptedCount(accepted)
                .responseRate(responseRate)
                .offerRate(offerRate)
                .build();
    }
}
