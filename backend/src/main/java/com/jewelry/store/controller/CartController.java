package com.jewelry.store.controller;

import com.jewelry.store.dto.common.ApiResponse;
import com.jewelry.store.dto.request.AddToCartRequest;
import com.jewelry.store.dto.request.UpdateCartItemRequest;
import com.jewelry.store.dto.response.CartResponse;
import com.jewelry.store.security.UserPrincipal;
import com.jewelry.store.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Shopping Cart", description = "Endpoints for managing customer cart items, quantities, and subtotal calculation")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get user shopping cart", description = "Lấy giỏ hàng của người dùng hiện tại kèm thông tin chi tiết và tính toán subtotal")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@AuthenticationPrincipal UserPrincipal currentUser) {
        CartResponse response = cartService.getCart(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/items")
    @Operation(summary = "Add jewelry variant to cart", description = "Thêm biến thể trang sức vào giỏ (tự động kiểm tra tồn kho và tính giá từ CSDL)")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody AddToCartRequest request) {
        CartResponse response = cartService.addToCart(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Thêm vào giỏ hàng thành công!", response));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update cart item quantity", description = "Cập nhật số lượng của một mặt hàng trong giỏ")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        CartResponse response = cartService.updateCartItem(currentUser.getId(), itemId, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật số lượng thành công!", response));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart", description = "Xóa một mặt hàng khỏi giỏ hàng")
    public ResponseEntity<ApiResponse<CartResponse>> removeCartItem(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long itemId) {
        CartResponse response = cartService.removeCartItem(currentUser.getId(), itemId);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa sản phẩm khỏi giỏ hàng!", response));
    }

    @DeleteMapping
    @Operation(summary = "Clear all items in cart", description = "Xóa toàn bộ mặt hàng trong giỏ hàng")
    public ResponseEntity<ApiResponse<Void>> clearCart(@AuthenticationPrincipal UserPrincipal currentUser) {
        cartService.clearCart(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.empty("Đã dọn sạch giỏ hàng!"));
    }
}
