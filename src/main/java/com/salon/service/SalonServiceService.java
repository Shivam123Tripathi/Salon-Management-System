package com.salon.service;

import com.salon.dto.ServiceDTO;
import com.salon.entity.SalonService;
import com.salon.exception.BadRequestException;
import com.salon.exception.ResourceNotFoundException;
import com.salon.repository.SalonServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SALON SERVICE SERVICE (yes, the naming is a bit redundant!)
 * 
 * Manages the salon's service catalog (Haircut, Facial, Manicure, etc.).
 * Provides CRUD operations and listing for both customers and admins.
 */
@Service
public class SalonServiceService {

    private final SalonServiceRepository serviceRepository;

    public SalonServiceService(SalonServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    /** Get all active services (for customer browsing) */
    public List<ServiceDTO> getAllActiveServices() {
        return serviceRepository.findByActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /** Get all services including inactive ones (for admin) */
    public List<ServiceDTO> getAllServices() {
        return serviceRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /** Get service by ID */
    public ServiceDTO getServiceById(Long id) {
        SalonService service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + id));
        return mapToDTO(service);
    }

    /** Get services by category */
    public List<ServiceDTO> getServicesByCategory(String category) {
        return serviceRepository.findByCategoryAndActiveTrue(category).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /** Create a new service (admin only) */
    public ServiceDTO createService(ServiceDTO dto) {
        if (serviceRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new BadRequestException("A service with this name already exists");
        }

        SalonService service = SalonService.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .durationMinutes(dto.getDurationMinutes())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .active(true)
                .build();

        return mapToDTO(serviceRepository.save(service));
    }

    /** Update an existing service (admin only) */
    public ServiceDTO updateService(Long id, ServiceDTO dto) {
        SalonService service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + id));

        if (dto.getName() != null) service.setName(dto.getName());
        if (dto.getDescription() != null) service.setDescription(dto.getDescription());
        if (dto.getDurationMinutes() != null) service.setDurationMinutes(dto.getDurationMinutes());
        if (dto.getPrice() != null) service.setPrice(dto.getPrice());
        if (dto.getCategory() != null) service.setCategory(dto.getCategory());
        if (dto.getActive() != null) service.setActive(dto.getActive());

        return mapToDTO(serviceRepository.save(service));
    }

    /** Soft-delete a service (set active = false instead of actually deleting) */
    public void deactivateService(Long id) {
        SalonService service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + id));
        service.setActive(false);
        serviceRepository.save(service);
    }

    /** Helper: Convert Entity → DTO */
    private ServiceDTO mapToDTO(SalonService service) {
        return ServiceDTO.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .durationMinutes(service.getDurationMinutes())
                .price(service.getPrice())
                .category(service.getCategory())
                .active(service.getActive())
                .build();
    }
}
