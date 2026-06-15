CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,

                       first_name VARCHAR(100),
                       last_name VARCHAR(100),

                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL,

                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);