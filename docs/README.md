# Docs Index

이 문서는 저장소의 공개 운영 문서를 빠르게 찾기 위한 인덱스다.

## 읽는 순서
1. `AGENTS.md`
2. `CLAUDE.md` if using Claude Code
3. `docs/operations/policies/*`
4. `.codex/rules/*`
5. `.codex/workflows/*`
6. `.codex/subagents/*`
7. `.claude/agents/*` if using Claude Code subagents
8. `.codex/skills/README.md`

## 문서 분류
- `docs/api/`: 공개 API 계약 문서
- `docs/operations/policies/`: 공개 운영 정책 정본
- `.codex/rules/`: Codex 실행 시 바로 참고할 구현 규칙
- `.codex/workflows/`: 작업 유형별 단계형 흐름
- `.codex/subagents/`: 역할 분리와 책임 경계
- `.claude/agents/`: Claude Code용 실제 실행 서브에이전트
- `.codex/skills/README.md`: 저장소 전용 스킬 인덱스

## 빠른 링크
- [루트 가이드](../AGENTS.md)
- [게시글 목록 API](api/post-list.md)
- [소셜 로그인 API](api/social-login.md)
- [Claude Compatibility](../CLAUDE.md)
- [프로젝트 규약](operations/policies/PROJECT_CONVENTIONS.md)
- [스택 마이그레이션 가이드](operations/policies/STACK_MIGRATION_GUIDE.md)
- [ERD 및 삭제 라이프사이클](operations/policies/ERD_DECISIONS_AND_LIFECYCLE.md)
- [조회 전략 및 N+1 가드레일](operations/policies/QUERY_STRATEGY_AND_N_PLUS_ONE.md)
- [외부 리뷰 워크플로우](operations/policies/EXTERNAL_REVIEW_WORKFLOW.md)
- [도메인 문서 레지스트리](operations/policies/DOMAIN_DOCUMENT_REGISTRY.md)
- [가이던스 스키마](operations/policies/guidance-schema.md)
- [Codex Rules](../.codex/rules/README.md)
- [Codex Workflows](../.codex/workflows/README.md)
- [Codex Subagents](../.codex/subagents/README.md)
- [Claude Agents Directory](../.claude/agents)
- [Codex Skills](../.codex/skills/README.md)

## 비공개 정책 경계
- 이 저장소에는 공개 가능한 운영 규칙만 둔다.
- 서비스 정책, 비즈니스 정책, 운영 판단 기준의 본문은 저장소 밖 비공개 문서에서 관리한다.
- 공개 문서에는 private consult 필요 여부만 드러낸다.
