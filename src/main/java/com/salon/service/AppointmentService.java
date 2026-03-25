package com.salon.service;

import com.salon.dto.BookingRequest;
import com.salon.dto.BookingResponse;
import com.salon.entity.*;
import com.salon.enums.AppointmentStatus;
import com.salon.enums.PaymentMethod;
import com.salon.enums.PaymentStatus;
import com.salon.exception.BadRequestException;
import com.salon.exception.ResourceNotFoundException;
import com.salon.exception.SlotNotAvailableException;
import com.salon.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * APPOINTMENT SERVICE
 * 
 * The main booking engine. Handles:
 * - Creating appointments (with double-booking prevention)
 * - Cancelling appointments (with cancellation policy)
 * - Rescheduling appointments
 * - Listing appointments for a customer
 * 
 * KEY BUSINESS RULE: NO DOUBLE BOOKING
 * Before creating an appointment, we check if the artist already has a booking
 * that overlaps with the requested time slot. This is done at the DATABASE level
 * using a query, not just in Java code, making it more reliable under concurrent requests.
 */
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final SalonServiceRepository salonServiceRepository;
    private final PaymentRepository paymentRepository;
    private final SalonConfigRepository salonConfigRepository;
    private final ArtistLeaveRepository artistLeaveRepository;
    private final EmailService emailService;
    private final FirebaseService firebaseService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              UserRepository userRepository,
                              ArtistRepository artistRepository,
                              SalonServiceRepository salonServiceRepository,
                              PaymentRepository paymentRepository,
                              SalonConfigRepository salonConfigRepository,
                              ArtistLeaveRepository artistLeaveRepository,
                              EmailService emailService,
                              FirebaseService firebaseService) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.artistRepository = artistRepository;
        this.salonServiceRepository = salonServiceRepository;
        this.paymentRepository = paymentRepository;
        this.salonConfigRepository = salonConfigRepository;
        this.artistLeaveRepository = artistLeaveRepository;
        this.emailService = emailService;
        this.firebaseService = firebaseService;
    }

    /**
     * BOOK AN APPOINTMENT
     * 
     * Steps:
     * 1. Validate all inputs (artist exists, service exists, date is valid)
     * 2. Calculate end time from start time + service duration
     * 3. Check salon timing enforcement
     * 4. Check artist is not on leave
     * 5. Check for double-booking (CRITICAL!)
     * 6. Create the appointment
     * 7. Create a payment record
     * 8. Return the booking confirmation
     */
    @Transactional
    public BookingResponse bookAppointment(String customerEmail, BookingRequest request) {
        // 1. Validate and fetch entities
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        Artist artist = artistRepository.findById(request.getArtistId())
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));
        SalonService service = salonServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        LocalDate date = request.getAppointmentDate();
        LocalTime startTime = request.getStartTime();

        // 2. Calculate end time
        LocalTime endTime = startTime.plusMinutes(service.getDurationMinutes());

        // 3. Date validation
        if (date.isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot book appointments in the past");
        }
        if (date.equals(LocalDate.now()) && startTime.isBefore(LocalTime.now())) {
            throw new BadRequestException("Cannot book a slot that has already passed today");
        }

        // 4. Salon timing enforcement
        SalonConfig config = salonConfigRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Salon configuration not set up"));

        if (startTime.isBefore(config.getOpeningTime()) || endTime.isAfter(config.getClosingTime())) {
            throw new BadRequestException("Appointment time is outside salon operating hours ("
                    + config.getOpeningTime() + " - " + config.getClosingTime() + ")");
        }

        // 5. Check artist is not on leave
        if (artistLeaveRepository.existsByArtistIdAndLeaveDate(request.getArtistId(), date)) {
            throw new BadRequestException("The selected artist is on leave on " + date);
        }

        // 6. DOUBLE-BOOKING CHECK
        boolean hasConflict = appointmentRepository.hasOverlappingAppointment(
                request.getArtistId(), date, startTime, endTime);
        if (hasConflict) {
            throw new SlotNotAvailableException(
                    "This time slot is already booked. Please choose a different slot.");
        }

        // 7. Create the appointment
        Appointment appointment = Appointment.builder()
                .customer(customer)
                .artist(artist)
                .service(service)
                .appointmentDate(date)
                .startTime(startTime)
                .endTime(endTime)
                .status(AppointmentStatus.BOOKED)
                .notes(request.getNotes())
                .build();

        Appointment saved = appointmentRepository.save(appointment);

        // 8. Create payment record
        PaymentMethod paymentMethod = PaymentMethod.PAY_AT_SALON; // Default
        if (request.getPaymentMethod() != null) {
            try {
                paymentMethod = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Keep default if invalid method provided
            }
        }

        Payment payment = Payment.builder()
                .appointment(saved)
                .amount(service.getPrice())
                .method(paymentMethod)
                .status(paymentMethod == PaymentMethod.PAY_AT_SALON ?
                        PaymentStatus.PENDING : PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);

        // 9. Send booking confirmation notifications
        try {
            emailService.sendBookingConfirmation(
                    customer.getEmail(),
                    customer.getFullName(),
                    service.getName(),
                    artist.getUser().getFullName(),
                    date,
                    startTime,
                    saved.getId().toString()
            );

            // Send push notification if device token exists
            // Note: Device token would be stored in User entity (add fcmToken field)
            firebaseService.sendBookingConfirmation(
                    null, // TODO: Get FCM token from customer.getFcmToken()
                    service.getName(),
                    date.toString(),
                    startTime.toString()
            );
        } catch (Exception e) {
            // Log but don't fail the booking if notification fails
            System.err.println("Failed to send booking notification: " + e.getMessage());
        }

        return mapToResponse(saved);
    }

    /**
     * CANCEL AN APPOINTMENT
     * 
     * Business Rule: Cancellation is only allowed if it's more than X hours
     * before the appointment (configured in SalonConfig.cancellationPolicyHours).
     */
    public BookingResponse cancelAppointment(Long appointmentId, String customerEmail) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        // Verify the customer owns this appointment
        if (!appointment.getCustomer().getEmail().equals(customerEmail)) {
            throw new BadRequestException("You can only cancel your own appointments");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BadRequestException("This appointment is already cancelled");
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel a completed appointment");
        }

        // Check cancellation policy
        SalonConfig config = salonConfigRepository.findAll().stream().findFirst().orElse(null);
        if (config != null) {
            LocalDateTime appointmentDateTime = LocalDateTime.of(
                    appointment.getAppointmentDate(), appointment.getStartTime());
            LocalDateTime cancellationDeadline = appointmentDateTime
                    .minusHours(config.getCancellationPolicyHours());

            if (LocalDateTime.now().isAfter(cancellationDeadline)) {
                throw new BadRequestException("Cancellation is not allowed less than "
                        + config.getCancellationPolicyHours()
                        + " hours before the appointment");
            }
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment saved = appointmentRepository.save(appointment);

        // Send cancellation notification
        try {
            emailService.sendCancellationNotification(
                    appointment.getCustomer().getEmail(),
                    appointment.getCustomer().getFullName(),
                    appointment.getService().getName(),
                    appointment.getAppointmentDate(),
                    appointment.getStartTime()
            );

            firebaseService.sendCancellationNotification(
                    null, // TODO: Get FCM token from customer
                    appointment.getService().getName()
            );
        } catch (Exception e) {
            System.err.println("Failed to send cancellation notification: " + e.getMessage());
        }

        // Update payment status to REFUNDED if it was completed
        paymentRepository.findByAppointmentId(appointmentId).ifPresent(payment -> {
            if (payment.getStatus() == PaymentStatus.COMPLETED) {
                payment.setStatus(PaymentStatus.REFUNDED);
                paymentRepository.save(payment);
            }
        });

        return mapToResponse(saved);
    }

    /**
     * RESCHEDULE AN APPOINTMENT
     * 
     * Cancels the old appointment and creates a new one.
     */
    @Transactional
    public BookingResponse rescheduleAppointment(Long appointmentId, String customerEmail,
                                                  BookingRequest newRequest) {
        // Cancel the old appointment
        cancelAppointment(appointmentId, customerEmail);

        // Book a new one with the updated details
        return bookAppointment(customerEmail, newRequest);
    }

    /** Get all appointments for a customer */
    public List<BookingResponse> getCustomerAppointments(String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return appointmentRepository.findByCustomerIdOrderByAppointmentDateDescStartTimeDesc(customer.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /** Get upcoming appointments for a customer */
    public List<BookingResponse> getUpcomingAppointments(String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return appointmentRepository.findByCustomerIdAndStatus(customer.getId(), AppointmentStatus.BOOKED)
                .stream()
                .filter(a -> !a.getAppointmentDate().isBefore(LocalDate.now()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /** Get past appointments for a customer */
    public List<BookingResponse> getPastAppointments(String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return appointmentRepository.findByCustomerIdOrderByAppointmentDateDescStartTimeDesc(customer.getId())
                .stream()
                .filter(a -> a.getAppointmentDate().isBefore(LocalDate.now())
                        || a.getStatus() == AppointmentStatus.COMPLETED
                        || a.getStatus() == AppointmentStatus.CANCELLED)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /** Get all appointments (for admin) with optional filters */
    public List<BookingResponse> getAllAppointments(LocalDate date, Long artistId, String status) {
        List<Appointment> appointments;

        if (date != null && artistId != null) {
            appointments = appointmentRepository.findByAppointmentDate(date).stream()
                    .filter(a -> a.getArtist().getId().equals(artistId))
                    .collect(Collectors.toList());
        } else if (date != null) {
            appointments = appointmentRepository.findByAppointmentDate(date);
        } else if (artistId != null) {
            appointments = appointmentRepository.findByArtistId(artistId);
        } else {
            appointments = appointmentRepository.findAll();
        }

        if (status != null && !status.isEmpty()) {
            AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
            appointments = appointments.stream()
                    .filter(a -> a.getStatus() == appointmentStatus)
                    .collect(Collectors.toList());
        }

        return appointments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /** Helper: Convert Appointment entity → BookingResponse DTO */
    private BookingResponse mapToResponse(Appointment appointment) {
        Payment payment = paymentRepository.findByAppointmentId(appointment.getId()).orElse(null);

        return BookingResponse.builder()
                .id(appointment.getId())
                .customerName(appointment.getCustomer().getFullName())
                .artistId(appointment.getArtist().getId())
                .artistName(appointment.getArtist().getUser().getFullName())
                .serviceId(appointment.getService().getId())
                .serviceName(appointment.getService().getName())
                .appointmentDate(appointment.getAppointmentDate())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus().name())
                .notes(appointment.getNotes())
                .price(appointment.getService().getPrice())
                .paymentStatus(payment != null ? payment.getStatus().name() : null)
                .paymentMethod(payment != null ? payment.getMethod().name() : null)
                .build();
    }
}
