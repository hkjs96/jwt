# JWT 인증 & Google OAuth2 예제 프로젝트

이 저장소는 Spring Boot 3.x, Spring Security, Redis, JWT 기반 인증 시스템에 Google OAuth2 로그인을 통합한 예제 프로젝트입니다. 프론트엔드로는 React + Vite + TypeScript를 사용했습니다.

---

## 📝 목차

1. [프로젝트 개요](#프로젝트-개요)
2. [기술 스택](#기술-스택)
3. [주요 기능](#주요-기능)
4. [디렉터리 구조](#디렉터리-구조)
5. [사전 준비](#사전-준비)
6. [환경 변수 설정](#환경-변수-설정)
7. [백엔드 실행](#백엔드-실행)
8. [프론트엔드 실행](#프론트엔드-실행)
9. [테스트](#테스트)
10. [Google OAuth 설정](#google-oauth-설정)
11. [License](#license)

---

## 🎯 프로젝트 개요

* Spring Boot 애플리케이션에 JWT 기반 로그인/회원가입/토큰 갱신/로그아웃 기능 구현
* Redis에 Refresh Token 저장·검증
* Google OAuth2 Authorization Code Grant 흐름 통합
* React + Vite + TypeScript 프론트엔드에서 소셜 로그인 버튼 및 리디렉션 처리

---

## 💻 기술 스택

* **Backend**: Java 17, Spring Boot 3.x, Spring Security, Spring Data JPA, Spring Data Redis, io.jsonwebtoken jjwt
* **DB**: H2 (인메모리, 테스트용)
* **OAuth Client**: Spring Security OAuth2 Client, `RestClient`
* **Frontend**: React 18, Vite, TypeScript, Axios, react-router-dom

---

## 🚀 주요 기능

* 🚪 일반 회원가입(Signup) & 로그인(Login)
* 🔄 Access Token/Refresh Token 발급 및 갱신
* 🛑 로그아웃 시 Refresh Token 삭제
* 🔒 JWT 검증 필터로 보호된 API 엔드포인트 구현
* 🌐 Google OAuth2 로그인 (인가코드 → 구글 API 호출 → JWT 발급)
* ⚙️ CORS, CSRF, Stateless 세션 설정

---

## 📂 디렉터리 구조

```
jwt-backend/
├─ .env                      # 환경변수 (.gitignore 등록)
├─ src/main/resources/
│   └─ application.yml       # Spring 설정 (dotenv import 포함)
├─ src/main/java/com/example/jwt
│   ├ config/
│   ├ common/
│   ├ controller/
│   ├ domain/
│   ├ service/
│   └ JwtApplication.java
└─ src/test/java/...         # 단위/통합 테스트

jwt-frontend/
├─ .env                      # VITE_ 접두사 환경변수
├─ src/
│   ├ api/axios.ts
│   ├ components/
│   ├ pages/
│   ├ App.tsx
│   └ main.tsx
└─ vite.config.ts
```

---

## 🔧 사전 준비

1. JDK 17 설치
2. Node.js (v22+) 설치
3. Redis 서버 실행 (기본 `localhost:6379`)
4. Google API Console에서 OAuth 애플리케이션 등록

---

## 🛠 환경 변수 설정

### 프로젝트 루트에 `.env` 파일 생성

```dotenv
# jwt-backend/.env
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
APP_BASE_URL=http://localhost:8080
DB_URL=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
DB_USER=sa
DB_PASS=
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=<Base64-encoded-secret>
JWT_EXPIRATION=60
```

```dotenv
# jwt-frontend/.env
VITE_GOOGLE_CLIENT_ID=your-google-client-id
VITE_BACKEND_URL=http://localhost:8080
```

---

## ▶️ 백엔드 실행

```bash
cd jwt-backend
./mvnw clean spring-boot:run
```

* 서버 포트: `8080`
* H2 콘솔: `http://localhost:8080/h2-console`

---

## ▶️ 프론트엔드 실행

```bash
cd jwt-frontend
npm install
npm run dev
```

* 개발 서버: `http://localhost:5173`

---

## ✅ 테스트

* 메인 애플리케이션 테스트

  ```bash
  cd jwt-backend
  ./mvnw test
  ```
* 프론트엔드 테스트 (필요 시)

---

## 🔐 Google OAuth 설정

1. Google API Console → OAuth 2.0 클라이언트 ID 생성
2. **Authorized JavaScript origins**: `http://localhost:5173`
3. **Authorized redirect URIs**: `http://localhost:5173/oauth2/redirect`
4. 발급된 **Client ID/Secret** 을 `.env` 에 등록

---

## 📄 License

MIT © Jisu Bak
