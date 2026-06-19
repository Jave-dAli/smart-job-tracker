package com.javed.smartjobtracker.config;

import com.javed.smartjobtracker.application.entity.JobApplication;
import com.javed.smartjobtracker.application.entity.ApplicationStatus;
import com.javed.smartjobtracker.application.repository.ApplicationRepository;
import com.javed.smartjobtracker.user.entity.Role;
import com.javed.smartjobtracker.user.entity.User;
import com.javed.smartjobtracker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.count() > 0) {
            return; // avoid duplicate inserts
        }

        for (int i = 1; i <= 5; i++) {

            User user = User.builder()
                    .email("user" + i + "@test.com")
                    .password(passwordEncoder.encode("password"))
                    .role(Role.USER)
                    .build();

            userRepository.save(user);

            for (int j = 1; j <= 20; j++) {

                JobApplication application = JobApplication.builder()

                        .userId(user.getId())

                        .companyName(
                                "Company-" + j)

                        .jobTitle(
                                "Java Developer")

                        .status(
                                ApplicationStatus.values()[j % ApplicationStatus.values().length])

                        .applicationDate(
                                LocalDate.now()
                                        .minusDays(j))

                        .build();

                applicationRepository.save(application);
            }
        }

        System.out.println(
                "Inserted 5 users with 20 applications each");
    }
}