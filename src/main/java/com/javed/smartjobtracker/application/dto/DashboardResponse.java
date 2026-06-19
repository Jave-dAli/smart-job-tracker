package com.javed.smartjobtracker.application.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private long totalApplications;
    private Map<String, Long> applicationsByStatus;
    private long interviewCount;
    private long rejectedCount;
    private long offerCount;
    private long acceptedCount;
    private double responseRate;
    private double offerRate;
}
