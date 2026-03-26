# Spring Core Rules

## Layer Responsibilities
- Controller:
  - HTTP 요청/응답 처리
  - 인증 컨텍스트 전달
  - DTO 검증
- Service:
  - 유스케이스 조합
  - 비즈니스 흐름
  - 트랜잭션 경계
- Repository:
  - 조회와 저장
  - 정책 없는 데이터 접근
- Entity:
  - 상태 보존
  - 최소한의 불변성/상태 전이
- Converter/Mapper:
  - 표현 변환만 수행

## Required Practices
- constructor injection only
- DTO와 Entity 분리
- 메서드 이름에 유스케이스 의도를 드러낼 것
- 서비스 메서드에서 트랜잭션 의도를 드러낼 것

## Avoid
- field injection
- controller 비즈니스 로직
- repository 정책 판단
- god service
- 의미 없는 util 클래스 증식
- DTO 검증 없는 외부 진입점

## Legacy Handling
- 기존 구조가 이 규칙과 다르더라도 신규 코드와 리팩터링은 이 규칙을 목표로 한다.
