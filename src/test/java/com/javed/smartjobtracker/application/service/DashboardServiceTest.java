package com.javed.smartjobtracker.application.service;

import com.javed.smartjobtracker.application.dto.DashboardResponse;
import com.javed.smartjobtracker.application.dto.DashboardStats;
import com.javed.smartjobtracker.application.repository.DashboardRepository;
import com.javed.smartjobtracker.user.entity.Role;
import com.javed.smartjobtracker.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private DashboardRepository dashboardRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void userDashboard_success() {
        User user = User.builder().id(1L).email("user@test.com").role(Role.USER).build();
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(user);

        DashboardStats stats = new DashboardStats(10L, 4L, 2L, 1L, 1L, 1L, 1L, 0L);
        when(dashboardRepository.getUserStats(1L)).thenReturn(stats);

        DashboardResponse res = dashboardService.getDashboard(auth);

        assertNotNull(res);
        assertEquals(10, res.getTotalApplications());
        assertEquals(1, res.getInterviewCount());
        assertEquals(0, res.getRejectedCount());
        assertEquals(1, res.getOfferCount());
        assertEquals(1, res.getAcceptedCount());
        
        assertEquals(60.0, res.getResponseRate(), 0.001);
        assertEquals(20.0, res.getOfferRate(), 0.001);

        verify(dashboardRepository, times(1)).getUserStats(1L);
        verify(dashboardRepository, never()).getSystemStats();
    }

    @Test
    void adminDashboard_success() {
        User admin = User.builder().id(2L).email("admin@test.com").role(Role.ADMIN).build();
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(admin);

        DashboardStats stats = new DashboardStats(20L, 5L, 5L, 2L, 4L, 2L, 1L, 1L);
        when(dashboardRepository.getSystemStats()).thenReturn(stats);

        DashboardResponse res = dashboardService.getDashboard(auth);

        assertNotNull(res);
        assertEquals(20, res.getTotalApplications());
        
        assertEquals(75.0, res.getResponseRate(), 0.001);
        assertEquals(15.0, res.getOfferRate(), 0.001);

        verify(dashboardRepository, times(1)).getSystemStats();
        verify(dashboardRepository, never()).getUserStats(anyLong());
    }

    @Test
    void divisionByZero_handling() {
        User user = User.builder().id(1L).email("user@test.com").role(Role.USER).build();
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(user);

        DashboardStats stats = new DashboardStats(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
        when(dashboardRepository.getUserStats(1L)).thenReturn(stats);

        DashboardResponse res = dashboardService.getDashboard(auth);

        assertNotNull(res);
        assertEquals(0, res.getTotalApplications());
        assertEquals(0.0, res.getResponseRate(), 0.001);
        assertEquals(0.0, res.getOfferRate(), 0.001);
    }
}
