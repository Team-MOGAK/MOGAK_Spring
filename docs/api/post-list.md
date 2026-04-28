# Post List API

이 문서는 게시글 목록 API의 응답 계약을 정리한다.

## Endpoints

- `GET /api/mogaks/{mogakId}/posts`
- `GET /api/posts`

## Response

목록 응답은 Spring Data `Slice` 직렬화 구조를 직접 노출하지 않는다.

```json
{
  "time": "2026-04-28 12:00:00",
  "status": "OK",
  "code": "SU",
  "message": "성공",
  "result": {
    "items": [],
    "page": 0,
    "size": 10,
    "hasNext": true
  }
}
```

`result` 필드:

| Field | Meaning |
| --- | --- |
| `items` | 현재 페이지의 게시글 응답 DTO 목록 |
| `page` | 요청 page 값 |
| `size` | 요청 size 값 |
| `hasNext` | 다음 page 존재 여부 |

## Contract Notes

- `result.content`, `result.number`, `result.numberOfElements`, `result.first`, `result.last`는 응답 계약이 아니다.
- `page`는 0부터 시작한다.
- `GET /api/posts`의 `address`가 생략되면 서버는 기존 동작대로 인증 사용자의 거주지를 사용한다.
