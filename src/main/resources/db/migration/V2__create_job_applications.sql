CREATE TABLE job_applications (
                                  id BIGSERIAL PRIMARY KEY,

                                  user_id BIGINT NOT NULL,

                                  company_name VARCHAR(255) NOT NULL,
                                  job_title VARCHAR(255) NOT NULL,

                                  status VARCHAR(50),
                                  application_date DATE,
                                  notes TEXT,

                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                  CONSTRAINT fk_job_user
                                      FOREIGN KEY (user_id)
                                          REFERENCES users(id)
);