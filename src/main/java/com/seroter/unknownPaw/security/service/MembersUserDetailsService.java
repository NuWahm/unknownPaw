package com.seroter.unknownPaw.security.service;

import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.repository.MemberRepository;
import com.seroter.unknownPaw.security.dto.MemberAuthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class MembersUserDetailsService implements UserDetailsService {
  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<Member> result = memberRepository.findByEmailAndFromSocial(username, false);
    if (!result.isPresent()) throw new UsernameNotFoundException("Check Email or Social");
    Member member = result.get();

    MemberAuthDTO membersAuthDTO = new MemberAuthDTO(
        member.getEmail(), member.getPassword(),
        member.getRoleSet().stream().map(
            membersRole -> new SimpleGrantedAuthority(
                "ROLE_" + membersRole.name()
            )
        ).collect(Collectors.toList()),
        member.getEmail(),
        member.getName(), member.isFromSocial()
    );
    return membersAuthDTO;
  }
}
