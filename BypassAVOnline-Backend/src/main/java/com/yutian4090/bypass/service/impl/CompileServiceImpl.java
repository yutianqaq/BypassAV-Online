package com.yutian4090.bypass.service.impl;

import com.yutian4090.bypass.controller.domain.CompilationResult;
import com.yutian4090.bypass.dto.CompilationResponseDTO;
import com.yutian4090.bypass.dto.CompileRequestDTO;
import com.yutian4090.bypass.service.CompileService;
import com.yutian4090.bypass.utils.CodeCompilationUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.yutian4090.bypass.utils.FileProcessor.saveFile;

@Service
public class CompileServiceImpl implements CompileService {

    @Override
    public CompilationResponseDTO compileCodeC(CompileRequestDTO request) throws IOException {

        CompilationResult compilationResult = CodeCompilationUtils.compileCCode(request.getCode(), request.getTemplateName());
        saveFile(compilationResult.getFileBytes(), compilationResult.getFileName());


        String downloadLink = "download/" + compilationResult.getFileName();
        CompilationResponseDTO result = new CompilationResponseDTO();

        result.setDownloadLink(downloadLink);
        return result;
    }

    @Override
    public CompilationResponseDTO compileCodeNIM(CompileRequestDTO request) throws IOException {
        CompilationResult compilationResult = CodeCompilationUtils.compileNimCode(request.getCode(), request.getTemplateName());
        String Filename = saveFile(compilationResult.getFileBytes(), compilationResult.getFileName());


        String downloadLink = "download/" + Filename;
        CompilationResponseDTO result = new CompilationResponseDTO();

        result.setDownloadLink(downloadLink);
        return result;
    }
}
