# Testing Rules

## Default Principle
- TDD-first를 기본값으로 둔다.
- 가능하면 실패 테스트 또는 재현 케이스를 먼저 만든다.

## Test Types
- Service:
  - 가능한 한 경량 단위 테스트 우선
- Controller:
  - `@WebMvcTest` 중심
- Repository:
  - JPA 슬라이스 테스트 우선
- Integration:
  - 인증, 트랜잭션, 스케줄링, 다계층 흐름 검증에 사용

## Guidance
- 테스트는 변경 위험에 맞춰 가장 작은 층에서 시작한다.
- 중요한 흐름은 단위 테스트만으로 끝내지 않는다.
- 기존 테스트 스타일이 섞여 있어도 새 테스트는 목표 구조를 따른다.

## Minimum Done
- 새 기능: 핵심 성공 시나리오 + 최소 1개 실패 시나리오
- 버그 수정: 재현 케이스
- 리팩터링: 안전망 테스트
