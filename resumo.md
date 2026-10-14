# Resumo — api_mongodb_query_money

## Sessão 2026-10-14 (planejamento padronizado + modernização da CI)
- **Resumo:** API de carteira já endurecida (2026-10-11). Nesta sessão: CI fixada (Ubuntu 24.04, Actions v7 SHA, Node 22/24, gate `npm audit --audit-level=high`) + docs padronizadas.
- **Validações:** `npm audit --audit-level=high` = 0; `node --check server.js` OK; `git diff --check` limpo; suíte roda apenas na CI (regra).
- **Resultado:** [x] CI verde; [x] docs; [x] `db/` (vazio) e `target/` (92 MB, regenerável via Maven) descartados.
- **Continuidade:** HEAD sincronizado; checkout removível. Não tocar em `java/`.

### Adendo (achados reais da sessão)
1. **CI quebrada desde 2026-10-11** (`npm test` apontava para `test/bootstrap.mjs` nunca commitado) → bootstrap restaurado (segredo JWT efêmero por processo).
2. **BUG CRÍTICO de autenticação:** hardening de 11/10 exigia `payload.jwtid`, mas `jsonwebtoken` serializa a opção `jwtid` como claim `jti` → **todo token válido recebia 401**; todas as rotas protegidas estavam quebradas em produção. Corrigido em `fix(auth)`. A suíte de integração pré-existente (13 asserts), finalmente executada na CI, detectou e comprovou a correção.
3. CI `38526240649` verde em Node 22/24 com 13/13 asserts.
