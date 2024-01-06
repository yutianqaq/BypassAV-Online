package com.yutian4090.bypass.utils;


import ch.qos.logback.classic.Logger;
import com.yutian4090.bypass.config.ApplicationProperties;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

@Component
public class FileProcessor {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(FileProcessor.class);

    private static ApplicationProperties applicationProperties;

    @Autowired
    public void setApplicationProperties(ApplicationProperties applicationProperties) {
        FileProcessor.applicationProperties = applicationProperties;
    }

    public static String saveFile(byte[] fileBytes, String fileName) throws IOException {
        File storageDirectory = new File(applicationProperties.getStorageDirector());
        if (!storageDirectory.exists()) {
            storageDirectory.mkdirs();
        }

        Path filePath = storageDirectory.toPath().resolve(fileName);

        Files.write(filePath, fileBytes);
        String modifiedFileName = fileName.substring(0, fileName.length() - 4);
        String filename = storageDirectory + File.separator + modifiedFileName + ".zip";
        ZipFile zipFile = new ZipFile( filename, "yutian".toCharArray());

        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(true);

        zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);

        zipFile.addFile(filePath.toFile(), zipParameters);
        Files.deleteIfExists(filePath);

        return modifiedFileName + ".zip";
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

    public static String getRandomFileName() {
        if (StringUtils.hasText("./icon")) {
            Path directory = Paths.get("./icon");
            if (Files.exists(directory) && Files.isDirectory(directory)) {
                try {
                    List<String> fileNames = Files.list(directory)
                            .filter(Files::isRegularFile)
                            .map(Path::getFileName)
                            .map(Path::toString)
                            .toList();

                    if (!fileNames.isEmpty()) {
                        Random random = new Random();
                        int randomIndex = random.nextInt(fileNames.size());
                        String randomFileName = fileNames.get(randomIndex);
                        logger.info("随机选择的文件名：{}", randomFileName);
                        return randomFileName;
                    } else {
                        logger.info("目录为空");
                    }
                } catch (IOException e) {
                    logger.error("无法读取目录中的文件", e);
                }
            } else {
                logger.info("目录不存在或不是一个有效的目录");
            }
        } else {
            logger.info("目录路径为空");
        }
        return null;
    }
}
