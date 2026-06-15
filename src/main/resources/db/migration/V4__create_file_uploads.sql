CREATE TABLE IF NOT EXISTS file_uploads (

                              id BIGSERIAL PRIMARY KEY,

                              job_application_id BIGINT NOT NULL,

                              file_name VARCHAR(255),

                              file_path VARCHAR(500),

                              uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                              CONSTRAINT fk_file_application
                                  FOREIGN KEY (job_application_id)
                                      REFERENCES job_applications(id)
);