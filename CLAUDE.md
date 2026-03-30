@AGENTS.md

# Claude Compatibility Notes

- Project-specific operating guidance lives in `AGENTS.md`.
- Use project subagents from `.claude/agents/*` when delegation helps.
- If a task may depend on private policy and public docs are insufficient, stop and ask the user instead of guessing.
- Prefer the smallest viable change and report concrete verification evidence.
