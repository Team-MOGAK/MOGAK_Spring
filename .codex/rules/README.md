# Codex Rules

이 디렉터리는 구현 중 바로 참고할 실행 규칙을 모은다.

## Rule Set
- `spring-core.md`: 계층 책임과 Spring 안티패턴 방지
- `testing.md`: 테스트 우선 원칙과 테스트 층 분리
- `persistence.md`: JPA 운영과 향후 PostgreSQL / Flyway 방향

## Usage
- 코드 변경 전에는 `spring-core.md`를 먼저 본다.
- 테스트를 추가하거나 수정할 때는 `testing.md`를 본다.
- 영속성, 쿼리, 스키마 영향이 있으면 `persistence.md`를 본다.
