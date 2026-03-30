# Guidance Schema

공개 문서와 비공개 문서의 역할 분리는 다음과 같다.

## Public Files
- `AGENTS.md`: 저장소 전체 기본 작업 규칙
- `CLAUDE.md`: Claude Code 호환 진입점. 정본은 `AGENTS.md`를 참조한다.
- `docs/operations/policies/*`: 공개 운영 정책 정본
- `.codex/rules/*`: Codex 실행용 구현 규칙
- `.codex/workflows/*`: 작업 유형별 단계형 흐름
- `.codex/subagents/*`: 역할 분리와 책임 경계
- `.codex/skills/README.md`: 저장소 전용 스킬 인덱스
- `.claude/agents/*`: Claude Code용 실제 프로젝트 서브에이전트

## Private Documents
- 서비스 정책
- 비즈니스 정책
- 운영 판단 기준

공개 저장소는 private 문서의 내용을 복제하지 않는다.

## Precedence
1. system / developer / user 지시
2. 더 하위의 `AGENTS.md`
3. 루트 `AGENTS.md`
4. `docs/operations/policies/*`
5. `.codex/*`

## Handling Rule
- 공개 문서만으로 충분한 기술 판단은 공개 문서에서 끝낸다.
- 공개 문서만으로 충분하지 않은 정책 판단은 private consult 또는 사용자 확인으로 넘긴다.
- Codex 역할 개념 정본은 `.codex/subagents/*`에 두고, Claude 실행 프롬프트는 `.claude/agents/*`에서 최소 집합만 유지한다.
