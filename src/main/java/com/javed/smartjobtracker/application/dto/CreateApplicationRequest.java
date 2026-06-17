package com.javed.smartjobtracker.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateApplicationRequest {

    @NotBlank
    @Size(max = 100)
    private String companyName;

    @NotBlank
    @Size(max = 100)
    private String jobTitle;

    @NotNull
    private LocalDate applicationDate;

    @Size(max = 150)
    private String location;

    @Size(max = 50)
    private String salaryRange;

    @org.hibernate.validator.constraints.URL
    private String jobUrl;

    @Size(max = 2000)
    private String notes;

    private Long resumeFileId;
    private Long coverLetterFileId;
}