# JWT 인증 & 세션 관리 프로젝트

이 저장소는 Spring Boot, Spring Security, Redis, JWT를 활용한 토큰 기반 인증 시스템 구현 예제 프로젝트입니다. Redis를 활용하여 Refresh Token을 관리하고, JWT Access Token을 통해 보안 엔드포인트를 보호합니다.

---

## 📝 목차

1. [프로젝트 개요](#프로젝트-개요)
2. [기술 스택](#기술-스택)
3. [주요 기능](#주요-기능)
4. [디렉터리 구조](#디렉터리-구조)
5. [사전 준비](#사전-준비)
6. [환경 변수 설정](#환경-변수-설정)
7. [실행 방법](#실행-방법)
8. [API 문서](#api-문서)
9. [테스트](#테스트)
10. [라이센스](#라이센스)

---

## 🎯 프로젝트 개요

이 프로젝트는 JWT(JSON Web Token)와 Redis를 활용한 세션 관리 및 인증 시스템의 구현 예제입니다.
Access Token과 Refresh Token을 활용한 인증 방식을 구현하여 보안성을 높이고, Redis를 통해 효율적인 토큰 관리를 제공합니다.

주요 구현 포인트:
- Access Token / Refresh Token 매커니즘
- Redis를 활용한 토큰 저장 및 검증
- Spring Security 기반 보안 설정
- 단위 테스트 및 통합 테스트

---

## 💻 기술 스택

* **Backend**: Java, Spring Boot, Spring Security, Spring Data JPA
* **Security**: JWT (io.jsonwebtoken)
* **Cache**: Redis
* **Database**: H2 (개발 및 테스트용 인메모리 DB)
* **Test**: JUnit 5, Mockito
* **Documentation**: Swagger (OpenAPI 3.0)

---

## 🚀 주요 기능

* 🔐 **회원 인증**
    * 회원가입 (이메일, 비밀번호)
    * 로그인 (JWT 토큰 발급)
    * 로그아웃 (Redis에서 Refresh Token 제거)

* 🔄 **토큰 관리**
    * Access Token 발급 (단기간 유효, 기본 60분)
    * Refresh Token 발급 (장기간 유효, 7일)
    * Token 갱신 API (만료된 Access Token 재발급)
    * JWT 기반 인증 필터 구현

* 👤 **사용자 정보**
    * 현재 인증된 사용자 정보 조회 API

* 🛡️ **보안 설정**
    * CORS 설정
    * Spring Security 기반 엔드포인트 보호
    * 예외 처리 (중복 이메일, 토큰 만료 등)

---

## 📂 디렉터리 구조

```
src/main/java/com/example/jwt/
├── common/                  # 공통 컴포넌트
│   ├── ApiResponse.java     # API 응답 표준화
│   └── exception/           # 예외 처리
├── config/                  # 설정 클래스
│   ├── auth/                # 인증 관련 설정
│   ├── CustomUserDetails.java
│   ├── JwtAuthenticationFilter.java
│   ├── RedisConfig.java     # Redis 설정
│   └── SecurityConfig.java  # 보안 설정
├── controller/              # API 컨트롤러
│   ├── AuthController.java  # 인증 API
│   └── UserController.java  # 사용자 API
├── domain/                  # 도메인 모델
│   ├── auth/                # 인증 관련 도메인
│   │   ├── dto/             # 데이터 전송 객체
│   │   └── service/         # 인증 서비스
│   └── user/                # 사용자 관련 도메인
│       ├── dto/             # 사용자 DTO
│       ├── entity/          # 사용자 엔티티
│       └── repository/      # 사용자 저장소
└── JwtApplication.java      # 애플리케이션 진입점

src/test/                    # 테스트 코드
```

---

## 🔧 사전 준비

1. JDK 17 이상
2. Redis 서버 실행 (기본 `localhost:6379`)
3. Maven 또는 Gradle (프로젝트 빌드 및 의존성 관리)

---

## 🛠 환경 변수 설정

### JWT 시크릿 키 생성 가이드

JWT 시크릿 키는 토큰 서명에 사용되며, 충분한 길이와 복잡성을 가져야 합니다:

- **최소 길이**:
    - HS256 알고리즘: 최소 32바이트(256비트)
    - HS384 알고리즘: 최소 48바이트(384비트)
    - HS512 알고리즘: 최소 64바이트(512비트)

- **시크릿 키 생성 방법**:
  ```bash
  # 랜덤 시크릿 키 생성 후 Base64 인코딩
  openssl rand -base64 32  # HS256용 (32바이트)
  openssl rand -base64 64  # HS512용 (64바이트)
  ```

- **주의사항**:
    - 실제 애플리케이션에서는 환경 변수나 시스템 프로퍼티로 시크릿 키를 관리하세요
    - 소스 코드에 하드코딩하지 마세요
    - 시크릿 키가 충분히 길지 않으면 JWT 라이브러리에서 오류가 발생할 수 있습니다

### application.yml 주요 설정

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  
  redis:
    host: localhost
    port: 6379
  
  h2:
    console:
      enabled: true
      path: /h2-console

jwt:
  secret: "YOUR_SECRET_KEY"  # Base64 인코딩된 비밀키 (최소 256비트/32바이트 이상)
  expiration: 60  # Access Token 만료 시간(분)
```

---

## ▶️ 실행 방법

### 프로젝트 클론

```bash
git clone https://github.com/yourusername/jwt-auth-project.git
cd jwt-auth-project
```

### 애플리케이션 실행

```bash
# Maven
./mvnw spring-boot:run

# Gradle
./gradlew bootRun
```

* 서버 포트: `8080`
* H2 콘솔: `http://localhost:8080/h2-console`
* Swagger UI: `http://localhost:8080/swagger-ui/index.html`

---

## 📚 API 문서

### 인증 API

* **회원가입**: `POST /api/auth/signup`
  ```json
  {
    "email": "user@example.com",
    "password": "password123"
  }
  ```

* **로그인**: `POST /api/auth/login`
  ```json
  {
    "email": "user@example.com",
    "password": "password123"
  }
  ```
  응답:
  ```json
  {
    "code": "SUCCESS",
    "message": "OK",
    "data": {
      "accessToken": "eyJhbG...",
      "refreshToken": "eyJhbG...",
      "expiresIn": 3600
    }
  }
  ```

* **토큰 갱신**: `POST /api/auth/refresh`
  ```json
  {
    "refreshToken": "eyJhbG..."
  }
  ```

* **로그아웃**: `POST /api/auth/logout?email=user@example.com`

### 사용자 API

* **내 정보 조회**: `GET /api/users/me`
    * 헤더: `Authorization: Bearer {accessToken}`

---

## ✅ 테스트

```bash
# 모든 테스트 실행
./mvnw test

# 특정 테스트 클래스 실행
./mvnw test -Dtest=JwtTokenProviderTest
```

### 테스트 코드 커버리지

프로젝트에는 주요 컴포넌트에 대한 단위 테스트가 포함되어 있습니다:
- JWT 토큰 관련 테스트
- 인증 서비스 테스트
- 컨트롤러 테스트

---

## 🔒 보안 아키텍처

### JWT 인증 흐름

1. 클라이언트가 로그인 요청
2. 서버는 인증 후 Access Token과 Refresh Token 발급
3. Access Token은 짧은 수명(60분), Refresh Token은 긴 수명(7일)
4. Access Token이 만료되면 Refresh Token으로 새 토큰 발급
5. Refresh Token은 Redis에 저장되어 관리됨
6. 로그아웃 시 Redis에서 Refresh Token 제거

### 보안 설정

- 모든 인증 요청은 `/api/auth/**` 경로를 통해 접근 가능
- 그 외 엔드포인트는 유효한 JWT 토큰이 필요
- JWT 필터를 통한 요청 인증 검증
- CORS 설정으로 허용된 오리진만 접근 가능

---

## 📄 라이센스

MIT © Jisu Bak