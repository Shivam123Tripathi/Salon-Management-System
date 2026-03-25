package com.salon.app.models;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class ServiceModel {
    @SerializedName("id") private Long id;
    @SerializedName("name") private String name;
    @SerializedName("description") private String description;
    @SerializedName("durationMinutes") private Integer durationMinutes;
    @SerializedName("price") private Double price;
    @SerializedName("category") private String category;
    @SerializedName("active") private Boolean active;

    // Constructor for creating new services
    public ServiceModel(String name, String description, Integer durationMinutes, Double price, String category) {
        this.name = name;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.price = price;
        this.category = category;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public Double getPrice() { return price; }
    public String getCategory() { return category; }
    public Boolean getActive() { return active; }
}
