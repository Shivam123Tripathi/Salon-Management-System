package com.salon.controller;

import com.salon.dto.ApiResponse;
import com.salon.service.FileUploadService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * FILE UPLOAD CONTROLLER
 *
 * Handles file uploads for artists and services.
 * Admin-only endpoints.
 */
@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    /**
     * UPLOAD ARTIST IMAGE
     *
     * POST /api/upload/artist
     * Content-Type: multipart/form-data
     * Body: file=<image_file>
     *
     * Returns the file path to be saved in artist profile
     */
    @PostMapping(value = "/artist", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadArtistImage(
            @RequestParam("file") MultipartFile file) {
        try {
            String filePath = fileUploadService.uploadFile(file, "artists");
            return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully",
                    Map.of("filePath", filePath)));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Failed to upload image: " + e.getMessage()));
        }
    }

    /**
     * UPLOAD SERVICE IMAGE
     *
     * POST /api/upload/service
     * Content-Type: multipart/form-data
     * Body: file=<image_file>
     *
     * Returns the file path to be saved in service details
     */
    @PostMapping(value = "/service", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadServiceImage(
            @RequestParam("file") MultipartFile file) {
        try {
            String filePath = fileUploadService.uploadFile(file, "services");
            return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully",
                    Map.of("filePath", filePath)));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Failed to upload image: " + e.getMessage()));
        }
    }

    /**
     * UPLOAD USER PROFILE IMAGE
     *
     * POST /api/upload/profile
     * Content-Type: multipart/form-data
     * Body: file=<image_file>
     *
     * Returns the file path to be saved in user profile
     */
    @PostMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadProfileImage(
            @RequestParam("file") MultipartFile file) {
        try {
            String filePath = fileUploadService.uploadFile(file, "profiles");
            return ResponseEntity.ok(ApiResponse.success("Profile image uploaded successfully",
                    Map.of("filePath", filePath)));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Failed to upload image: " + e.getMessage()));
        }
    }

    /**
     * DELETE FILE
     *
     * DELETE /api/upload?filePath=artists/abc123.jpg
     */
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@RequestParam String filePath) {
        fileUploadService.deleteFile(filePath);
        return ResponseEntity.ok(ApiResponse.success("File deleted successfully", null));
    }
}
