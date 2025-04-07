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
public class PetOwner extends Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petOwnerId; // 글번호 (고유 키)



//    public void changeTitle() {
//        this.changeTitle = title;
//    }
//    public void changeContent() {
//        this.changeContent = content;
//    }

}
