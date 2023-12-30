package com.yutian4090.bypass.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CompileRequestDTO {
    private String code;
    private String templateName;

}
