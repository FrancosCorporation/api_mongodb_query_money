# Resumo — api_mongodb_query_money

## Sessão 2026-10-14 (planejamento padronizado + modernização da CI)
- **Resumo:** API de carteira já endurecida (2026-10-11). Nesta sessão: CI fixada (Ubuntu 24.04, Actions v7 SHA, Node 22/24, gate `npm audit --audit-level=high`) + docs padronizadas.
- **Validações:** `npm audit --audit-level=high` = 0; `node --check server.js` OK; `git diff --check` limpo; suíte roda apenas na CI (regra).
- **Resultado:** [x] CI verde; [x] docs; [x] `db/` (vazio) e `target/` (92 MB, regenerável via Maven) descartados.
- **Continuidade:** HEAD sincronizado; checkout removível. Não tocar em `java/`.
