package com.javed.smartjobtracker.application.repository;

import org.springframework.stereotype.Repository;
import com.javed.smartjobtracker.application.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByUserId(Long userId);

    Optional<JobApplication> findByIdAndUserId(Long id, Long userId);

    long countByUserId(Long userId);
}