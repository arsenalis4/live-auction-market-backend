package com.example.demo.controller;

import com.example.demo.dto.UserRequestDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * 인증 컨트롤러
 * NestJS의 AuthController와 동일한 역할
 * 로그인, 회원가입, 로그아웃 등의 인증 관련 기능 제공
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "인증 관리", description = "로그인, 회원가입 등 인증 관련 API")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * 회원가입
     * NestJS: @Post('register') register(@Body() createUserDto: CreateUserDto)와 같음
     */
    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "409", description = "중복된 사용자명 또는 이메일")
    })
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRequestDto userRequestDto) {
        try {
            UserResponseDto registeredUser = userService.createUser(userRequestDto);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 로그인
     * NestJS: @Post('login') login(@Body() loginDto: LoginDto)와 같음
     * 
     * 참고: 실제로는 HTTP Basic Auth를 사용하므로 별도의 로그인 엔드포인트 없이
     * Authorization 헤더를 통해 인증됩니다.
     */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 인증을 수행합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            // 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword())
            );

            // 인증 성공 시 SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            return ResponseEntity.ok("{\"message\":\"로그인 성공\",\"username\":\"" + 
                    loginRequest.getUsername() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\":\"인증 실패\",\"message\":\"사용자명 또는 비밀번호가 올바르지 않습니다\"}");
        }
    }

    /**
     * 로그아웃
     * NestJS: @Post('logout') logout(@Request() req)와 같음
     */
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 사용자를 로그아웃합니다")
    public ResponseEntity<String> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("{\"message\":\"로그아웃 완료\"}");
    }

    /**
     * 현재 로그인한 사용자 정보
     * NestJS: @Get('profile') getProfile(@Request() req)와 같음
     */
    @GetMapping("/profile")
    @Operation(summary = "프로필 조회", description = "현재 로그인한 사용자의 프로필을 조회합니다")
    public ResponseEntity<UserResponseDto> getProfile(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return userService.getUserByUsername(principal.getName())
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 로그인 요청 DTO
     * NestJS의 LoginDto와 동일한 역할
     */
    public static class LoginRequest {
        private String username;
        private String password;

        // 기본 생성자
        public LoginRequest() {}

        // 생성자
        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
} 