package com.salon.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ArtistModel {
    @SerializedName("id") private Long id;
    @SerializedName("userId") private Long userId;
    @SerializedName(value = "fullName", alternate = {"full_name", "name", "artistName"}) private String fullName;
    @SerializedName(value = "specialization", alternate = {"speciality", "specializationName"}) private String specialization;
    @SerializedName("experienceYears") private Integer experienceYears;
    @SerializedName("rating") private Double rating;
    @SerializedName("totalReviews") private Integer totalReviews;
    @SerializedName("availabilityStatus") private String availabilityStatus;
    @SerializedName("workingHoursStart") private String workingHoursStart;
    @SerializedName("workingHoursEnd") private String workingHoursEnd;
    @SerializedName(value = "serviceNames", alternate = {"services", "service_names"}) private List<String> serviceNames;

    // Constructor for admin creating new artists
    public ArtistModel(String fullName, String specialization, Integer experienceYears,
                       String workingHoursStart, String workingHoursEnd) {
        this.fullName = fullName;
        this.specialization = specialization;
        this.experienceYears = experienceYears;
        this.workingHoursStart = workingHoursStart;
        this.workingHoursEnd = workingHoursEnd;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getSpecialization() { return specialization; }
    public Integer getExperienceYears() { return experienceYears; }
    public Double getRating() { return rating; }
    public Integer getTotalReviews() { return totalReviews; }
    public String getAvailabilityStatus() { return availabilityStatus; }
    public String getWorkingHoursStart() { return workingHoursStart; }
    public String getWorkingHoursEnd() { return workingHoursEnd; }
    public List<String> getServiceNames() { return serviceNames; }
}
