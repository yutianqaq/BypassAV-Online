package com.yutian4090.bypass;

import com.yutian4090.bypass.utils.TextFileProcessor;
import org.junit.Test;

import java.util.Arrays;


public class TextFileProcessorTests {
    @Test
    public void testGenerateRandomKey() {
        int keyLength = 8;
        byte[] key = TextFileProcessor.generateRandomString(keyLength).getBytes();
        System.out.println("Random Key: " + TextFileProcessor.convertToHexStringWithPrefix(key));

    }

    @Test
    public void testXorEncrypt() {
        int keyLength = 8;
        byte[] plaintext = "hello".getBytes();
//        byte[] key = TextFileProcessor.generateRandomString(keyLength).getBytes();
        byte[] key =  "test".getBytes();
        byte[] encrypted = TextFileProcessor.xorEncrypt(plaintext, key);
        System.out.println("Encrypted: " + TextFileProcessor.convertToHexStringWithPrefix(encrypted));

        byte[] decrypted = TextFileProcessor.xorEncrypt(encrypted, key);
        System.out.println("Decrypted: " + new String(decrypted));
        System.out.println("Decrypted: " + TextFileProcessor.convertToHexStringWithPrefix(decrypted));
        System.out.println("Decrypted: " + TextFileProcessor.convertToHexStringWithPrefix(key));
        System.out.println("Decrypted: " + Arrays.toString(key));
    }



}
