# Subagents Guide

저장소 전용 서브에이전트는 실제 Codex 기본 역할에 매핑되어야 한다.
이 문서는 저장소의 역할 개념 정본이다.
Claude Code 사용자는 실제 실행 자산으로 `.claude/agents/*`를 사용한다.
현재 `.claude/agents/*`에는 자주 쓰는 최소 역할만 구현되어 있으며, 나머지 역할은 필요할 때만 추가한다.
Claude 실행 동작은 `.claude/agents/*`가 우선하고, 역할 책임 정의는 이 문서를 기준으로 유지한다.

## Role Map
- `requirements-analyzer` -> `analyst`
- `architecture-reviewer` -> `architect`
- `implementation-agent` -> `executor`
- `test-implementation` -> `test-engineer`
- `qa-verifier` -> `verifier`
- `final-reviewer` -> `code-reviewer`

## Claude Mapping
- Claude `debugger`:
  - Codex 쪽에서는 주로 `requirements-analyzer` 또는 `architecture-reviewer`가 맡는 원인 분석 역할에 대응한다.
- Claude `test-runner`:
  - Codex 쪽 `qa-verifier`와 가장 가깝고, 필요 시 `test-implementation`과 연계한다.
- Claude `code-reviewer`:
  - Codex 쪽 `final-reviewer`와 가장 가깝다.
- Claude 쪽에는 최소 실행 자산만 두므로, 모든 Codex 역할이 1:1로 Claude subagent를 가지지는 않는다.

## Selection Rules
- 요구사항, 범위, 영향 분석은 `requirements-analyzer`
- 계층 분리, 구조 방향, 트랜잭션 경계 점검은 `architecture-reviewer`
- 실제 구현은 `implementation-agent`
- 테스트 설계/구현은 `test-implementation`
- 실행 검증과 회귀 체크는 `qa-verifier`
- 최종 diff 검토는 `final-reviewer`
- Claude 실사용 역할은 현재 `debugger`, `test-runner`, `code-reviewer` 세 가지다.

## Parallelization Boundaries
- 역할별 쓰기 범위가 겹치지 않을 때만 병렬화한다.
- 같은 파일군을 동시에 수정하는 병렬 작업은 피한다.
- 리뷰와 검증은 구현 결과를 전제로 하므로 보통 후행 단계다.
