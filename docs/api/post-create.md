# Post Create API

이 문서는 회고 생성 API의 이미지 업로드 계약을 정리한다.

## Endpoint

`POST /api/jogaks/{jogakId}/posts`

요청은 `multipart/form-data`를 사용한다.

## Request Parts

| Part | Required | Meaning |
| --- | --- | --- |
| `request` | yes | 회고 생성 JSON |
| `multipartFile` | no | 회고 이미지 목록 |

`request` 예시:

```json
{
  "targetDate": "2026-04-28",
  "contents": "오늘 회고"
}
```

이미지 파트 처리:
- `multipartFile` part가 없어도 회고 생성은 성공한다.
- 빈 파일 part만 전달되면 이미지 없음으로 처리한다.
- 이미지가 없으면 storage 업로드를 호출하지 않는다.
- 이미지가 있으면 storage 업로드 후 회고를 생성한다.

## Response Contract

이미지 없는 회고 생성 응답의 `imgUrls`는 빈 배열이다.

```json
{
  "code": "success",
  "result": {
    "id": 1,
    "mogakId": 1,
    "jogakId": 2,
    "dailyJogakId": 3,
    "targetDate": "2026-04-28",
    "userId": 7,
    "contents": "오늘 회고",
    "imgUrls": [],
    "createdAt": "2026-04-28T12:00:00"
  }
}
```

목록 응답에서 이미지 없는 회고의 `thumbnailUrl`은 `null`이다.

## Storage Disabled

`feature.storage.enabled=false` 상태에서 실제 이미지가 포함된 요청은 실패한다.

| HTTP | Code | Meaning |
| --- | --- | --- |
| `503` | `Z006` | 현재 서버 환경에서 storage 기능이 비활성화됨 |

`503 Z006`은 모바일 클라이언트가 자동 재시도할 일반 장애가 아니라, 현재 배포 환경에서 이미지 업로드/삭제 기능이 비활성화된 상태를 의미한다.

클라이언트 처리 기준:
- 이미지 없는 회고 생성 흐름은 계속 제공한다.
- 이미지 업로드 UI는 비활성화하거나 이미지 없이 생성하도록 유도한다.
- `Z006` 응답은 자동 재시도하지 않는다.

storage가 활성화되면 같은 API에서 이미지 포함 요청이 기존 업로드 흐름으로 처리된다.
