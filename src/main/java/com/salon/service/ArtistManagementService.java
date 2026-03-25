package com.salon.service;

import com.salon.dto.ArtistDTO;
import com.salon.entity.Artist;
import com.salon.entity.ArtistLeave;
import com.salon.entity.ArtistServiceMapping;
import com.salon.entity.User;
import com.salon.enums.AvailabilityStatus;
import com.salon.enums.UserRole;
import com.salon.exception.BadRequestException;
import com.salon.exception.ResourceNotFoundException;
import com.salon.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ARTIST MANAGEMENT SERVICE
 * 
 * Handles:
 * - CRUD operations for artists (admin functions)
 * - Artist listing (for customers to browse)
 * - Working hours, leaves, and service assignment management
 * 
 * @Transactional: When adding an artist, we create BOTH a User and an Artist
 * record. If either fails, BOTH are rolled back. This prevents "orphan" records.
 */
@Service
public class ArtistManagementService {

    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;
    private final ArtistServiceMappingRepository artistServiceMappingRepository;
    private final SalonServiceRepository salonServiceRepository;
    private final ArtistLeaveRepository artistLeaveRepository;
    private final PasswordEncoder passwordEncoder;

    public ArtistManagementService(ArtistRepository artistRepository,
                                   UserRepository userRepository,
                                   ArtistServiceMappingRepository artistServiceMappingRepository,
                                   SalonServiceRepository salonServiceRepository,
                                   ArtistLeaveRepository artistLeaveRepository,
                                   PasswordEncoder passwordEncoder) {
        this.artistRepository = artistRepository;
        this.userRepository = userRepository;
        this.artistServiceMappingRepository = artistServiceMappingRepository;
        this.salonServiceRepository = salonServiceRepository;
        this.artistLeaveRepository = artistLeaveRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Get all artists (for customer browsing) */
    public List<ArtistDTO> getAllArtists() {
        return artistRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /** Get artist by ID */
    public ArtistDTO getArtistById(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found with ID: " + id));
        return mapToDTO(artist);
    }

    /** Get available artists only */
    public List<ArtistDTO> getAvailableArtists() {
        return artistRepository.findByAvailabilityStatus(AvailabilityStatus.AVAILABLE).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Add a new artist (Admin only).
     * Creates a User account + Artist profile in one transaction.
     */
    @Transactional
    public ArtistDTO addArtist(ArtistDTO dto, String password) {
        // Check if email already exists
        if (dto.getFullName() == null || dto.getFullName().isBlank()) {
            throw new BadRequestException("Artist name is required");
        }

        // Create a user account for the artist
        String email = dto.getFullName().toLowerCase().replaceAll("\\s+", ".") + "@salon.com";
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("An artist with a similar name already exists. Email: " + email);
        }

        User user = User.builder()
                .fullName(dto.getFullName())
                .email(email)
                .password(passwordEncoder.encode(password != null ? password : "artist123"))
                .role(UserRole.ARTIST)
                .build();
        User savedUser = userRepository.save(user);

        // Create the artist profile
        Artist artist = Artist.builder()
                .user(savedUser)
                .specialization(dto.getSpecialization())
                .experienceYears(dto.getExperienceYears())
                .availabilityStatus(AvailabilityStatus.AVAILABLE)
                .workingHoursStart(dto.getWorkingHoursStart() != null ?
                        LocalTime.parse(dto.getWorkingHoursStart()) : LocalTime.of(9, 0))
                .workingHoursEnd(dto.getWorkingHoursEnd() != null ?
                        LocalTime.parse(dto.getWorkingHoursEnd()) : LocalTime.of(18, 0))
                .rating(0.0)
                .totalReviews(0)
                .build();

        return mapToDTO(artistRepository.save(artist));
    }

    /** Update artist details (Admin only) */
    @Transactional
    public ArtistDTO updateArtist(Long id, ArtistDTO dto) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found with ID: " + id));

        if (dto.getSpecialization() != null) artist.setSpecialization(dto.getSpecialization());
        if (dto.getExperienceYears() != null) artist.setExperienceYears(dto.getExperienceYears());
        if (dto.getAvailabilityStatus() != null) {
            artist.setAvailabilityStatus(AvailabilityStatus.valueOf(dto.getAvailabilityStatus()));
        }
        if (dto.getWorkingHoursStart() != null) {
            artist.setWorkingHoursStart(LocalTime.parse(dto.getWorkingHoursStart()));
        }
        if (dto.getWorkingHoursEnd() != null) {
            artist.setWorkingHoursEnd(LocalTime.parse(dto.getWorkingHoursEnd()));
        }

        // Also update the user's name if provided
        if (dto.getFullName() != null) {
            User user = artist.getUser();
            user.setFullName(dto.getFullName());
            userRepository.save(user);
        }

        return mapToDTO(artistRepository.save(artist));
    }

    /** Remove an artist (Admin only) — sets status to ON_LEAVE rather than deleting */
    public void removeArtist(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found with ID: " + id));
        artist.setAvailabilityStatus(AvailabilityStatus.ON_LEAVE);
        artistRepository.save(artist);
    }

    /** Assign a service to an artist */
    @Transactional
    public void assignServiceToArtist(Long artistId, Long serviceId) {
        if (!artistRepository.existsById(artistId)) {
            throw new ResourceNotFoundException("Artist not found with ID: " + artistId);
        }
        if (!salonServiceRepository.existsById(serviceId)) {
            throw new ResourceNotFoundException("Service not found with ID: " + serviceId);
        }
        if (artistServiceMappingRepository.existsByArtistIdAndServiceId(artistId, serviceId)) {
            throw new BadRequestException("This service is already assigned to this artist");
        }

        Artist artist = artistRepository.findById(artistId).get();
        com.salon.entity.SalonService service = salonServiceRepository.findById(serviceId).get();

        ArtistServiceMapping mapping = ArtistServiceMapping.builder()
                .artist(artist)
                .service(service)
                .build();

        artistServiceMappingRepository.save(mapping);
    }

    /** Remove a service from an artist */
    @Transactional
    public void removeServiceFromArtist(Long artistId, Long serviceId) {
        artistServiceMappingRepository.deleteByArtistIdAndServiceId(artistId, serviceId);
    }

    /** Add a leave day for an artist */
    public void addLeave(Long artistId, LocalDate leaveDate, String reason) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found with ID: " + artistId));

        if (artistLeaveRepository.existsByArtistIdAndLeaveDate(artistId, leaveDate)) {
            throw new BadRequestException("Leave already exists for this date");
        }

        ArtistLeave leave = ArtistLeave.builder()
                .artist(artist)
                .leaveDate(leaveDate)
                .reason(reason)
                .build();

        artistLeaveRepository.save(leave);
    }

    /** Set artist break times */
    public ArtistDTO setBreakTime(Long artistId, LocalTime breakStart, LocalTime breakEnd) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found with ID: " + artistId));

        artist.setBreakStartTime(breakStart);
        artist.setBreakEndTime(breakEnd);

        return mapToDTO(artistRepository.save(artist));
    }

    /** Helper: Convert Artist entity → ArtistDTO */
    private ArtistDTO mapToDTO(Artist artist) {
        List<String> serviceNames = artistServiceMappingRepository.findByArtistId(artist.getId())
                .stream()
                .map(mapping -> mapping.getService().getName())
                .collect(Collectors.toList());

        return ArtistDTO.builder()
                .id(artist.getId())
                .userId(artist.getUser().getId())
                .fullName(artist.getUser().getFullName())
                .specialization(artist.getSpecialization())
                .experienceYears(artist.getExperienceYears())
                .rating(artist.getRating())
                .totalReviews(artist.getTotalReviews())
                .availabilityStatus(artist.getAvailabilityStatus().name())
                .workingHoursStart(artist.getWorkingHoursStart() != null ?
                        artist.getWorkingHoursStart().toString() : null)
                .workingHoursEnd(artist.getWorkingHoursEnd() != null ?
                        artist.getWorkingHoursEnd().toString() : null)
                .serviceNames(serviceNames)
                .build();
    }
}
