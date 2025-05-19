package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.entity.Member.MemberStatus;
import com.seroter.unknownPaw.entity.Member.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;
import java.util.stream.IntStream;

@SpringBootTest
class MemberRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void insertMember() {
        Random random = new Random();

        IntStream.rangeClosed(1, 100).forEach(i -> {
            boolean gender = random.nextBoolean(); // true = 남자, false = 여자

            Member member = Member.builder()
                .email("Odeng" + i + "@mogae.com")
                .password(passwordEncoder.encode("1")) // 필요 시 인코딩 주석 해제
                .name("Owner" + i)
                .nickname("MungMung" + i)
                .phoneNumber("010-1111-" + String.format("%04d", i))
                .pawRate(0.5f)
                .gender(gender)
                .birthday(1990 + (i % 10)) // 예시로 1990~1999 사이
                .address("부산시 테스트구")
                .emailVerified(true)
                .fromSocial(false)
                .role(Role.USER)
                .status(MemberStatus.ACTIVE)
                .signupChannel("test")
                .build();

            member.addRole(Role.USER);

            memberRepository.save(member);
        });
    }
}