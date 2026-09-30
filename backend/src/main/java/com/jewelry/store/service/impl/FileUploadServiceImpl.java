package com.jewelry.store.service.impl;

import com.jewelry.store.exception.BadRequestException;
import com.jewelry.store.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    private final Path uploadRootPath;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");

    public FileUploadServiceImpl(@Value("${file.upload-dir:uploads}") String uploadDir) {
        this.uploadRootPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadRootPath);
        } catch (IOException e) {
            throw new IllegalStateException("Không thể khởi tạo thư mục lưu trữ tệp tin: " + uploadDir, e);
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String subDirectory) {
        validateFile(file);

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = getFileExtension(originalFilename);

        String uniqueFilename = UUID.randomUUID() + "." + extension;

        try {
            Path targetDir = uploadRootPath;
            if (subDirectory != null && !subDirectory.trim().isEmpty()) {
                targetDir = uploadRootPath.resolve(subDirectory.trim()).normalize();
                Files.createDirectories(targetDir);
            }

            Path targetLocation = targetDir.resolve(uniqueFilename);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            String relativePath = (subDirectory != null && !subDirectory.trim().isEmpty())
                    ? "/uploads/" + subDirectory.trim() + "/" + uniqueFilename
                    : "/uploads/" + uniqueFilename;

            log.info("Uploaded file successfully: {} (Size: {} bytes)", relativePath, file.getSize());
            return relativePath;
        } catch (IOException ex) {
            log.error("Failed to store file: {}", originalFilename, ex);
            throw new BadRequestException("Không thể lưu trữ tệp tin. Vui lòng thử lại!");
        }
    }

    @Override
    public List<String> uploadMultipleFiles(List<MultipartFile> files, String subDirectory) {
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }
        if (files.size() > 5) {
            throw new BadRequestException("Mỗi lần tải lên tối đa 5 hình ảnh");
        }

        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                urls.add(uploadFile(file, subDirectory));
            }
        }
        return urls;
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith("/uploads/")) {
            return;
        }
        try {
            String relativePath = fileUrl.substring("/uploads/".length());
            Path filePath = uploadRootPath.resolve(relativePath).normalize();
            if (filePath.startsWith(uploadRootPath)) {
                Files.deleteIfExists(filePath);
                log.info("Deleted media file: {}", filePath);
            }
        } catch (IOException e) {
            log.warn("Could not delete file: {}", fileUrl, e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Tệp tin tải lên không được rỗng");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("Kích thước tệp tin tối đa là 5MB (Tệp hiện tại: " + file.getSize() / 1024 / 1024 + "MB)");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Chỉ chấp nhận tệp tin định dạng hình ảnh (JPEG, PNG, WEBP, GIF)");
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BadRequestException("Định dạng đuôi tệp ." + extension + " không được hỗ trợ");
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
}
