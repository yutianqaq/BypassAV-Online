package com.yutian4090.bypass.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yutian4090.bypass.config.ApplicationProperties;
import com.yutian4090.bypass.dto.CompilationResponseDTO;
import com.yutian4090.bypass.dto.CompileRequestDTO;
import com.yutian4090.bypass.enums.Result;
import com.yutian4090.bypass.service.CompileService;
import com.yutian4090.bypass.utils.TextFileProcessor;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


import static com.yutian4090.bypass.utils.FileService.*;

@RestController
public class CodeController {

    private static ApplicationProperties applicationProperties;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    public void setApplicationProperties(ApplicationProperties applicationProperties) {
        CodeController.applicationProperties = applicationProperties;
    }
    @Resource
    CompileService CompileService;


    @PostMapping(value = "/v1/upload/compileC")
    public Result compileUploadCodeC(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "templateName", required = false) String templateName
    ) throws IOException {
        CompileRequestDTO compileC = new CompileRequestDTO();

        compileC.setTemplateName(templateName);
        if (file != null) {
            byte[] fileBytes = file.getBytes();
            String hexString = TextFileProcessor.convertToHexStringWithPrefix(fileBytes);
            compileC.setCode(hexString);
        }

        String selectedTemplate = compileC.getTemplateName();
        String templateFileName = applicationProperties.getTemplateCMapping().get(selectedTemplate);
        System.out.println(compileC);
        if (templateFileName == null) {
            return Result.error("未找到");
        } else {
            compileC.setTemplateName(templateFileName);
        }

        CompilationResponseDTO res = CompileService.compileCodeC(compileC);
        return Result.success(res);
    }

    @PostMapping(value = "/v1/upload/compileNIM")
    public Result compileUploadCodeNim(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "templateName", required = false) String templateName
    ) throws IOException {
        CompileRequestDTO compileNIM = new CompileRequestDTO();

        compileNIM.setTemplateName(templateName);
        if (file != null) {
            byte[] fileBytes = file.getBytes();
            String hexString = TextFileProcessor.convertToHexStringWithPrefix(fileBytes);
            compileNIM.setCode(hexString);
        }

        String selectedTemplate = compileNIM.getTemplateName();
        String templateFileName = applicationProperties.getTemplateNIMMapping().get(selectedTemplate);
        System.out.println(compileNIM);
        if (templateFileName == null) {
            return Result.error("未找到");
        } else {
            compileNIM.setTemplateName(templateFileName);
        }

        CompilationResponseDTO res = CompileService.compileCodeNIM(compileNIM);
        return Result.success(res);
    }

    @PostMapping(value = "/v1/compileC")
    public Result compileCodeC(@RequestBody CompileRequestDTO compileC) throws IOException {

        System.out.println(compileC);
        String selectedTemplate = compileC.getTemplateName();
        String templateFileName = applicationProperties.getTemplateCMapping().get(selectedTemplate);
        if (templateFileName == null) {
            return Result.error("未找到");
        } else {
            compileC.setTemplateName(templateFileName);
        }

        CompilationResponseDTO res = CompileService.compileCodeC(compileC);

        return Result.success(res);
    }

    @PostMapping("/v1/compileNIM")
    public Result compileCodeNim(@RequestBody CompileRequestDTO compileNIM) throws IOException {

        String selectedTemplate = compileNIM.getTemplateName();
        String templateFileName = applicationProperties.getTemplateNIMMapping().get(selectedTemplate);
        if (templateFileName == null) {
            return Result.error("未找到");
        } else {
            compileNIM.setTemplateName(templateFileName);
        }

        CompilationResponseDTO res = CompileService.compileCodeNIM(compileNIM);
        return Result.success(res);
    }

    @GetMapping("/v1/download/{filename}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String filename) throws IOException {
        byte[] fileBytes = getFileBytesFromStorage(filename);

        String contentType = determineContentType(filename);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());

        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

}
