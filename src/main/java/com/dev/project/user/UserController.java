package com.dev.project.user;

import com.dev.project.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/api/users") // 복수형 권장
    public ResponseEntity<ApiResponse<List<UserDTO>>> findAll() {
        List<UserDTO> userList = userService.findAll();

        // 실무에서는 데이터뿐만 아니라 성공 메시지 등을 함께 보냅니다.
        ApiResponse<List<UserDTO>> response = ApiResponse.<List<UserDTO>>builder()
                .success(true)
                .message("사용자 목록 조회 성공")
                .data(userList)
                .build();

        return ResponseEntity.ok(response);
    }
}
