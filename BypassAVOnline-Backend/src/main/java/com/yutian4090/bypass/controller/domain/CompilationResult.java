package com.yutian4090.bypass.controller.domain;

import lombok.*;

@Getter
@Setter
public class CompilationResult {
    private byte[] fileBytes;
    private String fileName;

    public CompilationResult(byte[] fileBytes, String fileName) {
        this.fileBytes = fileBytes;
        this.fileName = fileName;
    }


}
