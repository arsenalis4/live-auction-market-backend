package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 리포지토리 인터페이스
 * NestJS의 TypeORM Repository와 동일한 역할
 * JpaRepository를 상속받아 기본 CRUD 메서드들을 자동으로 제공받음
 */
@Repository // NestJS의 @Injectable()과 비슷한 의존성 주입 표시
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 사용자명으로 사용자 찾기
     * NestJS: findOne({ where: { username } })와 같음
     */
    Optional<User> findByUsername(String username);

    /**
     * 이메일로 사용자 찾기
     * NestJS: findOne({ where: { email } })와 같음
     */
    Optional<User> findByEmail(String email);

    /**
     * 사용자명 또는 이메일로 사용자 찾기
     * NestJS: findOne({ where: [{ username }, { email }] })와 같음
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * 사용자명이 존재하는지 확인
     * NestJS: count({ where: { username } }) > 0과 같음
     */
    boolean existsByUsername(String username);

    /**
     * 이메일이 존재하는지 확인
     * NestJS: count({ where: { email } }) > 0과 같음
     */
    boolean existsByEmail(String email);

    /**
     * 역할별 사용자 목록 조회
     * NestJS: find({ where: { role } })와 같음
     */
    List<User> findByRole(User.Role role);

    /**
     * 커스텀 쿼리 예제 - 사용자명으로 검색 (LIKE 사용)
     * NestJS: createQueryBuilder().where("username LIKE :username")와 비슷
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:username%")
    List<User> findByUsernameContaining(@Param("username") String username);

    /**
     * 네이티브 SQL 쿼리 예제
     * NestJS: query("SELECT * FROM users WHERE email = ?")와 비슷
     */
    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    Optional<User> findByEmailNative(@Param("email") String email);
} 