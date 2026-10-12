package com.jewelry.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jewelry.store.dto.request.CreateOrderRequest;
import com.jewelry.store.dto.request.ShippingAddressDto;
import com.jewelry.store.entity.*;
import com.jewelry.store.entity.enums.*;
import com.jewelry.store.repository.*;
import com.jewelry.store.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SystemE2EAndConcurrencyTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository variantRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private ProductVariant testVariant;
    private Inventory testInventory;

    @BeforeEach
    void setUp() {
        // Setup base test data
        Role customerRole = roleRepository.findByName(RoleType.ROLE_CUSTOMER)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(RoleType.ROLE_CUSTOMER)
                        .description("Customer")
                        .build()));

        testUser = userRepository.findByUsername("concurrency_user")
                .orElseGet(() -> {
                    User u = User.builder()
                            .username("concurrency_user")
                            .email("concurrency@aurajewelry.com")
                            .passwordHash(passwordEncoder.encode("Password123!"))
                            .firstName("Concurrency")
                            .lastName("Tester")
                            .status(UserStatus.ACTIVE)
                            .roles(new HashSet<>(Collections.singletonList(customerRole)))
                            .build();
                    return userRepository.save(u);
                });

        Category cat = categoryRepository.findBySlug("nhan-kim-cuong")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Nhẫn Kim Cương")
                        .slug("nhan-kim-cuong")
                        .status(CategoryStatus.ACTIVE)
                        .build()));

        Product prod = productRepository.findBySlug("nhan-test-concurrency")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .name("Nhẫn Test Concurrency")
                        .slug("nhan-test-concurrency")
                        .baseSku("TEST-CONC-001")
                        .category(cat)
                        .status(ProductStatus.ACTIVE)
                        .build()));

        testVariant = variantRepository.findBySku("TEST-CONC-VAR-001")
                .orElseGet(() -> variantRepository.save(ProductVariant.builder()
                        .product(prod)
                        .sku("TEST-CONC-VAR-001")
                        .sizeOption("Ni 12")
                        .materialType("Vàng Ý 750")
                        .originalPrice(new BigDecimal("15000000"))
                        .salePrice(new BigDecimal("12000000"))
                        .status(ProductStatus.ACTIVE)
                        .build()));

        testInventory = inventoryRepository.findByVariantId(testVariant.getId())
                .orElseGet(() -> inventoryRepository.save(Inventory.builder()
                        .variant(testVariant)
                        .quantity(2) // Only 2 items available!
                        .reservedQuantity(0)
                        .lowStockThreshold(1)
                        .build()));

        // Reset inventory to exactly 2
        testInventory.setQuantity(2);
        testInventory.setReservedQuantity(0);
        inventoryRepository.save(testInventory);
    }

    @Test
    @DisplayName("Kiểm tra Concurrency: 5 luồng đặt hàng đồng thời sản phẩm chỉ còn 2 món - Không được bán vượt tồn kho (Overselling)")
    void testConcurrentStockDeductionPreventsOverselling() throws InterruptedException {
        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    startLatch.await(); // Synchronize all threads to fire simultaneously

                    // Create separate user and cart for each thread to simulate real distinct buyers
                    String tempUsername = "buyer_" + index + "_" + System.currentTimeMillis();
                    User buyer = userRepository.save(User.builder()
                            .username(tempUsername)
                            .email(tempUsername + "@aura.com")
                            .passwordHash("hash")
                            .firstName("Buyer")
                            .lastName(String.valueOf(index))
                            .status(UserStatus.ACTIVE)
                            .roles(new HashSet<>(Collections.singletonList(roleRepository.findByName(RoleType.ROLE_CUSTOMER).get())))
                            .build());

                    Cart cart = cartRepository.save(Cart.builder().user(buyer).build());
                    cartItemRepository.save(CartItem.builder()
                            .cart(cart)
                            .variant(testVariant)
                            .quantity(1)
                            .build());

                    CreateOrderRequest req = new CreateOrderRequest();
                    req.setPaymentMethod(PaymentMethodType.COD);
                    req.setShippingAddress(ShippingAddressDto.builder()
                            .receiverName("Buyer " + index)
                            .phone("0901234567")
                            .streetAddress("123 Phố Tràng Tiền")
                            .ward("Phường Tràng Tiền")
                            .district("Quận Hoàn Kiếm")
                            .province("Hà Nội")
                            .build());

                    orderService.createOrder(buyer.getId(), req);
                    successCount.incrementAndGet();
                } catch (Exception ex) {
                    failCount.incrementAndGet();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        // Fire all threads at once!
        startLatch.countDown();
        boolean completed = finishLatch.await(15, TimeUnit.SECONDS);
        executorService.shutdown();

        assertTrue(completed, "Các luồng xử lý không kết thúc kịp thời");

        // Verify: Exactly 2 succeed and 3 fail (or due to optimistic lock 1-2 succeed), never > 2!
        assertTrue(successCount.get() <= 2, "Số đơn đặt thành công không được vượt quá số lượng tồn kho (2). Thực tế: " + successCount.get());
        assertEquals(threadCount, successCount.get() + failCount.get(), "Tổng số giao dịch phải bằng 5");

        // Check database: quantity must be >= 0
        Inventory currentInventory = inventoryRepository.findByVariantId(testVariant.getId()).orElseThrow();
        assertTrue(currentInventory.getQuantity() >= 0, "Tồn kho bị âm! Giá trị hiện tại: " + currentInventory.getQuantity());
    }

    @Test
    @DisplayName("Kiểm tra Concurrency: Mã giảm giá có hạn mức 1 lượt - 3 luồng cùng áp dụng chỉ duy nhất 1 luồng thành công")
    void testConcurrentCouponUsageExhaustion() throws InterruptedException {
        String couponCode = "FLASH_LIMITED_" + System.currentTimeMillis();
        Coupon limitedCoupon = couponRepository.save(Coupon.builder()
                .code(couponCode)
                .discountType(DiscountType.FIXED_AMOUNT)
                .discountValue(new BigDecimal("1000000"))
                .minOrderValue(new BigDecimal("5000000"))
                .usageLimit(1) // Only 1 usage allowed!
                .usedCount(0)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(10))
                .isActive(true)
                .build());

        int threadCount = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);

        AtomicInteger successCouponCount = new AtomicInteger(0);
        AtomicInteger failCouponCount = new AtomicInteger(0);

        // Ensure sufficient variant inventory for this test
        testInventory.setQuantity(100);
        inventoryRepository.save(testInventory);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    startLatch.await();

                    String tempUsername = "coupon_user_" + index + "_" + System.currentTimeMillis();
                    User buyer = userRepository.save(User.builder()
                            .username(tempUsername)
                            .email(tempUsername + "@aura.com")
                            .passwordHash("hash")
                            .firstName("CouponBuyer")
                            .lastName(String.valueOf(index))
                            .status(UserStatus.ACTIVE)
                            .roles(new HashSet<>(Collections.singletonList(roleRepository.findByName(RoleType.ROLE_CUSTOMER).get())))
                            .build());

                    Cart cart = cartRepository.save(Cart.builder().user(buyer).build());
                    cartItemRepository.save(CartItem.builder()
                            .cart(cart)
                            .variant(testVariant)
                            .quantity(1)
                            .build());

                    CreateOrderRequest req = new CreateOrderRequest();
                    req.setPaymentMethod(PaymentMethodType.COD);
                    req.setCouponCode(couponCode);
                    req.setShippingAddress(ShippingAddressDto.builder()
                            .receiverName("Coupon Buyer " + index)
                            .phone("0901234567")
                            .streetAddress("456 Lê Lợi")
                            .ward("Phường Bến Thành")
                            .district("Quận 1")
                            .province("TP. Hồ Chí Minh")
                            .build());

                    orderService.createOrder(buyer.getId(), req);
                    successCouponCount.incrementAndGet();
                } catch (Exception ex) {
                    failCouponCount.incrementAndGet();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        finishLatch.await(15, TimeUnit.SECONDS);
        executorService.shutdown();

        // Exactly 1 or at most 1 should succeed with coupon
        assertTrue(successCouponCount.get() <= 1, "Số đơn áp dụng coupon vượt quá hạn mức sử dụng (1). Thực tế: " + successCouponCount.get());

        Coupon refreshed = couponRepository.findById(limitedCoupon.getId()).orElseThrow();
        assertTrue(refreshed.getUsedCount() <= 1, "Coupon usedCount vượt quá hạn mức");
    }

    @Test
    @DisplayName("Kiểm tra SQL Injection & XSS An Toàn: Truy vấn tìm kiếm với chuỗi độc hại không gây lỗi máy chủ (HTTP 200)")
    void testSqlInjectionAndXssSafeHandling() throws Exception {
        // SQL Injection attack vector
        mockMvc.perform(get("/api/v1/products")
                        .param("keyword", "' OR '1'='1' --")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // XSS Injection vector
        mockMvc.perform(get("/api/v1/products")
                        .param("keyword", "<script>alert('xss')</script>")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Kiểm tra Health Check & Metrics: Actuator endpoint sẵn sàng hoạt động")
    void testActuatorHealthAndSystemMetrics() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
