package com.javed.smartjobtracker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FlywayMigrationTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void usersTableExists() throws Exception {

        try (Connection conn = dataSource.getConnection()) {

            ResultSet rs = conn.getMetaData()
                    .getTables(null, null, "users", null);

            assertTrue(rs.next());
        }
    }

    @Test
    void jobApplicationsTableExists() throws Exception {

        try (Connection conn = dataSource.getConnection()) {

            ResultSet rs = conn.getMetaData()
                    .getTables(null, null, "job_applications", null);

            assertTrue(rs.next());
        }
    }
}