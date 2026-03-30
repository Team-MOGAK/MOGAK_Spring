# External Review Workflow

이 문서는 중요 변경에 대한 공개 외부 리뷰 기준을 정의한다.

## Goals
- correctness, regression risk, policy drift, missing tests를 조기에 잡는다.
- 작은 로컬 수정에는 과도한 리뷰 오버헤드를 피한다.

## When External Review Is Required
다음 중 하나라도 해당하면 외부 리뷰를 요구한다.

- 새 공개 API 추가
- 요청/응답 계약 변경
- 인증/보안 동작 변경
- DB 스키마 영향 변경
- 새 공유 규칙, 추상화, 기반 문서 추가
- 여러 production 파일에 걸친 비국소 수정
- 사용자 가시 동작의 실질적 변경

다음은 보통 생략 가능하다.

- 작은 단일 버그 수정
- null-guard, typo, local cleanup
- 테스트 코드만의 수정
- 동작 보존 리팩터링이면서 범위가 작은 경우

## Review Order
1. 가장 작은 실행 가능한 변경으로 구현한다.
2. 로컬 검증을 먼저 수행한다.
3. 중요 변경이면 외부 리뷰를 수행한다.
4. finding을 triage 한다.
5. 수정 후 다시 로컬 검증을 수행한다.

## Review Input Contract
- 변경 요약
- 비목표 범위
- 실제 diff
- 로컬 검증 결과
- 저장소 제약
- consult한 공개 문서 목록

저장소 제약에는 다음을 명시한다.
- smallest viable change
- local fix 우선
- blast radius 최소화
- TDD-first when feasible
- correctness/regression/test gap 우선

## Triage Rules
- `fix`: 명확한 버그, 회귀 위험, 공개 규약 위반
- `verify`: 테스트 또는 코드 확인이 더 필요함
- `ignore`: 문맥상 false positive
- `escalate`: 공개 규칙만으로 결정 불가한 정책 문제

`escalate`는 사용자 확인 없이는 닫지 않는다.

## Review Focus
1. correctness
2. regression risk
3. 공개 규약 위반
4. missing tests
5. overengineering

## Notes
- 비공개 정책 내용은 리뷰 입력에 포함하지 않는다.
- 공개 규칙만으로 설명 불가한 정책 문제는 “비공개 정책 consult 필요” 수준으로만 표시한다.
