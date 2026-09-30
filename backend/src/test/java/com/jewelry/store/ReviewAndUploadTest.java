package com.jewelry.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jewelry.store.dto.request.CreateReviewRequest;
import com.jewelry.store.entity.Category;
import com.jewelry.store.entity.Product;
import com.jewelry.store.entity.enums.CategoryStatus;
import com.jewelry.store.entity.enums.ProductStatus;
import com.jewelry.store.repository.CategoryRepository;
import com.jewelry.store.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
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
class ReviewAndUploadTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Long testProductId;

    @BeforeEach
    void setUp() {
        Category category = categoryRepository.findAll().stream().findFirst().orElseGet(() -> {
            Category c = Category.builder()
                    .name("Nhẫn Kim Cương")
                    .slug("nhan-kim-cuong-test-" + System.currentTimeMillis())
                    .status(CategoryStatus.ACTIVE)
                    .build();
            return categoryRepository.save(c);
        });

        Product product = productRepository.findAll().stream().findFirst().orElseGet(() -> {
            Product p = Product.builder()
                    .category(category)
                    .name("Nhẫn Kim Cương Solitaire Test")
                    .slug("nhan-kim-cuong-test-" + System.currentTimeMillis())
                    .baseSku("SKU-TEST-" + System.currentTimeMillis())
                    .status(ProductStatus.ACTIVE)
                    .build();
            return productRepository.save(p);
        });

        testProductId = product.getId();
    }

    @Test
    @DisplayName("Khách chưa đăng nhập cố tình gửi đánh giá sản phẩm phải nhận 401 Unauthorized")
    void unauthenticatedCannotCreateReview() throws Exception {
        mockMvc.perform(post("/api/v1/products/" + testProductId + "/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Khách chưa đăng nhập cố tình tải ảnh lên phải nhận 401 Unauthorized")
    void unauthenticatedCannotUploadImage() throws Exception {
        mockMvc.perform(post("/api/v1/uploads/image"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("Khách hàng với ROLE_CUSTOMER không được phép truy cập trang duyệt đánh giá của Admin (403 Forbidden)")
    void customerCannotAccessAdminReviews() throws Exception {
        mockMvc.perform(get("/api/v1/admin/reviews"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Khách vãng lai có thể xem danh sách đánh giá đã duyệt của sản phẩm mà không cần đăng nhập")
    void publicCanGetApprovedProductReviews() throws Exception {
        mockMvc.perform(get("/api/v1/products/" + testProductId + "/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Khách vãng lai có thể xem thống kê số sao và trung bình đánh giá của sản phẩm")
    void publicCanGetProductRatingSummary() throws Exception {
        mockMvc.perform(get("/api/v1/products/" + testProductId + "/reviews/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.averageRating").exists())
                .andExpect(jsonPath("$.data.ratingBreakdown").isMap());
    }

    @Test
    @WithMockUser(username = "customer1", roles = "CUSTOMER")
    @DisplayName("Gửi đánh giá với số sao không hợp lệ (< 1 hoặc > 5) phải trả về 422 VALIDATION_FAILED")
    void createReviewWithInvalidRatingReturnsValidationError() throws Exception {
        CreateReviewRequest invalidRequest = CreateReviewRequest.builder()
                .orderId(1L)
                .rating(6) // Vượt quá 5 sao
                .comment("Sản phẩm tuyệt vời")
                .build();

        mockMvc.perform(post("/api/v1/products/" + testProductId + "/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));
    }
}
