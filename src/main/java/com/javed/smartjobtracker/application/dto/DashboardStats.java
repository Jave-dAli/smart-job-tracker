package com.javed.smartjobtracker.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private long totalApplications;
    private long appliedCount;
    private long shortlistedCount;
    private long assessmentCount;
    private long interviewCount;
    private long offerCount;
    private long acceptedCount;
    private long rejectedCount;
}
