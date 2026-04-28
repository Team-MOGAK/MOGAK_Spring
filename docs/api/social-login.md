# Social Login API

이 문서는 소셜 로그인 클라이언트-서버 API 계약을 정리한다.

## Endpoint

`POST /api/auth/{provider}/login`

`provider` 값:
- `apple`
- `google`
- `kakao`

## Request

```json
{
  "token": "provider-token"
}
```

토큰 전달 기준:
- `apple`: 클라이언트가 Apple에서 받은 `id_token`
- `google`: 클라이언트가 Google에서 받은 `id_token`
- `kakao`: 클라이언트가 Kakao에서 받은 `access_token`

## Response

```json
{
  "time": "2026-04-27 12:00:00",
  "status": "OK",
  "code": "SU",
  "message": "성공",
  "result": {
    "isRegistered": false,
    "userId": 10,
    "tokens": {
      "accessToken": "access-token",
      "refreshToken": "refresh-token"
    }
  }
}
```

JWT 계약:
- access token `sub`: `userId`
- access token `id`: `userId`
- access token `email`: 이메일이 있는 계정에서만 포함
- refresh token `sub`: `userId`

## Account Rules

- 이미 연결된 소셜 계정은 `provider + providerUserId`로 사용자를 찾는다.
- Apple/Google 신규 가입은 검증된 이메일이 필요하다.
- Kakao 신규 가입은 이메일 없이도 가능하다.
- 기존 이메일 계정이 있으면 자동 연결하지 않고 별도 계정 연결 절차를 요구한다.

## Error Codes

| Code | HTTP | Meaning |
| --- | --- | --- |
| `U008` | 400 | 지원하지 않는 소셜 로그인 공급자 |
| `U009` | 400 | 소셜 로그인 토큰 오류 |
| `U010` | 400 | 소셜 로그인 이메일 정보 필요 |
| `U011` | 409 | 이미 연결된 소셜 계정 |
| `U012` | 409 | 기존 계정에 소셜 계정 연결 필요 |
| `U013` | 400 | 소셜 로그인 이메일 검증 필요 |
