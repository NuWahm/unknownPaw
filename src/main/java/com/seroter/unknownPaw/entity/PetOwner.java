package com.seroter.unknownPaw.entity;

import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.entity.Post;
import com.seroter.unknownPaw.entity.ServiceCategory;
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
public class PetOwner extends Post {
    @Builder.Default
    @OneToMany(mappedBy = "petOwner",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Image> images = new ArrayList<>();
//    public void changeTitle() {
//        this.changeTitle = title;
//    }
//    public void changeContent() {
//        this.changeContent = content;
//    }

}
