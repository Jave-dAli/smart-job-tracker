package com.javed.smartjobtracker.application.controller;

import com.javed.smartjobtracker.application.entity.ApplicationStatus;
import com.javed.smartjobtracker.application.entity.JobApplication;
import com.javed.smartjobtracker.application.repository.ApplicationRepository;
import com.javed.smartjobtracker.security.JwtService;
import com.javed.smartjobtracker.user.entity.Role;
import com.javed.smartjobtracker.user.entity.User;
import com.javed.smartjobtracker.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ApplicationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private User regularUser;
    private User adminUser;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        applicationRepository.deleteAll();
        userRepository.deleteAll();

        // 1. Create Users
        regularUser = User.builder()
                .email("user@tracker.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .build();
        regularUser = userRepository.save(regularUser);

        adminUser = User.builder()
                .email("admin@tracker.com")
                .password("password123")
                .firstName("Admin")
                .lastName("User")
                .role(Role.ADMIN)
                .build();
        adminUser = userRepository.save(adminUser);

        // 2. Generate Tokens
        userToken = "Bearer " + jwtService.generateToken(regularUser);
        adminToken = "Bearer " + jwtService.generateToken(adminUser);

        // 3. Seed Job Applications
        // User's applications
        saveApp(regularUser.getId(), "Google", "SWE", ApplicationStatus.INTERVIEW, LocalDate.of(2026, 1, 15));
        saveApp(regularUser.getId(), "Google", "PM", ApplicationStatus.APPLIED, LocalDate.of(2026, 2, 10));
        saveApp(regularUser.getId(), "Meta", "SWE", ApplicationStatus.OFFER, LocalDate.of(2026, 3, 5));
        saveApp(regularUser.getId(), "Apple", "Designer", ApplicationStatus.REJECTED, LocalDate.of(2026, 4, 1));
        saveApp(regularUser.getId(), "Netflix", "Manager", ApplicationStatus.ACCEPTED, LocalDate.of(2026, 5, 20));
        saveApp(regularUser.getId(), "Amazon", "Analyst", ApplicationStatus.SHORTLISTED, LocalDate.of(2026, 6, 1));

        // Admin/other user application
        saveApp(adminUser.getId(), "Microsoft", "SWE", ApplicationStatus.INTERVIEW, LocalDate.of(2026, 1, 20));
    }

    private void saveApp(Long userId, String company, String title, ApplicationStatus status, LocalDate date) {
        JobApplication app = JobApplication.builder()
                .userId(userId)
                .companyName(company)
                .jobTitle(title)
                .status(status)
                .applicationDate(date)
                .build();
        applicationRepository.save(app);
    }

    @Test
    void testPagination() throws Exception {
        // Page 0, Size 3: should return 3 records (user has 6 in total)
        mockMvc.perform(get("/api/v1/applications")
                        .header("Authorization", userToken)
                        .param("page", "0")
                        .param("size", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.totalElements", is(6)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.number", is(0)));

        // Page 1, Size 3: should return next 3 records
        mockMvc.perform(get("/api/v1/applications")
                        .header("Authorization", userToken)
                        .param("page", "1")
                        .param("size", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.number", is(1)));
    }

    @Test
    void testFilteringByStatus() throws Exception {
        mockMvc.perform(get("/api/v1/applications")
                        .header("Authorization", userToken)
                        .param("status", "INTERVIEW")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].companyName", is("Google")))
                .andExpect(jsonPath("$.content[0].jobTitle", is("SWE")));
    }

    @Test
    void testFilteringByCompany() throws Exception {
        // Partial, case-insensitive check
        mockMvc.perform(get("/api/v1/applications")
                        .header("Authorization", userToken)
                        .param("company", "ooG")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2))) // Google SWE & Google PM
                .andExpect(jsonPath("$.content[*].companyName", containsInAnyOrder("Google", "Google")));
    }

    @Test
    void testSorting() throws Exception {
        // Sort by dateApplied DESC (newest application first)
        mockMvc.perform(get("/api/v1/applications")
                        .header("Authorization", userToken)
                        .param("sort", "dateApplied")
                        .param("direction", "DESC")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].companyName", is("Amazon"))) // 2026-06-01
                .andExpect(jsonPath("$.content[5].companyName", is("Google"))); // 2026-01-15
    }

    @Test
    void testCombinedQuery() throws Exception {
        // status=INTERVIEW & sort=dateApplied & size=3
        mockMvc.perform(get("/api/v1/applications")
                        .header("Authorization", userToken)
                        .param("status", "INTERVIEW")
                        .param("sort", "dateApplied")
                        .param("direction", "DESC")
                        .param("page", "0")
                        .param("size", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].companyName", is("Google")));
    }

    @Test
    void testSortWhitelistValidation() throws Exception {
        mockMvc.perform(get("/api/v1/applications")
                        .header("Authorization", userToken)
                        .param("sort", "unsupportedField")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid sort field")));
    }

    @Test
    void testUserDashboard() throws Exception {
        // User has 6 apps: APPLIED(1), SHORTLISTED(1), INTERVIEW(1), OFFER(1), ACCEPTED(1), REJECTED(1)
        // Responses = total (6) - applied (1) = 5. responseRate = (5/6)*100 = 83.33%
        // Offers = offer (1) + accepted (1) = 2. offerRate = (2/6)*100 = 33.33%
        mockMvc.perform(get("/api/v1/dashboard")
                        .header("Authorization", userToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalApplications", is(6)))
                .andExpect(jsonPath("$.interviewCount", is(1)))
                .andExpect(jsonPath("$.rejectedCount", is(1)))
                .andExpect(jsonPath("$.offerCount", is(1)))
                .andExpect(jsonPath("$.acceptedCount", is(1)))
                .andExpect(jsonPath("$.responseRate", closeTo(83.33, 0.05)))
                .andExpect(jsonPath("$.offerRate", closeTo(33.33, 0.05)));
    }

    @Test
    void testAdminDashboard() throws Exception {
        // Admin gets system-wide stats:
        // User has 6, Admin has 1 (INTERVIEW) -> Total = 7
        // Applied = 1, Shortlisted = 1, Interview = 2, Offer = 1, Accepted = 1, Rejected = 1
        // Responses = 7 - 1 = 6. responseRate = (6/7)*100 = 85.71%
        // Offers = 1 + 1 = 2. offerRate = (2/7)*100 = 28.57%
        mockMvc.perform(get("/api/v1/dashboard")
                        .header("Authorization", adminToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalApplications", is(7)))
                .andExpect(jsonPath("$.interviewCount", is(2)))
                .andExpect(jsonPath("$.rejectedCount", is(1)))
                .andExpect(jsonPath("$.offerCount", is(1)))
                .andExpect(jsonPath("$.acceptedCount", is(1)))
                .andExpect(jsonPath("$.responseRate", closeTo(85.71, 0.05)))
                .andExpect(jsonPath("$.offerRate", closeTo(28.57, 0.05)));
    }
}
