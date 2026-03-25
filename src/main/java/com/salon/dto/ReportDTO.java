package com.salon.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.Map;

/**
 * REPORT DTO
 * 
 * Used for analytics reports: revenue by date, popular services, peak hours, etc.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDTO {
    /** Revenue grouped by date (e.g., "2024-01-15" → 5000.00) */
    private Map<String, BigDecimal> revenueByDate;
    
    /** Total revenue in the requested period */
    private BigDecimal totalRevenue;
    
    /** Service name → booking count (e.g., "Haircut" → 45) */
    private Map<String, Long> popularServices;
    
    /** Hour of day → booking count (e.g., "10" → 12 means 12 bookings at 10 AM) */
    private Map<String, Long> peakHours;
    
    /** Artist name → booking count */
    private Map<String, Long> artistPerformance;
}
