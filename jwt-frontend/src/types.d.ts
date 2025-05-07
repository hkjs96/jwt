export interface ApiResponse<T> {
    /** 응답 코드 (SUCCESS or ERROR) */
    code: 'SUCCESS' | 'ERROR';
    /** 응답 메시지 */
    message: string;
    /** 실제 데이터 */
    data: T;
}
  
export interface SignupRequest {
    /** 사용자 이메일 */
    email: string;
    /** 사용자 비밀번호 */
    password: string;
}
  
export interface LoginRequest {
    /** 사용자 이메일 */
    email: string;
    /** 사용자 비밀번호 */
    password: string;
}
  
export interface RefreshRequest {
    /** Refresh Token */
    refreshToken: string;
}
  
export interface TokenResponse {
    /** 발급된 Access Token */
    accessToken: string;
    /** 발급된 Refresh Token */
    refreshToken: string;
    /** Access Token 만료까지 남은 시간(초) */
    expiresIn: number;
}
  
export interface UserResponse {
    /** 사용자 이메일 */
    email: string;
    /** 사용자 권한 목록 */
    roles: string[];
}
  
export interface ErrorResponse {
    /** 에러 코드 */
    code: string;
    /** 에러 메시지 */
    message: string;
}