package com.jewelry.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jewelry.store.dto.request.CreateOrderRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderCheckoutTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Khách chưa đăng nhập cố tình gọi Checkout đơn hàng phải nhận 401 Unauthorized")
    void unauthenticatedCannotCheckout() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Khách chưa đăng nhập cố tình truy cập sổ địa chỉ phải nhận 401 Unauthorized")
    void unauthenticatedCannotAccessAddresses() throws Exception {
        mockMvc.perform(get("/api/v1/addresses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("User với vai trò CUSTOMER không được phép truy cập trang quản lý đơn hàng của Admin (403 Forbidden)")
    void customerCannotAccessAdminOrders() throws Exception {
        mockMvc.perform(get("/api/v1/admin/orders"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "customer1", roles = "CUSTOMER")
    @DisplayName("Checkout gửi request rỗng (thiếu phương thức thanh toán) phải trả về 422 VALIDATION_FAILED")
    void checkoutWithEmptyDataShouldReturnValidationErrors() throws Exception {
        CreateOrderRequest emptyRequest = new CreateOrderRequest();

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));
    }

    @Test
    @DisplayName("Kiểm tra mã voucher giảm giá không tồn tại trả về phản hồi isValid = false")
    void validateNonExistentCouponReturnsInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/coupons/validate")
                        .param("code", "INVALID_CODE_999")
                        .param("amount", "50000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid").value(false));
    }
}
