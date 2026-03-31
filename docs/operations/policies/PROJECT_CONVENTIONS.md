# Project Conventions

이 문서는 `MOGAK_Spring`의 공개 구현 규약 정본이다.

## Stack Status
- 현재:
  - Java 25
  - Spring Boot 4.0.2
  - Gradle 9.1.0
  - 단일 모듈 Gradle
  - Spring Data JPA
  - MySQL / H2 흔적
- 목표:
  - Java 25
  - Spring Boot 4.x 유지
  - PostgreSQL
  - Flyway
  - Jakarta 네임스페이스
- 대규모 스택 업그레이드는 `Spring Boot 2.7.x -> 3.5.x -> 4.x`의 단계형 마이그레이션을 기본 경로로 본다.
- 현재 기준선은 `Java 25 + Spring Boot 4.0.2 + Gradle 9.1.0`이다.
- Java 25 기준선에서 `jdeps` 잔여는 허용 리스크로 기록하고, Boot 4 기준으로 다시 점검한다.
- storage 기능은 현재 비활성 기본값(`feature.storage.enabled=false`)을 사용하며, 이미지 업로드/삭제 요청은 `503 fail-fast`로 처리한다.

## Implementation Rules
- 생성자 주입만 사용한다. 필드 주입과 setter 주입은 금지한다.
- Controller는 요청 검증, 인증 컨텍스트 해석, 응답 반환만 담당한다.
- Service는 유스케이스 중심으로 작성한다.
- Repository는 데이터 접근만 담당한다.
- DTO는 API 경계 타입이고 Entity는 영속 모델이다. API 계약 제어가 필요할 때는 DTO를 우선한다.
- Converter/Mapper는 표현 변환만 담당한다.
- 비즈니스 규칙과 상태 전이는 Service 또는 Entity 메서드에 둔다.
- 새 코드에서는 무분별한 static util 추가를 피한다.
- 비국소적 공통화보다 국소 수정과 명시적 코드가 우선이다.

## Transaction Rules
- Service는 읽기와 쓰기의 트랜잭션 의도를 분명히 드러낸다.
- 쓰기 메서드는 `@Transactional`로 명시한다.
- 트랜잭션은 Controller나 Repository에서 시작하지 않는다.
- 조회라고 해서 항상 `@Transactional(readOnly = true)`를 붙이지 않는다.
- 단건 조회, 단순 목록 조회, 짧은 조회 API는 기본적으로 non-transactional을 우선 검토한다.
- 조회 전용 라우팅이나 기존 트랜잭션 합류가 필요하면 `@Transactional(readOnly = true, propagation = SUPPORTS)`를 고려한다.
- 긴 복합 조회, 통계/정산성 조회, 중간 데이터 변경이 치명적인 조회는 `@Transactional(readOnly = true)`를 사용한다.
- `readOnly = true`는 관성적으로 붙이지 말고, 정합성 요구와 인프라 이점을 기준으로 선택한다.

### Read Transaction Decision Guide
- 단건 조회:
  - 기본은 non-transactional
  - ID 조회, 단순 존재 여부 확인, 짧은 참조성 조회에 적합
- 단순 목록/페이징 조회:
  - 기본은 non-transactional
  - 미세한 조회 시점 차이보다 응답 속도와 DB 부하 절감이 더 중요할 때 적합
- 조회 전용 DB 라우팅 또는 상위 트랜잭션 합류가 필요한 조회:
  - `@Transactional(readOnly = true, propagation = SUPPORTS)` 고려
  - 트랜잭션이 없을 때는 물리 트랜잭션 생성을 피하고, 있을 때는 안전하게 합류하는 용도
- 긴 복합 조회 및 정합성 민감 조회:
  - `@Transactional(readOnly = true)` 사용
  - 조인/집계/정산/리포트처럼 중간 변경이 결과를 왜곡할 수 있는 경우에 적합

## API and Error Rules
- Request DTO와 Response DTO를 분리한다.
- 검증 가능한 입력은 DTO에서 우선 검증한다.
- 예외 체계는 도메인/애플리케이션 의미가 드러나게 유지한다.
- 새 공개 API, 인증/인가 변경, 응답 계약 변경은 중요 변경으로 본다.
- 공개 규칙만으로 정할 수 없는 정책성 응답 기준은 비공개 정책 consult 또는 사용자 확인이 필요하다.

## Service Return Patterns
- Service가 항상 Response DTO를 반환해야 하는 것은 아니다.
- Service는 유스케이스에 따라 Entity, 도메인 객체, projection, 전용 반환 객체를 반환할 수 있다.
- 단순 내부 조회나 상위 서비스 조합에서는 Entity 또는 도메인 객체 반환을 허용한다.
- 외부 API 계약을 명확히 통제해야 하거나, 응답 조합/가공/정책 노출 통제가 필요하면 DTO를 반환한다.
- Controller는 Service에 맞추기 위해 Repository나 영속성 세부사항에 의존하지 않는다.
- API 응답 직렬화에 Entity를 그대로 노출할지는 lazy loading, 연관관계, 응답 안정성, 정책 노출 위험을 기준으로 신중하게 판단한다.

## JPA and Persistence Rules
- Repository는 조회/저장 책임만 가진다.
- 쿼리 최적화는 데이터 접근 관점에서 수행하고 정책 판단을 섞지 않는다.
- 목록 조회, 상세 조회, 배치/스케줄러 로직, 응답 조립 구간에서는 N+1 위험을 점검한다.
- N+1 위험이 있으면 fetch join, `@EntityGraph`, projection, 조회 전용 쿼리 분리 등을 사용한다.
- 연관 엔티티를 순회하거나 컬렉션 기반 응답을 조립할 때는 쿼리 수 증가를 의식적으로 확인한다.
- 스키마 변경은 목표 상태 기준으로 Flyway 도입 방향에 맞춘다. 이번 셋업에서 실제 Flyway는 도입하지 않는다.

## Dependency and Reference Rules
- Service 간 직접 양방향 의존을 만들지 않는다.
- 순환참조가 생기면 `@Lazy`로 덮기보다 유스케이스 재분리, 조정 서비스 도입, 조회/명령 분리로 먼저 해결한다.
- Entity 양방향 연관관계는 최소화한다.
- API 응답 직렬화에서 엔티티 양방향 참조로 인한 순환참조를 항상 점검한다.
- 순환참조는 설계 문제 신호로 보고 구조를 다시 본다.

## Testing Rules
- 기본 원칙은 TDD-first다.
- 가능한 경우 실패 테스트 또는 재현 케이스를 먼저 만든다.
- 서비스 테스트는 경량 단위 테스트를 우선한다.
- 컨트롤러 테스트는 `@WebMvcTest` 중심으로 설계한다.
- 저장소 테스트는 JPA 슬라이스 테스트를 우선 고려한다.
- 중요한 흐름은 통합 테스트로 보완한다.
- 기존 테스트가 목표 구조를 따르지 않아도, 새 테스트는 목표 구조를 따르게 한다.
- 통합 테스트에서는 mocking을 최소화한다.
- 통합 테스트의 기본 목적은 실제 Spring context, 실제 JPA, 실제 트랜잭션 흐름을 검증하는 것이다.
- mocking은 외부 시스템 오류 유도, 예외 상황 재현, 만들기 어려운 edge case 구성, 대표 케이스 축약이 필요한 경우에만 제한적으로 사용한다.
- 핵심 비즈니스 흐름 자체를 mock으로 대체하지 않는다.
- 통합 테스트가 느린 단위 테스트처럼 변질되면 테스트 층을 다시 나눈다.

## Refactoring Rules
- 리팩터링은 동작 보존이 우선이다.
- 리팩터링 전에 최소한의 안전망 테스트를 확보한다.
- 현행 레거시 패턴을 복제하지 말고 목표 구조로 점진 이동시킨다.

## Anti-Patterns to Avoid
- field injection
- controller 비즈니스 로직
- god service
- repository 내부 정책 판단
- DTO 검증 없는 진입점
- 임시 편의를 위한 과도한 static helper
- SQL 또는 쿼리 계층에 비즈니스 정책을 숨기는 방식
