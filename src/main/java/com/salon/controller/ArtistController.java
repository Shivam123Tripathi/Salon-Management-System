package com.salon.controller;

import com.salon.dto.ApiResponse;
import com.salon.dto.ArtistDTO;
import com.salon.dto.SlotDTO;
import com.salon.service.ArtistManagementService;
import com.salon.service.SlotService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * ARTIST CONTROLLER
 * 
 * Lists artists and their available slots.
 * GET endpoints are PUBLIC (browse artists without login).
 * 
 * @DateTimeFormat: Tells Spring how to parse date strings from URL parameters.
 * ISO format: "2024-01-15"
 */
@RestController
@RequestMapping("/api/artists")
@CrossOrigin(origins = "*")
public class ArtistController {

    private final ArtistManagementService artistService;
    private final SlotService slotService;

    public ArtistController(ArtistManagementService artistService, SlotService slotService) {
        this.artistService = artistService;
        this.slotService = slotService;
    }

    /**
     * LIST ALL ARTISTS
     * 
     * GET /api/artists
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ArtistDTO>>> getAllArtists() {
        List<ArtistDTO> artists = artistService.getAllArtists();
        return ResponseEntity.ok(ApiResponse.success("Artists retrieved", artists));
    }

    /**
     * GET ARTIST BY ID
     * 
     * GET /api/artists/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArtistDTO>> getArtistById(@PathVariable Long id) {
        ArtistDTO artist = artistService.getArtistById(id);
        return ResponseEntity.ok(ApiResponse.success("Artist retrieved", artist));
    }

    /**
     * GET AVAILABLE SLOTS FOR AN ARTIST
     * 
     * GET /api/artists/1/slots?serviceId=2&date=2024-01-15
     * 
     * This is the KEY endpoint in the booking flow.
     * The Android app calls this to show available time slots to the customer.
     * 
     * @RequestParam: Reads query parameters from the URL.
     * Example: ?serviceId=2&date=2024-01-15
     */
    @GetMapping("/{artistId}/slots")
    public ResponseEntity<ApiResponse<List<SlotDTO>>> getAvailableSlots(
            @PathVariable Long artistId,
            @RequestParam Long serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<SlotDTO> slots = slotService.getAvailableSlots(artistId, serviceId, date);
        return ResponseEntity.ok(ApiResponse.success("Slots retrieved", slots));
    }

    /**
     * GET ONLY AVAILABLE ARTISTS
     * 
     * GET /api/artists/available
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<ArtistDTO>>> getAvailableArtists() {
        List<ArtistDTO> artists = artistService.getAvailableArtists();
        return ResponseEntity.ok(ApiResponse.success("Available artists retrieved", artists));
    }
}
