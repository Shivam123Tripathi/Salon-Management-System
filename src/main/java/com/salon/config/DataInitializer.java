package com.salon.config;

import com.salon.entity.Artist;
import com.salon.entity.SalonConfig;
import com.salon.entity.SalonService;
import com.salon.entity.User;
import com.salon.enums.AvailabilityStatus;
import com.salon.enums.UserRole;
import com.salon.repository.ArtistRepository;
import com.salon.repository.SalonConfigRepository;
import com.salon.repository.SalonServiceRepository;
import com.salon.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * DATA INITIALIZER
 *
 * Seeds the database with demo data on first startup.
 * Every block is guarded by a count() == 0 check so it only
 * runs when the table is empty — safe to restart the server.
 */
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            SalonConfigRepository salonConfigRepository,
            UserRepository userRepository,
            SalonServiceRepository salonServiceRepository,
            ArtistRepository artistRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // ── 1. Salon Config ───────────────────────────────────────────────
            if (salonConfigRepository.count() == 0) {
                SalonConfig config = SalonConfig.builder()
                        .salonName("My Salon")
                        .openingTime(LocalTime.of(9, 0))
                        .closingTime(LocalTime.of(21, 0))
                        .cancellationPolicyHours(2)
                        .defaultSlotIntervalMinutes(30)
                        .build();
                salonConfigRepository.save(config);
                System.out.println("✓ Salon config created.");
            }

            // ── 2. Users ──────────────────────────────────────────────────────
            User admin = ensureUser(
                    userRepository, passwordEncoder,
                    "admin@salon.com", "Salon Admin", "Demo@123", UserRole.ADMIN, "9999999999");
            User customer = ensureUser(
                    userRepository, passwordEncoder,
                    "customer@salon.com", "John Customer", "Demo@123", UserRole.CUSTOMER, "8888888888");
            User samUser = ensureUser(
                    userRepository, passwordEncoder,
                    "sam@salon.com", "Sam Smith", "artist123", UserRole.ARTIST, "9111111111");
            User lisaUser = ensureUser(
                    userRepository, passwordEncoder,
                    "lisa@salon.com", "Lisa Ray", "artist123", UserRole.ARTIST, "9222222222");
            System.out.println("✓ Demo users ensured (admin/customer password: Demo@123).");

            // ── 3. Artists (require saved User objects) ───────────────────
            Optional<Artist> existingSamArtist = artistRepository.findByUserId(samUser.getId());
            if (existingSamArtist.isEmpty()) {
                Artist sam = Artist.builder()
                        .user(samUser)
                        .specialization("Master Barber")
                        .experienceYears(5)
                        .availabilityStatus(AvailabilityStatus.AVAILABLE)
                        .workingHoursStart(LocalTime.of(9, 0))
                        .workingHoursEnd(LocalTime.of(18, 0))
                        .rating(4.8)
                        .totalReviews(42)
                        .build();
                artistRepository.save(sam);
            }

            Optional<Artist> existingLisaArtist = artistRepository.findByUserId(lisaUser.getId());
            if (existingLisaArtist.isEmpty()) {
                Artist lisa = Artist.builder()
                        .user(lisaUser)
                        .specialization("Color & Skin Specialist")
                        .experienceYears(8)
                        .availabilityStatus(AvailabilityStatus.AVAILABLE)
                        .workingHoursStart(LocalTime.of(10, 0))
                        .workingHoursEnd(LocalTime.of(19, 0))
                        .rating(4.9)
                        .totalReviews(87)
                        .build();
                artistRepository.save(lisa);
            }
            System.out.println("✓ Demo artists ensured.");

            // ── 4. Services ───────────────────────────────────────────────────
            if (salonServiceRepository.count() == 0) {
                SalonService haircut = SalonService.builder()
                        .name("Men's Haircut")
                        .description("Classic haircut with wash and styling.")
                        .price(new BigDecimal("25.00"))
                        .durationMinutes(30)
                        .category("Hair")
                        .active(true)
                        .build();

                SalonService beard = SalonService.builder()
                        .name("Beard Trim")
                        .description("Precision beard trim and shaping.")
                        .price(new BigDecimal("15.00"))
                        .durationMinutes(15)
                        .category("Hair")
                        .active(true)
                        .build();

                SalonService facial = SalonService.builder()
                        .name("Relaxing Facial")
                        .description("Deep cleansing facial treatment.")
                        .price(new BigDecimal("45.00"))
                        .durationMinutes(45)
                        .category("Skin")
                        .active(true)
                        .build();

                SalonService styling = SalonService.builder()
                        .name("Hair Styling")
                        .description("Professional hair styling for events.")
                        .price(new BigDecimal("35.00"))
                        .durationMinutes(45)
                        .category("Hair")
                        .active(true)
                        .build();

                salonServiceRepository.saveAll(List.of(haircut, beard, facial, styling));
                System.out.println("✓ Demo services created.");
            }

            System.out.println("✓ Database seeding complete.");
        };
    }

    private User ensureUser(UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            String email,
                            String fullName,
                            String rawPassword,
                            UserRole role,
                            String preferredPhone) {
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> User.builder().email(email).build());

        user.setFullName(fullName);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);

        if (preferredPhone != null && !preferredPhone.isBlank()) {
            Optional<User> phoneOwner = userRepository.findByPhone(preferredPhone);
            boolean phoneAvailable = phoneOwner.isEmpty()
                    || (phoneOwner.get().getId() != null && phoneOwner.get().getId().equals(user.getId()));
            if (phoneAvailable) {
                user.setPhone(preferredPhone);
            } else if (user.getPhone() == null || user.getPhone().isBlank()) {
                // Keep startup resilient when preferred phone is already used by another account.
                user.setPhone(null);
            }
        }

        return userRepository.save(user);
    }
}
