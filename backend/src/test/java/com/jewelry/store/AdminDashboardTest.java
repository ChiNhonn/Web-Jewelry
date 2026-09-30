package com.jewelry.store;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminDashboardTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Khách chưa đăng nhập cố tình truy cập Dashboard quản trị phải nhận 401 Unauthorized")
    void unauthenticatedCannotAccessDashboard() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard/overview"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("Người dùng với vai trò CUSTOMER bị từ chối 403 Forbidden khi truy cập Dashboard")
    void customerCannotAccessDashboard() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard/overview"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Quản trị viên có quyền truy cập tổng quan Dashboard và nhận đủ các trường thống kê tài chính & kho hàng")
    void adminCanAccessOverviewMetrics() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalRevenue").exists())
                .andExpect(jsonPath("$.data.totalOrders").exists())
                .andExpect(jsonPath("$.data.totalCustomers").exists())
                .andExpect(jsonPath("$.data.outOfStockCount").exists())
                .andExpect(jsonPath("$.data.lowStockCount").exists());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    @DisplayName("Nhân viên (STAFF) có thể xem biểu đồ chuỗi thời gian doanh thu 7 ngày (trả về đúng 7 điểm dữ liệu)")
    void staffCanAccessRevenueAnalyticsChart() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard/revenue-analytics")
                        .param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(7)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Quản trị viên có thể truy cập danh sách cảnh báo tồn kho sắp hết")
    void adminCanAccessLowStockAlerts() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard/low-stock")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }
}
