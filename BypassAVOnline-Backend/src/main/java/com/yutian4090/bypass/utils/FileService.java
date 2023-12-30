package com.yutian4090.bypass.utils;


import com.yutian4090.bypass.config.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
@Component
public class FileService {

    private static ApplicationProperties applicationProperties;

    @Autowired
    public void setApplicationProperties(ApplicationProperties applicationProperties) {
        FileService.applicationProperties = applicationProperties;
    }

    public static void saveFile(byte[] fileBytes, String fileName) throws IOException {


        File storageDirectory = new File(applicationProperties.getStorageDirector());
        if (!storageDirectory.exists()) {
            storageDirectory.mkdirs();
        }

        Path filePath = storageDirectory.toPath().resolve(fileName);
        Files.write(filePath, fileBytes);
    }

    public static byte[] getFileBytesFromStorage(String fileName) throws IOException {
        Path filePath = Path.of(applicationProperties.getStorageDirector(), fileName);
        return Files.readAllBytes(filePath);
    }

    public static String determineContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

        switch (extension) {
            case "exe":
                return "application/octet-stream";
            default:
                return "application/octet-stream";
        }
    }
}
