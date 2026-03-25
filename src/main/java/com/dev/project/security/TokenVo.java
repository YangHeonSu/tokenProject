package com.dev.project.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TokenVo {

    private String grantType;
    private String accessToken;
    private String refreshToken;
}
