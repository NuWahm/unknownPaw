// src/main/java/com/seroter/unknownPaw/config/WebConfig.java
package com.seroter.unknownPaw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
@RequiredArgsConstructor            // ← Lombok, 없으면 생성자 직접 작성
public class WebConfig implements WebMvcConfigurer {

    private final UploadPathProvider uploadPathProvider;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // OS마다 달라지는 업로드 루트
        String base = uploadPathProvider.getUploadPath().replace("\\", "/") + "/";

        registry.addResourceHandler("/petowner/**")
                .addResourceLocations("file:" + base + "petowner/");

        registry.addResourceHandler("/petsitter/**")
                .addResourceLocations("file:" + base + "petsitter/");

        registry.addResourceHandler("/member/**")
                .addResourceLocations("file:" + base + "member/");

        registry.addResourceHandler("/pet/**")
                .addResourceLocations("file:" + base + "pet/");

//        registry.addResourceHandler("/community/**")
//                .addResourceLocations("file:" + base + "community/");
//        System.out.println(base+"확인");

        // community 이미지 핸들러
        // URL 패턴을 /community/images/** 로 변경했는지 확인
        registry.addResourceHandler("/community/images/**") // <-- 프론트에서 요청하는 URL과 일치시켜야 합니다.
            .addResourceLocations("file:" + base + "COMMUNITY/");
        System.out.println("리소스 핸들러 등록: /unknownPaw/api/community/images/** -> file:" + base + "community/");


        // 그 외 정적 자원
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}