package com.yutian4090.bypass.utils;


import com.yutian4090.bypass.config.ApplicationProperties;
import com.yutian4090.bypass.controller.domain.CompilationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;


@Component
public class CodeCompilationUtils {

    private static ApplicationProperties applicationProperties;

    @Autowired
    public void setApplicationProperties(ApplicationProperties applicationProperties) {
        CodeCompilationUtils.applicationProperties = applicationProperties;
    }
    public static CompilationResult compileCCode(String code, String templateFileName) throws IOException {

        String processedCode = TextFileProcessor.processTemplate(code, templateFileName); // 处理模板
        // 将处理后的代码保存到临时文件
        // 生成临时文件名
        String tempFileName = UUID.randomUUID().toString() + ".c";
        String compiledFileName = UUID.randomUUID().toString() + ".exe";
        File tempFile = new File(tempFileName);

        // 保存处理后的代码到临时文件
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(processedCode);
        }

        // 编译代码为EXE文件
        ProcessBuilder processBuilder = new ProcessBuilder(applicationProperties.getCompilerC(), tempFile.getAbsolutePath(), "-o", compiledFileName);
        Process process = processBuilder.start();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 读取编译后的EXE文件
        File outputFile = new File(compiledFileName);
        byte[] fileBytes = Files.readAllBytes(outputFile.toPath());

        // 删除临时文件和输出文件
        tempFile.delete();
        outputFile.delete();

        return new CompilationResult(fileBytes, compiledFileName);
    }
    public static CompilationResult compileNimCode(String code, String templateFileName) throws IOException {

        String processedCode = TextFileProcessor.processTemplate(code, templateFileName); // 处理模板

        // 将处理后的代码保存到临时文件
        // 生成临时文件名
        String randomLetters = TextFileProcessor.generateRandomFileName();

        String uniqueId = UUID.randomUUID().toString().replace("-", "");
        String tempFileName = randomLetters + uniqueId + ".nim";
        String compiledFileName = randomLetters + uniqueId + ".exe";
        File tempFile = new File(tempFileName);

        // 保存处理后的代码到临时文件
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(processedCode);
        }
        System.out.println(compiledFileName);
        System.out.println(tempFile.getAbsolutePath());

        // nim c -d=release --cc:gcc --app=gui --cpu=amd64 -o:x1.exe x1Ldr.nim
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(applicationProperties.getCompilerNIM(), "c", "-d=release", "-d=mingw", "--app=gui", "-d:strip", "--opt:size",
                    "--cpu=amd64", "-o:" + compiledFileName, tempFile.getAbsolutePath());
            List<String> command = processBuilder.command();

            Process process = processBuilder.start();

            // 等待进程执行完成
            int exitCode = process.waitFor();

        } catch (IOException e) {
            // 处理进程启动的IO异常
            e.printStackTrace();
        } catch (InterruptedException e) {
            // 处理进程等待过程中的中断异常
            e.printStackTrace();
        }

        // 读取编译后的EXE文件
        File outputFile = new File(compiledFileName);
        byte[] fileBytes = Files.readAllBytes(outputFile.toPath());

        // 删除临时文件和输出文件
        tempFile.delete();
        outputFile.delete();

        return new CompilationResult(fileBytes, compiledFileName);
    }

}
