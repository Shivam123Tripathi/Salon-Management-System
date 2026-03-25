package com.salon.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * FILE UPLOAD SERVICE
 *
 * Handles file uploads (images for artists and services).
 */
@Service
public class FileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    @Value("${file.upload.dir:./uploads}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif"};

    /**
     * UPLOAD FILE
     *
     * Saves the uploaded file to the server and returns the file path.
     *
     * @param file Uploaded file
     * @param subfolder Subfolder name (e.g., "artists", "services")
     * @return File path relative to upload directory
     */
    public String uploadFile(MultipartFile file, String subfolder) throws IOException {
        // Validate file
        validateFile(file);

        // Create upload directory if not exists
        Path uploadPath = Paths.get(uploadDir, subfolder);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString() + "." + extension;

        // Save file
        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        logger.info("File uploaded successfully: {}", filePath);

        // Return relative path
        return subfolder + "/" + newFilename;
    }

    /**
     * DELETE FILE
     *
     * Deletes a file from the server.
     *
     * @param filePath File path relative to upload directory
     */
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(uploadDir, filePath);
            Files.deleteIfExists(path);
            logger.info("File deleted: {}", filePath);
        } catch (IOException e) {
            logger.error("Failed to delete file {}: {}", filePath, e.getMessage());
        }
    }

    /**
     * VALIDATE FILE
     */
    private void validateFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException("File size exceeds 5MB limit");
        }

        String extension = getFileExtension(file.getOriginalFilename());
        boolean isAllowed = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equalsIgnoreCase(extension)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            throw new IOException("File type not allowed. Allowed types: jpg, jpeg, png, gif");
        }
    }

    /**
     * GET FILE EXTENSION
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
