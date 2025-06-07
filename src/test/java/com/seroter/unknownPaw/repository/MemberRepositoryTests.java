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

    @Test
    public void createDummyMembers() {
        Random random = new Random();
        String[] names = {"김철수", "이영희", "박지민", "최수진", "정민준", "강다은", "윤서연", "임준호", "한미영", "송태호"};
        String[] addresses = {"부산시 해운대구", "부산시 수영구", "부산시 부산진구", "부산시 동래구", "부산시 남구", 
                            "부산시 북구", "부산시 중구", "부산시 서구", "부산시 동구", "부산시 영도구"};

        for (int i = 1; i <= 50; i++) {
            String name = names[random.nextInt(names.length)];
            String address = addresses[random.nextInt(addresses.length)];
            
            Member member = Member.builder()
                    .email("user" + i + "@example.com")
                    .password(passwordEncoder.encode("password" + i))
                    .name(name)
                    .nickname(name + random.nextInt(1000))
                    .phoneNumber("010-" + String.format("%04d", random.nextInt(10000)) + "-" + String.format("%04d", random.nextInt(10000)))
                    .pawRate(random.nextFloat() * 5.0f)
                    .gender(random.nextBoolean())
                    .birthday(1980 + random.nextInt(30))
                    .address(address)
                    .emailVerified(true)
                    .fromSocial(false)
                    .role(Member.Role.USER)
                    .status(Member.MemberStatus.ACTIVE)
                    .signupChannel("test")
                    .build();

            memberRepository.save(member);
        }
    }
}