package com.jewelry.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jewelry.store.dto.request.CreateCategoryRequest;
import com.jewelry.store.util.SlugUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductCatalogTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Hàm SlugUtils phải chuyển đổi tiếng Việt có dấu sang URL slug chuẩn SEO chính xác")
    void testSlugGeneration() {
        String title1 = "Nhẫn Kim Cương Solitaire Vàng Trắng 18K";
        String slug1 = SlugUtils.toSlug(title1);
        assertThat(slug1).isEqualTo("nhan-kim-cuong-solitaire-vang-trang-18k");

        String title2 = "Đồng Hồ & Lắc Tay Đính Ngọc Trai Tự Nhiên";
        String slug2 = SlugUtils.toSlug(title2);
        assertThat(slug2).isEqualTo("dong-ho-lac-tay-dinh-ngoc-trai-tu-nhien");
    }

    @Test
    @DisplayName("Khách hàng vãng lai có thể xem danh mục mà không cần đăng nhập")
    void publicCanAccessCategories() throws Exception {
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Người dùng chưa đăng nhập gọi API tạo sản phẩm Admin phải bị từ chối 401 Unauthorized")
    void unauthenticatedCannotCreateProduct() throws Exception {
        mockMvc.perform(post("/api/v1/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("User có ROLE_CUSTOMER cố tình tạo danh mục phải nhận 403 Forbidden")
    void customerCannotCreateCategory() throws Exception {
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("Danh mục thử nghiệm")
                .build();

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
