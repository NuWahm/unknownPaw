package com.seroter.unknownPaw.entity;

import com.seroter.unknownPaw.entity.Enum.PostType;
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
@DiscriminatorValue("PET_OWNER")
public class PetOwner extends Post {
    @ManyToOne
    private Pet pet;



}