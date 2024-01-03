package com.yutian4090.bypass.controller.domain;
import com.yutian4090.bypass.dto.CompileRequestDTO;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CombinedRequest {
    private MultipartFile file;
    private CompileRequestDTO compileC;

}
