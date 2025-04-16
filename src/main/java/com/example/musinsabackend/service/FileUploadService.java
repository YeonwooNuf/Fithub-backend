package com.example.musinsabackend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final String UPLOAD_DIR = "/app/uploads/profile-images/";

    public String saveProfileImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("업로드된 파일이 없습니다.");
        }

        try {
            String ext = getFileExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID().toString() + "." + ext;
            File dest = new File(UPLOAD_DIR + filename);

            file.transferTo(dest);

            return filename; // 저장된 파일 이름만 반환 (DB에 저장될 값)
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 저장 실패: " + e.getMessage());
        }
    }

    private String getFileExtension(String originalName) {
        if (originalName == null || !originalName.contains(".")) {
            throw new IllegalArgumentException("잘못된 파일명입니다.");
        }
        return originalName.substring(originalName.lastIndexOf(".") + 1);
    }
}
