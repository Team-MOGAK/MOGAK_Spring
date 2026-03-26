# Workflows Guide

## Workflow Selection
- `full-delivery.md`
  - 새 기능
  - 구조 변경
  - 인증/보안 변경
  - DB 영향 변경
- `bugfix-fastlane.md`
  - 재현 가능한 단일 버그
  - 범위 작은 국소 수정
  - 원인 분석이 필요한 수정
- `refactor-guarded.md`
  - 구조 정리
  - 중복 제거
  - 책임 정돈

## Shared Rules
- 구현 전 재현 케이스 또는 실패 테스트를 먼저 확보한다.
- 구현 후 리뷰와 검증을 생략하지 않는다.
- 공개 규칙만으로 닫을 수 없는 정책 문제는 사용자 확인이 필요하다.
