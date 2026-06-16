ALTER TABLE job_applications
    ADD COLUMN cover_letter_file_id BIGINT;


ALTER TABLE job_applications
    ADD COLUMN job_url VARCHAR(255);


ALTER TABLE job_applications
    ADD COLUMN location VARCHAR(255);


ALTER TABLE job_applications
    ADD COLUMN resume_file_id BIGINT;


ALTER TABLE job_applications
    ADD COLUMN salary_range VARCHAR(255);


ALTER TABLE job_applications
    ADD CONSTRAINT fk_job_applications_resume
        FOREIGN KEY (resume_file_id)
            REFERENCES file_uploads(id);