package com.jewelry.store.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileUploadService {

    String uploadFile(MultipartFile file, String subDirectory);

    List<String> uploadMultipleFiles(List<MultipartFile> files, String subDirectory);

    void deleteFile(String fileUrl);
}
