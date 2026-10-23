package com.Learning_Managnment_System.JWD_70_lms.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class CourseFileUploadService {
    
    private final String UPLOAD_DIR = "uploads/courses/";
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2 MB (In Bytes)

    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 2MB limit. Please upload a smaller image.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Invalid file type. Only image files (JPG, PNG, WEBP, etc.) are allowed.");
        }

        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String newFileName = UUID.randomUUID().toString() + extension;
        Path path = Paths.get(UPLOAD_DIR + newFileName);
        Files.write(path, file.getBytes());

        return newFileName; // DB တွင် သိမ်းရန် ဖိုင်အမည် အသစ်ပြန်ပေးခြင်း
    }
}