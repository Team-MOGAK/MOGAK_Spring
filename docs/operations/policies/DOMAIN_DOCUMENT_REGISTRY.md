# Domain Document Registry

이 문서는 도메인별로 어떤 공개 문서를 먼저 consult해야 하는지 보여주는 경량 지도다.

## Rules
- 공개 문서로 충분한 기술 판단은 이 레지스트리와 연결 문서를 따른다.
- `private-policy: yes`인 도메인에서 공개 문서만으로 판단이 부족하면 비공개 정책 consult 또는 사용자 확인이 필요하다.
- 비공개 정책의 이름과 내용은 이 문서에 적지 않는다.

## Domain Map

| Domain | Public Consult Docs | private-policy |
| --- | --- | --- |
| repo-governance | `AGENTS.md`, `CLAUDE.md`, `PROJECT_CONVENTIONS.md`, `EXTERNAL_REVIEW_WORKFLOW.md`, `guidance-schema.md` | no |
| auth-security | `PROJECT_CONVENTIONS.md`, `.codex/rules/spring-core.md`, `.codex/workflows/full-delivery.md` | yes |
| user-profile | `PROJECT_CONVENTIONS.md`, `.codex/rules/spring-core.md`, `.codex/rules/testing.md` | yes |
| mogak | `PROJECT_CONVENTIONS.md`, `.codex/rules/spring-core.md`, `.codex/workflows/full-delivery.md` | yes |
| jogak | `PROJECT_CONVENTIONS.md`, `.codex/rules/spring-core.md`, `.codex/workflows/full-delivery.md` | yes |
| post-comment | `PROJECT_CONVENTIONS.md`, `.codex/rules/spring-core.md`, `.codex/rules/testing.md` | yes |
| follow-networking | `PROJECT_CONVENTIONS.md`, `.codex/rules/spring-core.md`, `.codex/rules/testing.md` | yes |
| scheduler | `PROJECT_CONVENTIONS.md`, `.codex/rules/testing.md`, `.codex/workflows/full-delivery.md` | yes |
| storage | `PROJECT_CONVENTIONS.md`, `.codex/rules/persistence.md`, `.codex/workflows/full-delivery.md` | yes |
| persistence | `PROJECT_CONVENTIONS.md`, `.codex/rules/persistence.md`, `EXTERNAL_REVIEW_WORKFLOW.md` | no |
| platform-common | `PROJECT_CONVENTIONS.md`, `.codex/rules/spring-core.md`, `.codex/workflows/refactor-guarded.md` | no |

## Consult Triggers
- 도메인 동작이 바뀌는 구현
- API 계약, 인증, 검증 기대치 변경
- DB 영향 또는 스키마 영향 변경
- 테스트 시나리오 의미가 바뀌는 변경
- 여러 계층을 가로지르는 구조 변경

## Maintenance Rule
- 새 도메인이 생기거나 consult 문서 세트가 바뀌면 이 문서를 같은 변경셋에서 갱신한다.
