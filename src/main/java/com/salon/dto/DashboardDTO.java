package com.salon.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * DASHBOARD DTO
 * 
 * Returned by the admin dashboard endpoint.
 * Contains high-level stats about the salon's operations.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDTO {
    private Long totalBookings;
    private Long todayBookings;
    private Long activeArtists;
    private BigDecimal totalRevenue;
    private BigDecimal todayRevenue;
    private List<BookingResponse> recentBookings;
    private Long completedBookings;
    private Long cancelledBookings;
}
