package com.jewelry.store;

import com.jewelry.store.util.VNPayUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentGatewayTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Khách chưa đăng nhập cố tình khởi tạo thanh toán phải nhận 401 Unauthorized")
    void unauthenticatedCannotInitiatePayment() throws Exception {
        mockMvc.perform(post("/api/v1/payments/JLW-20261001-TEST1/initiate"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Khách chưa đăng nhập cố tình tra cứu chi tiết thanh toán phải nhận 401 Unauthorized")
    void unauthenticatedCannotGetPaymentDetails() throws Exception {
        mockMvc.perform(get("/api/v1/payments/JLW-20261001-TEST1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("VNPay Return URL mở công khai và phát hiện chữ ký giả mạo (Invalid Checksum)")
    void vnpayReturnWithTamperedChecksumReturnsFailure() throws Exception {
        mockMvc.perform(get("/api/v1/payments/vnpay-return")
                        .param("vnp_TxnRef", "JLW-20261001-TEST1")
                        .param("vnp_Amount", "1000000000")
                        .param("vnp_ResponseCode", "00")
                        .param("vnp_SecureHash", "TAMPERED_HASH_ABCDEF123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(false))
                .andExpect(jsonPath("$.data.message").value("Chữ ký bảo mật không hợp lệ (Invalid Checksum)"));
    }

    @Test
    @DisplayName("VNPay IPN Webhook mở công khai và trả về mã 97 khi Checksum không hợp lệ")
    void vnpayIpnWithInvalidChecksumReturnsCode97() throws Exception {
        mockMvc.perform(get("/api/v1/payments/vnpay-ipn")
                        .param("vnp_TxnRef", "JLW-20261001-TEST1")
                        .param("vnp_Amount", "1000000000")
                        .param("vnp_SecureHash", "TAMPERED_HASH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.RspCode").value("97"))
                .andExpect(jsonPath("$.Message").value("Invalid Checksum"));
    }

    @Test
    @DisplayName("Thuật toán VNPay HMAC-SHA512 mã hóa chuỗi ra mã Hex chuẩn 128 ký tự")
    void hmacSha512GeneratesValid128HexCharacters() {
        String key = "RAOCTP02OZ8FKKAEBRISAAWUM8SO1KEJ";
        String data = "vnp_Amount=1000000000&vnp_Command=pay&vnp_TxnRef=JLW-20261001-TEST1";

        String hash = VNPayUtils.hmacSHA512(key, data);

        assertNotNull(hash);
        assertEquals(128, hash.length(), "HMAC-SHA512 hex string phải có độ dài chính xác 128 ký tự");
        assertTrue(hash.matches("^[0-9a-fA-F]+$"), "Chuỗi băm phải là định dạng Hexadecimal");
    }
}
