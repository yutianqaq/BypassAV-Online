package com.yutian4090.bypass.utils;

import com.yutian4090.bypass.config.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
@Component
public class TextFileProcessor {
    private static final int MAX_LETTER_LENGTH = 10;

    private static ApplicationProperties applicationProperties;

    @Autowired
    public void setApplicationProperties(ApplicationProperties applicationProperties) {
        TextFileProcessor.applicationProperties = applicationProperties;
    }
    public static String processTemplate(String code, String selectedTemplate) throws IOException {
        String template = readTemplateFromFile(selectedTemplate); // 从文件中读取模板内容
        String processedCode = template.replace("{{Shellcode}}", code);

        int occurrences = countCommas(code);
        processedCode = processedCode.replace("{{Len}}", String.valueOf(occurrences + 1));

        List<String> variableNamesToReplace = Arrays.asList("calc_payload", "calc_len", "calc", "rv", "th", "oldProtect", "tId", "rPtr", "wSuccess", "tHandle");

        processedCode = TextFileProcessor.replaceVariableNames(processedCode, variableNamesToReplace);

        return processedCode;
    }
    public static String readTemplateFromFile(String templateFileName) throws IOException {
        String templateFilePath = applicationProperties.getTemplatesDirectory() + templateFileName;
        System.out.println(templateFilePath);
        byte[] templateBytes = Files.readAllBytes(Paths.get(templateFilePath));
        return new String(templateBytes, StandardCharsets.UTF_8);
    }

    public static String replaceVariableNames(String code, List<String> variableNames) {
        Random random = new Random();
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int variableNameLength = 8;

        for (String variableName : variableNames) {
            String generatedVariableName = generateRandomVariableName(random, characters, variableNameLength);
            code = code.replace(variableName, generatedVariableName);
        }

        return code;
    }

    private static String generateRandomVariableName(Random random, String characters, int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            stringBuilder.append(characters.charAt(index));
        }
        return stringBuilder.toString();
    }


    public static String generateRandomFileName() {
        Random random = new Random();
        int letterLength = random.nextInt(MAX_LETTER_LENGTH) + 1;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < letterLength; i++) {
            char letter = (char) (random.nextInt(26) + 'a');
            sb.append(letter);
        }

        return sb.toString();
    }

    private static int countCommas(String text) {
        int count = 0;

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ',') {
                count++;
            }
        }

        return count;
    }
}
