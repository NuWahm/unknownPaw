package com.seroter.unknownPaw.entity;

import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.entity.Post;
import com.seroter.unknownPaw.entity.ServiceCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetSitter extends Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petSitterId;

}