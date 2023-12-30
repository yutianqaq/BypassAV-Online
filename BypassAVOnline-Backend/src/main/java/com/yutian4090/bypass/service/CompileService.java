package com.yutian4090.bypass.service;

import com.yutian4090.bypass.dto.CompilationResponseDTO;
import com.yutian4090.bypass.dto.CompileRequestDTO;

import java.io.IOException;

public interface CompileService {
    CompilationResponseDTO compileCodeC(CompileRequestDTO request) throws IOException;
    CompilationResponseDTO compileCodeNIM(CompileRequestDTO request) throws IOException;
}
