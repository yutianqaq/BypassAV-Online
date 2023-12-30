package com.yutian4090.bypass.controller;

import com.yutian4090.bypass.config.ApplicationProperties;
import com.yutian4090.bypass.dto.CompilationResponseDTO;
import com.yutian4090.bypass.dto.CompileRequestDTO;
import com.yutian4090.bypass.enums.Result;
import com.yutian4090.bypass.service.CompileService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.yutian4090.bypass.utils.FileService.*;

@RestController
public class CodeController {

    private static ApplicationProperties applicationProperties;

    @Autowired
    public void setApplicationProperties(ApplicationProperties applicationProperties) {
        CodeController.applicationProperties = applicationProperties;
    }
    @Resource
    CompileService CompileService;

    @PostMapping("/v1/compileC")
    public Result compileCodeC(@RequestBody CompileRequestDTO compileC) throws IOException {


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
