package com.jewelry.store.controller;

import com.jewelry.store.dto.common.ApiResponse;
import com.jewelry.store.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Media Uploads", description = "Endpoints for uploading jewelry and review photos")
public class UploadController {

    private final FileUploadService fileUploadService;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload single image", description = "Tải lên 1 hình ảnh trang sức hoặc ảnh feedback đánh giá (Tối đa 5MB)")
    public ResponseEntity<ApiResponse<String>> uploadSingleImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {
        String fileUrl = fileUploadService.uploadFile(file, folder);
        return ResponseEntity.ok(ApiResponse.success("Tải lên hình ảnh thành công!", fileUrl));
    }

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload multiple images", description = "Tải lên nhiều hình ảnh (Tối đa 5 ảnh mỗi lần tải)")
    public ResponseEntity<ApiResponse<List<String>>> uploadMultipleImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "folder", defaultValue = "reviews") String folder) {
        List<String> fileUrls = fileUploadService.uploadMultipleFiles(files, folder);
        return ResponseEntity.ok(ApiResponse.success("Tải lên danh sách hình ảnh thành công!", fileUrls));
    }
}
