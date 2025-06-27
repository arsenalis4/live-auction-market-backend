package com.example.demo.controller;

import com.example.demo.dto.UserRequestDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 사용자 컨트롤러 클래스
 * NestJS의 @Controller() 데코레이터가 붙은 클래스와 동일한 역할
 * HTTP 요청을 받아서 적절한 서비스 메서드를 호출하고 응답을 반환
 */
@RestController // NestJS의 @Controller()와 같음
@RequestMapping("/api/users") // NestJS의 @Controller('api/users')와 같음
@Tag(name = "사용자 관리", description = "사용자 관련 API") // NestJS Swagger의 @ApiTags()와 같음
public class UserController {

    private final UserService userService;

    // 생성자 주입 (NestJS의 constructor injection과 같음)
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 사용자 생성
     * NestJS: @Post() create(@Body() createUserDto: CreateUserDto)와 같음
     */
    @PostMapping
    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "사용자 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "409", description = "중복된 사용자명 또는 이메일")
    })
    public ResponseEntity<UserResponseDto> createUser(
            @Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto createdUser = userService.createUser(userRequestDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * 모든 사용자 조회
     * NestJS: @Get() findAll()와 같음
     */
    @GetMapping
    @Operation(summary = "모든 사용자 조회", description = "등록된 모든 사용자 목록을 조회합니다")
    @PreAuthorize("hasRole('ADMIN')") // NestJS의 @UseGuards(RolesGuard)와 비슷
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * 페이지네이션을 사용한 사용자 조회
     * NestJS: @Get() findAll(@Query() paginationDto: PaginationDto)와 비슷
     */
    @GetMapping("/paginated")
    @Operation(summary = "페이지네이션 사용자 조회", description = "페이지네이션을 사용하여 사용자 목록을 조회합니다")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDto>> getUsersPaginated(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 필드") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "정렬 방향") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<UserResponseDto> users = userService.getUsersPaginated(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * ID로 사용자 조회
     * NestJS: @Get(':id') findOne(@Param('id') id: string)와 같음
     */
    @GetMapping("/{id}")
    @Operation(summary = "사용자 상세 조회", description = "ID를 사용하여 특정 사용자 정보를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public ResponseEntity<UserResponseDto> getUserById(
            @Parameter(description = "사용자 ID") @PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 사용자명으로 사용자 조회
     * NestJS: @Get('username/:username') findByUsername(@Param('username') username: string)와 같음
     */
    @GetMapping("/username/{username}")
    @Operation(summary = "사용자명으로 조회", description = "사용자명을 사용하여 사용자 정보를 조회합니다")
    public ResponseEntity<UserResponseDto> getUserByUsername(
            @Parameter(description = "사용자명") @PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 사용자 정보 수정
     * NestJS: @Put(':id') update(@Param('id') id: string, @Body() updateUserDto: UpdateUserDto)와 같음
     */
    @PutMapping("/{id}")
    @Operation(summary = "사용자 정보 수정", description = "사용자 정보를 수정합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 수정 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @PreAuthorize("hasRole('ADMIN') or authentication.name == @userService.getUserById(#id).orElse(null)?.username")
    public ResponseEntity<UserResponseDto> updateUser(
            @Parameter(description = "사용자 ID") @PathVariable Long id,
            @Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto updatedUser = userService.updateUser(id, userRequestDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 사용자 삭제
     * NestJS: @Delete(':id') remove(@Param('id') id: string)와 같음
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "사용자 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "사용자 ID") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 사용자명으로 검색
     * NestJS: @Get('search') search(@Query('username') username: string)와 같음
     */
    @GetMapping("/search")
    @Operation(summary = "사용자명 검색", description = "사용자명으로 사용자를 검색합니다")
    public ResponseEntity<List<UserResponseDto>> searchUsers(
            @Parameter(description = "검색할 사용자명") @RequestParam String username) {
        List<UserResponseDto> users = userService.searchUsersByUsername(username);
        return ResponseEntity.ok(users);
    }

    /**
     * 역할별 사용자 조회
     * NestJS: @Get('role/:role') findByRole(@Param('role') role: UserRole)와 같음
     */
    @GetMapping("/role/{role}")
    @Operation(summary = "역할별 사용자 조회", description = "특정 역할을 가진 사용자들을 조회합니다")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getUsersByRole(
            @Parameter(description = "사용자 역할") @PathVariable User.Role role) {
        List<UserResponseDto> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    /**
     * 현재 로그인한 사용자 정보 조회
     * NestJS: @Get('me') getProfile(@Request() req)와 같음
     */
    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> getCurrentUser(java.security.Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        return userService.getUserByUsername(principal.getName())
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }
} 