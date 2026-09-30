package com.jewelry.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jewelry.store.dto.request.CreateCouponRequest;
import com.jewelry.store.dto.request.StockAdjustmentRequest;
import com.jewelry.store.dto.request.UpdateThresholdRequest;
import com.jewelry.store.dto.request.UpdateUserStatusRequest;
import com.jewelry.store.entity.*;
import com.jewelry.store.entity.enums.*;
import com.jewelry.store.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminCmsManagementTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository variantRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    private Long testVariantId;
    private Long testUserId;

    @BeforeEach
    void setUp() {
        Category category = categoryRepository.findAll().stream().findFirst().orElseGet(() -> {
            Category c = Category.builder()
                    .name("Dây Chuyền Bạch Kim CMS")
                    .slug("day-chuyen-cms-" + System.currentTimeMillis())
                    .status(CategoryStatus.ACTIVE)
                    .build();
            return categoryRepository.save(c);
        });

        Product product = productRepository.findAll().stream().findFirst().orElseGet(() -> {
            Product p = Product.builder()
                    .category(category)
                    .name("Dây Chuyền Luxury CMS Test")
                    .slug("day-chuyen-cms-test-" + System.currentTimeMillis())
                    .baseSku("SKU-CMS-" + System.currentTimeMillis())
                    .status(ProductStatus.ACTIVE)
                    .build();
            return productRepository.save(p);
        });

        ProductVariant variant = variantRepository.findAll().stream().findFirst().orElseGet(() -> {
            ProductVariant v = ProductVariant.builder()
                    .product(product)
                    .sku("SKU-VAR-CMS-" + System.currentTimeMillis())
                    .sizeOption("45cm")
                    .materialType("Bạch Kim 950")
                    .originalPrice(BigDecimal.valueOf(15000000))
                    .salePrice(BigDecimal.valueOf(14000000))
                    .status(ProductStatus.ACTIVE)
                    .build();

            Inventory inv = Inventory.builder()
                    .quantity(20)
                    .reservedQuantity(0)
                    .lowStockThreshold(5)
                    .version(0L)
                    .build();

            v.setInventory(inv);
            return variantRepository.save(v);
        });

        testVariantId = variant.getId();

        // Ensure inventory exists
        if (inventoryRepository.findByVariantId(testVariantId).isEmpty()) {
            Inventory inv = Inventory.builder()
                    .variant(variant)
                    .quantity(20)
                    .reservedQuantity(0)
                    .lowStockThreshold(5)
                    .version(0L)
                    .build();
            inventoryRepository.save(inv);
        }

        User user = userRepository.findAll().stream().findFirst().orElseGet(() -> {
            User u = User.builder()
                    .username("testcustomer_cms_" + System.currentTimeMillis())
                    .email("cms_user_" + System.currentTimeMillis() + "@aurajewelry.com")
                    .passwordHash("$2a$12$eXampleHashedPasswordBcrypt1234567890123456789012")
                    .firstName("Văn")
                    .lastName("Nguyễn")
                    .phone("0909123456")
                    .status(UserStatus.ACTIVE)
                    .build();
            return userRepository.save(u);
        });

        testUserId = user.getId();
    }

    @Test
    @DisplayName("Khách chưa đăng nhập cố tình truy cập kho hoặc khách hàng phải nhận 401 Unauthorized")
    void unauthenticatedCannotAccessCms() throws Exception {
        mockMvc.perform(get("/api/v1/admin/inventory"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/admin/coupons"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("Khách hàng thông thường (CUSTOMER) bị từ chối 403 Forbidden khi truy cập API quản trị")
    void customerCannotAccessAdminCms() throws Exception {
        mockMvc.perform(get("/api/v1/admin/inventory"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/admin/coupons"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Quản trị viên có thể xem danh sách tồn kho và nhập hàng bổ sung (Restock) thành công")
    void adminCanListAndAdjustInventory() throws Exception {
        // 1. Kiểm tra lấy danh sách kho
        mockMvc.perform(get("/api/v1/admin/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());

        // 2. Nhập thêm 15 sản phẩm vào kho
        StockAdjustmentRequest adjustReq = StockAdjustmentRequest.builder()
                .variantId(testVariantId)
                .adjustmentType(StockAdjustmentRequest.AdjustmentType.ADD)
                .quantity(15)
                .reason("Nhập xưởng hoàn thiện đợt 1")
                .build();

        mockMvc.perform(post("/api/v1/admin/inventory/adjust")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adjustReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.variantId").value(testVariantId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Quản trị viên có thể cập nhật ngưỡng cảnh báo tồn kho an toàn cho biến thể")
    void adminCanUpdateLowStockThreshold() throws Exception {
        UpdateThresholdRequest req = UpdateThresholdRequest.builder()
                .lowStockThreshold(8)
                .build();

        mockMvc.perform(put("/api/v1/admin/inventory/" + testVariantId + "/threshold")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.lowStockThreshold").value(8));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Quản trị viên tạo mã khuyến mãi (Coupon) thành công và xem danh sách mã")
    void adminCanCreateAndManagePromotionalCoupons() throws Exception {
        String uniqueCode = "AURA" + System.currentTimeMillis();

        CreateCouponRequest couponReq = CreateCouponRequest.builder()
                .code(uniqueCode)
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(BigDecimal.valueOf(15))
                .minOrderValue(BigDecimal.valueOf(1000000))
                .maxDiscount(BigDecimal.valueOf(500000))
                .usageLimit(100)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(30))
                .isActive(true)
                .build();

        mockMvc.perform(post("/api/v1/admin/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(couponReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.code").value(uniqueCode))
                .andExpect(jsonPath("$.data.discountValue").value(15));

        // Lấy danh sách coupons
        mockMvc.perform(get("/api/v1/admin/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Quản trị viên có thể xem hồ sơ người dùng và khóa (LOCKED) tài khoản vi phạm")
    void adminCanListAndModerateUserAccount() throws Exception {
        // Xem danh sách người dùng
        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());

        // Khóa tài khoản
        UpdateUserStatusRequest statusReq = UpdateUserStatusRequest.builder()
                .status(UserStatus.LOCKED)
                .reason("Nghi ngờ hoạt động gian lận thẻ tín dụng")
                .build();

        mockMvc.perform(put("/api/v1/admin/users/" + testUserId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("LOCKED"));
    }
}
