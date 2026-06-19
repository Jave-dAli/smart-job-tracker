package com.javed.smartjobtracker.application.repository;

import org.springframework.stereotype.Repository;
import com.javed.smartjobtracker.application.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<JobApplication, Long>, JpaSpecificationExecutor<JobApplication> {

    List<JobApplication> findByUserId(Long userId);

    List<JobApplication> findByUserIdAndDeletedAtIsNull(Long userId);

    Optional<JobApplication> findByIdAndUserId(Long id, Long userId);

    Optional<JobApplication> findByIdAndUserIdAndDeletedAtIsNull(Long id, Long userId);

    long countByUserId(Long userId);
}