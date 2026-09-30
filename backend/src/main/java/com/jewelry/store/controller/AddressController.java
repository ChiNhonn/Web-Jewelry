package com.jewelry.store.controller;

import com.jewelry.store.dto.common.ApiResponse;
import com.jewelry.store.dto.request.AddressRequest;
import com.jewelry.store.dto.response.AddressResponse;
import com.jewelry.store.security.UserPrincipal;
import com.jewelry.store.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Shipping Addresses", description = "Endpoints for managing customer shipping addresses")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    @Operation(summary = "Get user shipping addresses", description = "Lấy danh sách sổ địa chỉ nhận hàng của khách")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<AddressResponse> addresses = addressService.getUserAddresses(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(addresses));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get address by ID", description = "Lấy chi tiết một địa chỉ giao hàng")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressById(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id) {
        AddressResponse response = addressService.getAddressById(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @Operation(summary = "Create shipping address", description = "Thêm địa chỉ giao hàng mới")
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody AddressRequest request) {
        AddressResponse response = addressService.createAddress(currentUser.getId(), request);
        return new ResponseEntity<>(ApiResponse.success("Thêm địa chỉ thành công!", response), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update shipping address", description = "Cập nhật địa chỉ giao hàng")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {
        AddressResponse response = addressService.updateAddress(currentUser.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật địa chỉ thành công!", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete shipping address", description = "Xóa địa chỉ giao hàng")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id) {
        addressService.deleteAddress(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.empty("Đã xóa địa chỉ thành công!"));
    }

    @PutMapping("/{id}/default")
    @Operation(summary = "Set default shipping address", description = "Thiết lập địa chỉ mặc định")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id) {
        AddressResponse response = addressService.setDefaultAddress(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Đã đặt làm địa chỉ mặc định!", response));
    }
}
