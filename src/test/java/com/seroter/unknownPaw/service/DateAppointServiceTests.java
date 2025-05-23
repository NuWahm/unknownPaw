package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.DateAppointRequestDTO;
import com.seroter.unknownPaw.entity.Enum.ServiceCategory;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.entity.Pet;
import com.seroter.unknownPaw.entity.PetOwner;
import com.seroter.unknownPaw.entity.PetSitter;
import com.seroter.unknownPaw.repository.MemberRepository;
import com.seroter.unknownPaw.repository.PetOwnerRepository;
import com.seroter.unknownPaw.repository.PetRepository;
import com.seroter.unknownPaw.repository.PetSitterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


import static org.assertj.core.api.Assertions.assertThat;


@Rollback(false)
@SpringBootTest

public class DateAppointServiceTests {


  @Autowired
  private DateAppointService dateAppointService;


  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private PetRepository petRepository;

  @Autowired
  private PetOwnerRepository petOwnerPostRepository;

  @Autowired
  private PetSitterRepository petSitterPostRepository;

  @Test
  public void create100DateAppointsTest() {


    Member member = memberRepository.findAll().stream().findFirst()
        .orElseThrow(() -> new RuntimeException("No members found"));
    System.out.println("😄 Member ID: " + member.getMid());
    Pet pet = petRepository.findAll().stream().findFirst()
        .orElseThrow(() -> new RuntimeException("No pets found"));
    System.out.println("🐶 Pet ID: " + pet.getPetId());
    PetOwner ownerPost = petOwnerPostRepository.findAll().stream().findFirst()
        .orElseThrow(() -> new RuntimeException("No pet owner posts found"));
    System.out.println("🐸 PetOwnerPost ID: " + ownerPost.getPostId());
    PetSitter sitterPost = petSitterPostRepository.findAll().stream().findFirst()
        .orElseThrow(() -> new RuntimeException("No pet sitter posts found"));
    System.out.println("🐔 PetSitterPost ID: " + sitterPost.getPostId());


    for (int i = 1; i <= 100; i++) {
      DateAppointRequestDTO requestDTO = DateAppointRequestDTO.builder()
          .decideHourRate(10000 + (i * 100))
          .readTheOriginalText(i % 2 == 0)
          .chat("Dummy chat message #" + i)
          .defaultLocation("Location " + (i % 10))
          .flexibleLocation("Flexible Location " + (i % 5))
          .confirmationDate(LocalDateTime.now().minusDays(i % 7))
          .futureDate(LocalDateTime.now().plusDays(i % 30))
          .serviceCategory(i % 3 == 0 ? ServiceCategory.WALK : (i % 3 == 1 ? ServiceCategory.CARE : ServiceCategory.HOTEL))
          .mid(member.getMid())
          .petId(pet.getPetId())
          .imgId(null)
          .petOwnerPostId(ownerPost.getPostId())
          .petSitterPostId(sitterPost.getPostId())
          .build();

      var responseDTO = dateAppointService.create(requestDTO);
      assertThat(responseDTO).isNotNull();

    }
  }
}







