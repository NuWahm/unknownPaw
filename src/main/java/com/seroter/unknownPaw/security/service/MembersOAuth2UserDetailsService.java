package com.seroter.unknownPaw.security.service;


import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.repository.MemberRepository;
import com.seroter.unknownPaw.security.dto.MemberAuthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class MembersOAuth2UserDetailsService extends DefaultOAuth2UserService {
  private final MemberRepository memberRepository;
  @Lazy
  private final PasswordEncoder passwordEncoder;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate =
        new DefaultOAuth2UserService();
    OAuth2User oAuth2User = delegate.loadUser(userRequest);

    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    SocialType socialType = getSocialType(registrationId.trim().toString());
    String userNameAttributeName = userRequest.getClientRegistration()
        .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
    log.info("userNameAttributeName >> " + userNameAttributeName);
    Map<String, Object> attributes = oAuth2User.getAttributes();
    for (Map.Entry<String, Object> entry : attributes.entrySet()) {
      System.out.println(entry.getKey() + ":" + entry.getValue());
    }
    String email = null;
    if (socialType.name().equals("GOOGLE"))
      email = oAuth2User.getAttribute("email");
    log.info("Email: " + email);
    Member member = saveSocialMember(email);

    MemberAuthDTO membersAuthDTO = new MemberAuthDTO(
        member.getEmail(),
        member.getPassword(),
        true,
        member.getRoleSet().stream().map(
                role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
            .collect(Collectors.toList())
        , attributes
    );
    membersAuthDTO.setFromSocial(member.isFromSocial());
    membersAuthDTO.setName(member.getName());
    log.info("membersAuthDTO: " + membersAuthDTO);
    return membersAuthDTO;
  }

  private Member saveSocialMember(String email) {
    Optional<Member> result = memberRepository.findByEmailAndFromSocial(email, true);
    if (result.isPresent()) return result.get();

    Member member = Member.builder()
        .email(email)
        .password(passwordEncoder.encode("1"))
        .fromSocial(true)
        .build();
    member.addRole(Member.Role.USER);
    memberRepository.save(member);
    return member;
  }

  private SocialType getSocialType(String registrationId) {
    if (SocialType.NAVER.name().equals(registrationId)) {
      return SocialType.NAVER;
    }
    if (SocialType.KAKAO.name().equals(registrationId)) {
      return SocialType.KAKAO;
    }
    return SocialType.GOOGLE;
  }

  enum SocialType {
    KAKAO, NAVER, GOOGLE
  }
}
