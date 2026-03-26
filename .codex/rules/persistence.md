# Persistence Rules

## Current State
- 현재 저장소는 Spring Data JPA 기반 단일 모듈 구조다.
- MySQL / H2 흔적이 존재한다.

## Target Direction
- PostgreSQL 중심
- Flyway 기반 스키마 관리
- JPA 계층 책임 명확화

## Rules
- Repository는 데이터 접근 책임만 가진다.
- 쿼리 최적화는 데이터 접근 차원에서만 수행한다.
- 정책 판단은 Service 또는 Entity에 둔다.
- 스키마 영향 변경은 중요 변경으로 간주한다.
- Flyway는 아직 도입 전이지만, 새 문서와 설계는 Flyway 도입을 방해하지 않게 작성한다.

## Notes
- 이번 셋업은 persistence 운영 원칙만 정의하며 실제 DB 마이그레이션을 수행하지 않는다.
