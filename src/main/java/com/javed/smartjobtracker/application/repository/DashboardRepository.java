package com.javed.smartjobtracker.application.repository;

import com.javed.smartjobtracker.application.dto.DashboardStats;
import com.javed.smartjobtracker.application.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardRepository extends JpaRepository<JobApplication, Long> {

    @Query("SELECT new com.javed.smartjobtracker.application.dto.DashboardStats(" +
            "COUNT(a), " +
            "COUNT(CASE WHEN a.status = com.javed.smartjobtracker.application.entity.ApplicationStatus.APPLIED THEN 1 END), " +
            "COUNT(CASE WHEN a.status = com.javed.smartjobtracker.application.entity.ApplicationStatus.SHORTLISTED THEN 1 END), " +
            "COUNT(CASE WHEN a.status = com.javed.smartjobtracker.application.entity.ApplicationStatus.ASSESSMENT THEN 1 END), " +
            "COUNT(CASE WHEN a.status = com.javed.smartjobtracker.application.entity.ApplicationStatus.INTERVIEW THEN 1 END), " +
            "COUNT(CASE WHEN a.status = com.javed.smartjobtracker.application.entity.ApplicationStatus.OFFER THEN 1 END), " +
            "COUNT(CASE WHEN a.status = com.javed.smartjobtracker.application.entity.ApplicationStatus.ACCEPTED THEN 1 END), " +
            "COUNT(CASE WHEN a.status = com.javed.smartjobtracker.application.entity.ApplicationStatus.REJECTED THEN 1 END)" +
            ") " +
            "FROM JobApplication a " +
            "WHERE a.deletedAt IS NULL")
    DashboardStats getSystemStats();

    @Query("SELECT new com.javed.smartjobtracker.application.dto.DashboardStats(" +
            "COUNT(a), " +
            "COUNT(CASE WHEN a.status = com.javed.smartjobtracker.application.entity.ApplicationStatus.APPLIED THEN 1 END), " +
            "COUNT(CASE WHEN a.status = com.javed.smartjobtracker.application.entity.ApplicationStatus.SHORTLISTED THEN 1 END), " +
            "COUNT(CASE WHEN a.status = com.javed.smartjobtracker.application.entity.ApplicationStatus.ASSESSMENT THEN 1 END), " +
            "COUNT(CASE WHEN a.status = com.javed.smartjobtracker.application.entity.ApplicationStatus.INTERVIEW THEN 1 END), " +
            "COUNT(CASE WHEN a.status = com.javed.smartjobtracker.application.entity.ApplicationStatus.OFFER THEN 1 END), " +
            "COUNT(CASE WHEN a.status = com.javed.smartjobtracker.application.entity.ApplicationStatus.ACCEPTED THEN 1 END), " +
            "COUNT(CASE WHEN a.status = com.javed.smartjobtracker.application.entity.ApplicationStatus.REJECTED THEN 1 END)" +
            ") " +
            "FROM JobApplication a " +
            "WHERE a.userId = :userId AND a.deletedAt IS NULL")
    DashboardStats getUserStats(@Param("userId") Long userId);
}
