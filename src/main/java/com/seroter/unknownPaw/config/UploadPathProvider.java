package com.seroter.unknownPaw.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UploadPathProvider {

    @Value("${upload.base-path}")
    private String basePath;

    public String getUploadPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (basePath.equals("default")) {
            if (os.contains("win")) {
                return "C:\\upload";
            } else {
                return "/Users/hozi_studio/upload";
            }
        }
        return basePath;
    }
}