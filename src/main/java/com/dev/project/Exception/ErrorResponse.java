package com.dev.project.Exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor // 기본 생성자 추가 (Jackson 라이브러리가 사용함)
public class ErrorResponse {
    private int status;
    private String message;
}
