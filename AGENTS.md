# MOGAK_Spring Agent Guide

## 목적
- 이 저장소에서 Codex와 서브에이전트가 따라야 할 공개 운영 규칙을 정의한다.
- 서비스 정책, 비즈니스 정책, 운영 판단 기준의 본문은 저장소에 두지 않는다.
- 저장소 안에는 구현 규약, 워크플로, 검증 절차, 역할 분리 원칙만 둔다.

## 적용 범위
- 이 문서는 저장소 전체에 적용된다.
- 하위에 더 구체적인 `AGENTS.md`가 생기면 해당 범위에서 우선한다.
- 비공개 정책이 필요한 작업은 로컬 비공개 정책 문서를 consult해야 하며, 공개 문서만으로 판단이 충분하지 않으면 사용자 확인이 필요하다.

## 우선 참조 문서
- 공개 운영 문서 인덱스: `docs/README.md`
- Claude 호환 진입점: `CLAUDE.md`
- 구현 규약 정본: `docs/operations/policies/PROJECT_CONVENTIONS.md`
- 외부 리뷰 규칙: `docs/operations/policies/EXTERNAL_REVIEW_WORKFLOW.md`
- 도메인 문서 지도: `docs/operations/policies/DOMAIN_DOCUMENT_REGISTRY.md`
- Codex 실행 문서: `.codex/rules/*`, `.codex/subagents/*`, `.codex/workflows/*`, `.codex/skills/README.md`
- Claude 실행 자산: `.claude/agents/*`

## 현재 상태와 목표 상태
- 현재 상태:
  - Java 25
  - Spring Boot 3.5.x
  - Gradle 9.1.0
  - 단일 모듈 Gradle 프로젝트
  - JPA 기반
  - MySQL / H2 흔적 존재
- 목표 상태:
  - Java 25
  - Spring Boot 4.x
  - PostgreSQL
  - Flyway 기반 스키마 관리
  - Jakarta 네임스페이스 기준
- 새 코드와 리팩터링은 목표 상태 방향을 우선한다.
- 대규모 스택 업그레이드는 `Spring Boot 2.7.x -> 3.5.x -> 4.x`의 단계형 마이그레이션을 기본 경로로 본다.
- 현재 기준선은 `Java 25 + Spring Boot 3.5.x + Gradle 9.1.0`이며, 다음 단계는 `Spring Boot 4.x`다.
- Java 25 기준선에서 `jdeps` 잔여는 허용 리스크로 기록하고, Stage 3에서 다시 정리한다.
- 단, 이번 저장소 셋업은 실제 마이그레이션을 수행하지 않는다.

## Java / Spring 핵심 원칙
- 생성자 주입만 사용한다.
- Controller는 HTTP 입출력, 인증 컨텍스트, 응답 조립만 담당한다.
- Service는 유스케이스 조합과 비즈니스 흐름을 담당한다.
- Repository는 조회와 저장만 담당하고 정책 판단을 넣지 않는다.
- API 경계에서는 DTO를 우선하지만, 서비스 내부 반환 타입은 유스케이스 복잡도와 경계 성격에 맞게 선택한다.
- 트랜잭션 경계는 서비스 계층에서 명시하되, 조회라고 항상 트랜잭션을 여는 방식은 지양한다.
- 서비스 간 순환참조와 엔티티 직렬화 순환참조를 항상 경계한다.
- 목록/상세/배치 조회에서는 N+1 위험이 없는지 점검한다.
- 작은 국소 수정이 가능하면 새 추상화보다 국소 수정을 우선한다.
- 현재 코드베이스의 레거시 패턴은 관찰 대상이지 신규 기준이 아니다.

## 기본 검증 명령
- 현재 기본 검증: `sh gradlew test`
- `gradlew` 실행 비트를 복구한 뒤에는 `./gradlew test`를 기본 검증으로 사용한다.
- 확장 검증: `sh gradlew clean test`
- Gradle/JDK/컴파일 환경 문제로 테스트가 시작되기 전에 막히면 그 원인을 검증 결과에 함께 보고한다.
- 문서/운영체계 변경이라도 가능한 범위에서 기본 검증을 시도한다.

## 작업 분류
- 전체 흐름:
  - 새 기능
  - 구조 변경
  - 인증/보안 변경
  - DB 영향 변경
  - 여러 production 파일에 걸친 비국소 변경
- 버그 수정 흐름:
  - 재현 가능한 단일 버그
  - 범위가 작은 단일 책임 수정
- 리팩터링 흐름:
  - 구조 정리
  - 중복 제거
  - 패키지/클래스 책임 정돈

## 생략 불가 단계
- 구현 전에 최소한 재현 케이스 또는 실패 테스트를 정의한다.
- 구현 후 리뷰 단계를 거친다.
- 완료 전에 검증 결과를 확인한다.
- 공개 규칙만으로 판단할 수 없는 정책성 변경은 사용자 확인 없이는 완료 처리하지 않는다.

## 서브에이전트 운영
- 역할 분리 원칙과 primary Codex role 매핑은 `.codex/subagents/README.md`를 따른다.
- Claude Code 사용자는 실제 실행 가능한 서브에이전트를 `.claude/agents/*`에서 우선 사용한다.
- 병렬화는 책임 범위가 겹치지 않을 때만 사용한다.
- 역할 문서에 없는 임의의 전용 역할을 만들기보다 기존 역할 문서를 우선 재사용한다.

## 비공개 정책 경계
- 저장소에는 비공개 정책의 제목, 내용, 예시 규칙, 판단 기준을 쓰지 않는다.
- 공개 문서에는 `private-policy: yes/no` 수준의 consult 필요 여부만 기록한다.
- 비공개 정책에 접근할 수 없고 그 정책이 작업 결과에 영향을 줄 수 있으면 작업을 중단하고 사용자에게 확인한다.
