# 🚀 Spring Boot + JPA + Security + WebSocket + MySQL Demo

NestJS 개발자를 위한 Spring Boot 완전 예제 프로젝트입니다.

## 📚 기술 스택

| 기술 | Spring Boot | NestJS 비교 |
|------|-------------|-------------|
| **웹 프레임워크** | Spring Boot Web | @nestjs/core |
| **ORM** | Spring Data JPA + Hibernate | TypeORM |
| **인증/인가** | Spring Security | Passport + Guards |
| **WebSocket** | STOMP over WebSocket | @nestjs/websockets |
| **API 문서화** | Swagger 3 (OpenAPI) | @nestjs/swagger |
| **데이터베이스** | MySQL | MySQL/PostgreSQL |
| **유효성 검증** | Bean Validation | class-validator |
| **예외 처리** | @ControllerAdvice | Exception Filters |

## 🏗️ 프로젝트 구조

```
src/main/java/com/example/demo/
├── config/                 # 설정 클래스들 (NestJS의 모듈 설정과 비슷)
│   ├── SecurityConfig.java         # Spring Security 설정
│   ├── WebSocketConfig.java        # WebSocket STOMP 설정
│   ├── SwaggerConfig.java          # API 문서화 설정
│   └── CustomAuthenticationEntryPoint.java
├── controller/             # REST API 컨트롤러 (NestJS Controller와 동일)
│   ├── UserController.java         # 사용자 관리 API
│   ├── AuthController.java         # 인증 관련 API
│   └── ChatController.java         # WebSocket 채팅
├── dto/                    # Data Transfer Objects (NestJS DTO와 동일)
│   ├── UserRequestDto.java
│   └── UserResponseDto.java
├── entity/                 # JPA 엔티티 (NestJS Entity와 동일)
│   └── User.java
├── repository/             # 데이터 접근 계층 (NestJS Repository와 동일)
│   └── UserRepository.java
├── service/                # 비즈니스 로직 (NestJS Service와 동일)
│   └── UserService.java
├── exception/              # 예외 처리 (NestJS Exception Filter와 비슷)
│   └── GlobalExceptionHandler.java
└── DemoApplication.java    # 메인 애플리케이션 클래스
```

## 🔧 설치 및 실행

### 1. 사전 요구사항
- Java 21
- MySQL 8.0+
- Gradle

### 2. MySQL 데이터베이스 설정
```sql
CREATE DATABASE live_auction_demo;
CREATE USER 'root'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON live_auction_demo.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

### 3. 애플리케이션 실행
```bash
# 프로젝트 클론
git clone <repository-url>
cd demo

# 의존성 설치 및 실행 (NestJS의 npm install && npm run start:dev와 비슷)
./gradlew bootRun
```

### 4. 접속 확인
- 애플리케이션: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- 테스트 페이지: http://localhost:8080/test.html

## 📋 주요 API 엔드포인트

### 인증 관련
```http
POST /api/auth/register      # 회원가입 (NestJS: @Post('register'))
POST /api/auth/login         # 로그인
GET  /api/auth/profile       # 현재 사용자 정보
POST /api/auth/logout        # 로그아웃
```

### 사용자 관리
```http
GET    /api/users            # 모든 사용자 조회 (ADMIN만)
GET    /api/users/{id}       # 특정 사용자 조회
GET    /api/users/username/{username}  # 사용자명으로 조회
PUT    /api/users/{id}       # 사용자 정보 수정
DELETE /api/users/{id}       # 사용자 삭제 (ADMIN만)
GET    /api/users/search?username=xxx  # 사용자 검색
GET    /api/users/role/{role}  # 역할별 사용자 조회
```

### WebSocket 채팅
```javascript
// 연결: /ws
// 구독: /topic/public (공개 채팅)
// 메시지 전송: /app/chat.sendMessage
// 사용자 입장: /app/chat.addUser
```

## 🔒 인증 방식

현재는 **HTTP Basic Authentication**을 사용합니다.

### API 호출 예제
```javascript
// NestJS에서 JWT 사용하는 것과 비슷하지만, Basic Auth 사용
const response = await fetch('/api/users', {
    headers: {
        'Authorization': 'Basic ' + btoa('username:password')
    }
});
```

## 🎯 NestJS와의 주요 차이점

| 항목 | Spring Boot | NestJS |
|------|-------------|---------|
| **의존성 주입** | `@Autowired` | `constructor injection` |
| **데코레이터** | `@RestController`, `@Service` | `@Controller()`, `@Injectable()` |
| **라우팅** | `@GetMapping`, `@PostMapping` | `@Get()`, `@Post()` |
| **유효성 검증** | `@Valid` + Bean Validation | `ValidationPipe` + class-validator |
| **예외 처리** | `@ControllerAdvice` | `@Catch()` Exception Filter |
| **WebSocket** | STOMP + `@MessageMapping` | `@WebSocketGateway()` + `@SubscribeMessage()` |
| **설정** | `application.properties` | 환경변수 + 모듈 imports |

## 📖 사용 예제

### 1. 회원가입
```http
POST /api/auth/register
Content-Type: application/json

{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123",
    "role": "USER"
}
```

### 2. 사용자 조회 (인증 필요)
```http
GET /api/users/1
Authorization: Basic am9obl9kb2U6cGFzc3dvcmQxMjM=
```

### 3. WebSocket 채팅 (JavaScript)
```javascript
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    // 공개 채팅 구독
    stompClient.subscribe('/topic/public', function(message) {
        console.log(JSON.parse(message.body));
    });
    
    // 메시지 전송
    stompClient.send("/app/chat.sendMessage", {}, JSON.stringify({
        sender: "John",
        content: "Hello World!",
        type: "CHAT"
    }));
});
```

## 🧪 테스트 방법

1. **브라우저에서 테스트**: http://localhost:8080/test.html
2. **Swagger UI 사용**: http://localhost:8080/swagger-ui.html
3. **cURL 사용**:
```bash
# 회원가입
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@test.com","password":"123456","role":"USER"}'

# 사용자 조회 (Basic Auth)
curl -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Basic dGVzdDoxMjM0NTY="
```

## 🔄 데이터베이스 스키마

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

## 🚀 확장 가능한 기능들

- [ ] JWT 토큰 기반 인증 구현
- [ ] Redis를 이용한 세션 관리
- [ ] 파일 업로드 기능
- [ ] 이메일 인증
- [ ] 소셜 로그인 (OAuth2)
- [ ] 실시간 알림 시스템
- [ ] 경매 시스템 구현

## 📝 학습 포인트

NestJS 개발자라면 다음 관점에서 비교해보세요:

1. **의존성 주입**: NestJS의 constructor injection vs Spring의 @Autowired
2. **데코레이터**: NestJS의 @Controller() vs Spring의 @RestController
3. **미들웨어**: NestJS의 Guards vs Spring의 Security Filter
4. **ORM**: TypeORM vs JPA/Hibernate
5. **WebSocket**: @WebSocketGateway vs @MessageMapping

## 🤝 기여하기

이슈나 개선사항이 있으시면 언제든지 PR을 보내주세요!

## 📄 라이선스

MIT License 