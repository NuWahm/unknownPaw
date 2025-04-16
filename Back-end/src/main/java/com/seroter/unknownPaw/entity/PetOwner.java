package com.seroter.unknownPaw.entity;

import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.entity.Post;
import com.seroter.unknownPaw.entity.ServiceCategory;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PetOwner extends Post {

//    public void changeTitle() {
//        this.changeTitle = title;
//    }
//    public void changeContent() {
//        this.changeContent = content;
//    }

}
