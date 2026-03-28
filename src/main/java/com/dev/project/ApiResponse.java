package com.dev.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data; // 여기에 List<UserDTO>가 들어갑니다.
}