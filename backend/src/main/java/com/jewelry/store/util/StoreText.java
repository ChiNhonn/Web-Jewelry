package com.jewelry.store.util;

import com.jewelry.store.entity.enums.OrderStatus;
import com.jewelry.store.entity.enums.PaymentMethodType;
import com.jewelry.store.entity.enums.PaymentStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;

public final class StoreText {

    private static final Map<Long, String> CATEGORY_SLUGS = Map.of(
            1L, "nhan-kim-cuong",
            2L, "day-chuyen-mat-day",
            3L, "vong-tay-lac",
            4L, "bong-tai",
            5L, "trang-suc-vang-18k",
            6L, "trang-suc-bac"
    );

    private static final Map<String, String> IMAGES = Map.ofEntries(
            Map.entry("jw_ring_solitaire", "https://images.unsplash.com/photo-1605100804763-247f67b3557e?auto=format&fit=crop&w=1000&q=80"),
            Map.entry("jw_ring_ruby", "https://images.unsplash.com/photo-1603561591411-07134e71a2a9?auto=format&fit=crop&w=1000&q=80"),
            Map.entry("jw_necklace_italy", "https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&w=1000&q=80"),
            Map.entry("jw_pendant_sapphire", "https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?auto=format&fit=crop&w=1000&q=80"),
            Map.entry("jw_bracelet_silver", "https://images.unsplash.com/photo-1611591437281-460bfbe1220a?auto=format&fit=crop&w=1000&q=80"),
            Map.entry("jw_bangle_gold", "https://images.unsplash.com/photo-1596944924616-7b38e7cfac36?auto=format&fit=crop&w=1000&q=80"),
            Map.entry("jw_earrings_pearl", "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=1000&q=80"),
            Map.entry("jw_earrings_stud", "https://images.unsplash.com/photo-1588444650733-d0767b753fc8?auto=format&fit=crop&w=1000&q=80"),
            Map.entry("jw_kieng_gold", "https://images.unsplash.com/photo-1617038260897-41a1f14a8ca0?auto=format&fit=crop&w=1000&q=80"),
            Map.entry("jw_chain_men", "https://images.unsplash.com/photo-1599643477877-530eb83abc8e?auto=format&fit=crop&w=1000&q=80"),
            Map.entry("jw_rings_couple", "https://images.unsplash.com/photo-1543294001-f7cd5d7fb516?auto=format&fit=crop&w=1000&q=80"),
            Map.entry("jw_anklet_silver", "https://images.unsplash.com/photo-1573408301185-9146fe634ad0?auto=format&fit=crop&w=1000&q=80")
    );

    private StoreText() {
    }

    public static String categorySlug(Long id, String name) {
        if (id != null && CATEGORY_SLUGS.containsKey(id)) {
            return CATEGORY_SLUGS.get(id);
        }
        return slugify(name);
    }

    public static Long categoryIdFromSlug(String slug) {
        if (slug == null || slug.isBlank()) {
            return null;
        }
        for (Map.Entry<Long, String> entry : CATEGORY_SLUGS.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(slug)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static String productSlug(Long id) {
        return "sp-" + id;
    }

    public static Long productIdFromSlug(String slug) {
        if (slug == null) {
            return null;
        }
        String value = slug.startsWith("sp-") ? slug.substring(3) : slug;
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static String imageUrl(String imageName) {
        if (imageName == null || imageName.isBlank()) {
            return null;
        }
        if (imageName.startsWith("http://") || imageName.startsWith("https://") || imageName.startsWith("/")) {
            return imageName;
        }
        return IMAGES.get(imageName);
    }

    public static OrderStatus orderStatus(String text) {
        if (text == null) {
            return OrderStatus.PENDING;
        }
        return switch (text.trim().toLowerCase(Locale.ROOT)) {
            case "đã xác nhận", "da xac nhan" -> OrderStatus.CONFIRMED;
            case "đang giao", "dang giao" -> OrderStatus.SHIPPING;
            case "đã giao hàng", "đã giao", "da giao", "đã hoàn tất" -> OrderStatus.DELIVERED;
            case "đã hủy", "da huy" -> OrderStatus.CANCELLED;
            default -> OrderStatus.PENDING;
        };
    }

    public static String orderStatusText(OrderStatus status) {
        if (status == null) {
            return "Chờ xác nhận";
        }
        return switch (status) {
            case CONFIRMED, PROCESSING -> "Đã xác nhận";
            case SHIPPING -> "Đang giao";
            case DELIVERED, COMPLETED -> "Đã giao hàng";
            case CANCELLED, RETURNED, REFUNDED -> "Đã hủy";
            default -> "Chờ xác nhận";
        };
    }

    public static PaymentStatus paymentStatus(String text) {
        if (text == null) {
            return PaymentStatus.PENDING;
        }
        String value = text.trim().toLowerCase(Locale.ROOT);
        if (value.contains("đã thanh toán") || value.contains("da thanh toan")) {
            return PaymentStatus.COMPLETED;
        }
        if (value.contains("thất bại") || value.contains("that bai")) {
            return PaymentStatus.FAILED;
        }
        return PaymentStatus.PENDING;
    }

    public static String paymentStatusText(PaymentStatus status) {
        if (status == PaymentStatus.COMPLETED) {
            return "Đã thanh toán";
        }
        if (status == PaymentStatus.FAILED) {
            return "Thất bại";
        }
        return "Chưa thanh toán";
    }

    public static PaymentMethodType paymentMethod(String text) {
        if (text != null && text.toLowerCase(Locale.ROOT).contains("vnpay")) {
            return PaymentMethodType.VNPAY;
        }
        return PaymentMethodType.COD;
    }

    public static String paymentMethodText(PaymentMethodType method) {
        return method == PaymentMethodType.VNPAY ? "VNPay" : "COD";
    }

    public static Long orderId(String orderCode) {
        if (orderCode == null || orderCode.isBlank()) {
            return null;
        }
        String digits = orderCode.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(digits);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static BigDecimal money(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(0, RoundingMode.HALF_UP);
    }

    public static String slugify(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return normalized;
    }
}
