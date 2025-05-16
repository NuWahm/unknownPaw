package com.seroter.unknownPaw.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor

public class PetSitter extends Post {
    @Column(name = "desired_hourly_rate", nullable = false)
    @Builder.Default
    private Integer desiredHourlyRate = 5000;

    public int getHourlyRate() {
        return desiredHourlyRate;
    }

    public void setHourlyRate(int hourlyRate) {
        this.desiredHourlyRate = hourlyRate;
    }

    @Builder.Default
    @OneToMany(mappedBy = "petSitter",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Image> images = new ArrayList<>();



}


