package com.example.demo.service;

import com.example.demo.dto.UserRequestDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 사용자 서비스 클래스
 * NestJS의 @Injectable() Service와 동일한 역할
 * 비즈니스 로직을 처리하고 Repository와 Controller 사이의 중간 계층 역할
 */
@Service // NestJS의 @Injectable()과 같음
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 생성자 주입 (NestJS의 constructor injection과 같음)
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Spring Security UserDetailsService 구현
     * NestJS의 Passport Strategy에서 validateUser와 비슷한 역할
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }

    /**
     * 사용자 생성
     * NestJS의 create 메서드와 같음
     */
    @Transactional // 쓰기 작업이므로 트랜잭션 필요
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        // 중복 검사
        if (userRepository.existsByUsername(userRequestDto.getUsername())) {
            throw new RuntimeException("이미 존재하는 사용자명입니다: " + userRequestDto.getUsername());
        }
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다: " + userRequestDto.getEmail());
        }

        // DTO를 Entity로 변환하고 비밀번호 암호화
        User user = userRequestDto.toEntity();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 저장하고 DTO로 변환하여 반환
        User savedUser = userRepository.save(user);
        return UserResponseDto.fromEntity(savedUser);
    }

    /**
     * 모든 사용자 조회
     * NestJS의 findAll과 같음
     */
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 페이지네이션을 사용한 사용자 조회
     * NestJS의 find with pagination과 비슷
     */
    public Page<UserResponseDto> getUsersPaginated(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserResponseDto::fromEntity);
    }

    /**
     * ID로 사용자 조회
     * NestJS의 findOne과 같음
     */
    public Optional<UserResponseDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserResponseDto::fromEntity);
    }

    /**
     * 사용자명으로 사용자 조회
     * NestJS의 findOne with conditions와 같음
     */
    public Optional<UserResponseDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserResponseDto::fromEntity);
    }

    /**
     * 사용자 정보 수정
     * NestJS의 update 메서드와 같음
     */
    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + id));

        // 기존 사용자가 아닌 다른 사용자가 같은 사용자명이나 이메일을 사용하는지 확인
        if (!user.getUsername().equals(userRequestDto.getUsername()) && 
            userRepository.existsByUsername(userRequestDto.getUsername())) {
            throw new RuntimeException("이미 존재하는 사용자명입니다: " + userRequestDto.getUsername());
        }
        if (!user.getEmail().equals(userRequestDto.getEmail()) && 
            userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다: " + userRequestDto.getEmail());
        }

        // 정보 업데이트
        user.setUsername(userRequestDto.getUsername());
        user.setEmail(userRequestDto.getEmail());
        user.setRole(userRequestDto.getRole());
        
        // 비밀번호가 변경된 경우에만 암호화하여 업데이트
        if (userRequestDto.getPassword() != null && !userRequestDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return UserResponseDto.fromEntity(updatedUser);
    }

    /**
     * 사용자 삭제
     * NestJS의 remove 메서드와 같음
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("사용자를 찾을 수 없습니다: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * 사용자명으로 검색
     * NestJS의 search functionality와 비슷
     */
    public List<UserResponseDto> searchUsersByUsername(String username) {
        return userRepository.findByUsernameContaining(username)
                .stream()
                .map(UserResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 역할별 사용자 조회
     * NestJS의 find with specific conditions와 같음
     */
    public List<UserResponseDto> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role)
                .stream()
                .map(UserResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
} 