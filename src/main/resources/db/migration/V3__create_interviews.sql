CREATE TABLE interviews (

                            id BIGSERIAL PRIMARY KEY,

                            job_application_id BIGINT NOT NULL,

                            interview_date TIMESTAMP,

                            interview_type VARCHAR(50),

                            notes TEXT,

                            CONSTRAINT fk_interview_application
                                FOREIGN KEY (job_application_id)
                                    REFERENCES job_applications(id)
);