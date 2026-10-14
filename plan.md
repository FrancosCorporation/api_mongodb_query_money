# Plan — api_mongodb_query_money

## 1. Visão geral
API REST de carteira de ações (clientes/JWT/wallets), port Node 22/Express 4 do projeto Spring Boot 2022 (`java/` preservado propositalmente como fonte histórica). Endurecida em 2026-10-11 (SEC-01..08, BUG-01..03) com login limitado por e-mail/IP e tratamento central de erros.

## 2. Arquitetura e tecnologias
Node 18+→22, ESM, express 4, mongoose, helmet, jsonwebtoken (HS512), bcryptjs. Bootstrap de teste injeta segredo efêmero (`test/bootstrap.mjs`). Docker/Compose presente.

## 3. Funcionalidades existentes
- [x] Clientes, carteiras, JWT estrito, rate limit de login — implementado.
- [x] CI (npm ci, sintaxe, bootstrap de teste) — **modernizada nesta sessão** (Ubuntu 24.04, Actions v7 SHA, Node 22/24, gate audit).

## 4. Objetivos futuros
- OBJ-01 (P2, concluído nesta sessão): CI previsível com gates.
- OBJ-02 (P3): TEST-01/02 — suítes de contrato das rotas (vedadas nesta esteira).
- OBJ-03 (P3, externo): rotação do segredo JWT antigo + coordenação da limpeza de histórico.
- OBJ-04 (P3): avaliar atualização do `java/` histórico separadamente do runtime Node (não misturar).

## 5–9. Melhorias/Roadmap/Riscos/Ideias
- Regra preservada: não alterar `java/` (fonte histórica do port).
- Risco: segredo JWT antigo no histórico; mitigação = rotação (ação do dono).

*(Gerado em 2026-10-14 pelo agente orquestrador)*
