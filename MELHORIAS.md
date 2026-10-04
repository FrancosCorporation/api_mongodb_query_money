# MELHORIAS — api_mongodb_query_money

> **Gerado por análise de código em 2026-10-02** · Stack: Node 18+ (Express + Mongoose) + JWT HS512 + BCrypt · port do Spring Boot de 2022
> Branch `api_mongodb_query_money-version-1.2` · base `9620d7d` · 3.241 LOC · **1 teste** (`test/api-test.mjs`, 13 asserts)
>
> **Este arquivo é um plano de execução.** Cada item tem ID, `arquivo:linha`, mudança exata,
> critério de aceite e comando de verificação.

---

## 0. Como usar este documento

1. Execute na ordem **P0 → P1 → P2 → P3**, respeitando as ondas da §8.
2. Ao terminar um item: marque `- [x]`, rode o **Verificação**, comite `fix(<ID>): descrição`.
3. **Não troque a biblioteca de auth.** O `jsonwebtoken` + BCrypt já estão corretos; os defeitos são
   **de configuração e de autorização**, não de arquitetura.
4. **Não mexa em `java/`.** É o código de origem Spring Boot preservado por decisão do projeto
   (commit `9620d7d` moveu as fontes Node para `src/`). Toda correção vai em `src/` ou `server.js`.
5. **Idioma:** comentários e respostas em português (padrão do repo); commits em inglês com
   prefixo `fix:`/`feat:`/`docs:`.

---

## 1. Diagnóstico executivo

API REST de carteira de ações: clientes com cadastro, login por JWT e carteira de ações validada
contra uma coleção `ActionName`. É uma conversão fiel do Spring Boot original — o comentário de
`src/middleware/auth.js:3` (`"Port de Auth_token.java/Auth_valid.java: HMAC512, expiração 24h"`) e o
`src/controllers/index.js:41` (`"BCryptPasswordEncoder.encode()"`) mostram a correspondência.

**O que está bem (não refaça):**

| Item | Evidência |
|---|---|
| Senhas com BCrypt, custo 10 | `src/controllers/index.js:41` (`bcrypt.hashSync(password, 10)`) |
| Senha nunca sai na resposta | `src/controllers/index.js:6` (`const semSenha = ({password, ...c}) => c`) |
| JWT com `HS512` explícito | `src/middleware/auth.js:10` e `:24` (`algorithms: ['HS512']`) |
| Rotas protegidas exigem Bearer | `server.js:18-26` (`exigirToken` em todas) |
| O secret aceita env | `auth.js:6` tem `process.env.JWT_SECRET ||` — o problema é o **fallback** |
| Segredos de build fora do git | `git ls-files \| grep -c '^target/'` → **0** |
| 13 asserts de integração | `test/api-test.mjs` (register/login/401/wallet) |
| Erro de e-mail repetido tratado | `index.js:34-36` (devolve `409`) |

**O que está quebrado:** o token prova **quem** você é, mas o código **nunca pergunta se você é o
dono** daquele registro. Não existe uma linha sequer verificando `req.user` contra `req.params.id`
(confirmei: `grep 'req.user\|decoded\|payload' src/ server.js` → só `jwtid: id` na geração).
Isso transforma qualquer token válido em acesso administrativo sobre **todos** os clientes.

Há ainda o `fallback` de segredo hardcoded (`auth.js:6`), que herda o problema da versão Java — o
próprio comentário admite: *"O secret do original era fixo no código"*.

---

## 2. Tabela de prioridades

| ID | Título | Sev | Arquivo | Depende de |
|---|---|---|---|---|
| SEC-01 | Fallback de segredo JWT hardcoded | **P0** | `src/middleware/auth.js:6` | — |
| SEC-02 | IDOR: qualquer token edita/deleta qualquer cliente | **P0** | `src/controllers/index.js:46-59` | SEC-03 |
| SEC-03 | Token não é usado após a verificação (claims ignoradas) | **P0** | `src/middleware/auth.js:24` | — |
| SEC-04 | `listarClientes` devolve todos os clientes a qualquer um | **P0** | `src/controllers/index.js:9-12` | SEC-03 |
| SEC-05 | Login sem rate limit (brute-force) | **P1** | `server.js:15` | — |
| SEC-06 | Mass assignment em `modificarCliente` | **P1** | `src/controllers/index.js:47` | SEC-02 |
| BUG-01 | IDOR na carteira: ler/editar carteira de terceiro | **P1** | `src/controllers/index.js:66-96` | SEC-03 |
| BUG-02 | `registrar` não valida formato de e-mail/senha | **P1** | `src/controllers/index.js:29-33` | — |
| SEC-07 | Sem CORS declarado (nem restrito) | **P2** | `server.js:11` | — |
| SEC-08 | Sem `helmet` / headers de segurança | **P2** | `server.js:11` | — |
| BUG-03 | `catch` engolindo erro do Mongoose vira `500` sem log | **P1** | `src/controllers/index.js` (async) | — |
| IMP-01 | `semSenha` não cobre `Wallet`/`ActionName` | **P2** | `src/controllers/index.js:62-70` | — |
| IMP-02 | Nenhum `try/catch` nos handlers async | **P1** | `server.js` | — |
| TEST-01 | Testes não cobrem IDOR nem autorização | **P1** | `test/api-test.mjs` | SEC-02, SEC-04 |
| TEST-02 | Sem teste de token expirado/forjado | **P1** | novo `test/` | SEC-01 |
| DEVOPS-01 | Sem Dockerfile nem CI (tem compose) | **P2** | `docker-compose.yml` | — |
| DEVOPS-02 | Sem `.env.example` | **P2** | *(ausente)* | SEC-01 |
| DEVOPS-03 | `target/` e `java/` no disco: higiene de build | **P3** | `java/` (100 arq trackeados) | — |
| DOC-01 | README não documenta o `JWT_SECRET` obrigatório | **P2** | `README.md` | SEC-01 |
| DOC-02 | Falta `SECURITY.md` | **P3** | *(ausente)* | — |

**Placar: 4 P0 · 7 P1 · 5 P2 · 2 P3 = 18 itens.**

---

## 3. Segurança
### SEC-01 · Fallback de segredo JWT hardcoded · [P0]

- **Arquivo:** `src/middleware/auth.js:6`
- **Evidência:**
  ```javascript
  const TOKEN_SENHA = process.env.JWT_SECRET || '<UUID-HARDCODED-NO-CODIGO>';
  ```
  O comentário nas linhas 4-5 admite: *"O secret do original era fixo no código; aqui via env com
  fallback documentado"*. O valor é um UUID v4 — previsível em formato e **literalmente publicado**
  neste repositório.
- **Impacto:** se `JWT_SECRET` não estiver setado (o caso mais comum num deploy apressado), a API
  **sobe normalmente** usando o segredo público do Git. Com esse segredo, qualquer pessoa assina
  `HS512` válido com `sub` e `jwtid` à escolha — acesso total a todos os clientes e carteiras.
  Pior: como é idêntico em toda cópia do projeto, um token emitido numa instância valida em
  qualquer outra (token confusion entre ambientes).
- **Mudança:**
  1. Trocar o fallback por **falha explícita** na inicialização:
     ```javascript
     const TOKEN_SENHA = process.env.JWT_SECRET;
     if (!TOKEN_SENHA) {
       throw new Error(
         'JWT_SECRET nao definido. Exporte a variavel (ex.: openssl rand -hex 32) antes de iniciar.'
       );
     }
     if (Buffer.byteLength(TOKEN_SENHA, 'utf8') < 32) {
       throw new Error('JWT_SECRET deve ter no minimo 32 bytes.');
     }
     ```
     Um fallback silencioso é **pioor** que não ter env: mascara o problema e mantém o vazamento.
  2. Deixar de lado o argumento "herança do Java": o `condominio_api` irmão já migrou para
     `Environment.GetEnvironmentVariable` **sem fallback** (`condominio_api/Config/Settings.cs:19`)
     — o padrão da casa já é falhar cedo.
  3. **Rotacionar**: o UUID hardcoded (ver `src/middleware/auth.js:6`) é considerado comprometido para sempre; invalidar todos
     os tokens emitidos com ele.
- **Aceite:** API **não inicia** sem `JWT_SECRET`; com segredo < 32 bytes também recusa; o literal
  não existe mais em nenhum arquivo.
- **Verificação:**
  ```bash
  grep -rn "<UUID-HARDCODED>" . --include='*.js' && echo FALHA || echo OK
  # substitua <UUID-HARDCODED> pelo valor que hoje esta em src/middleware/auth.js:6
  unset JWT_SECRET; node server.js          # deve recusar com mensagem clara
  JWT_SECRET=curto node server.js           # deve recusar por tamanho
  JWT_SECRET=$(openssl rand -hex 32) node server.js   # deve subir
  ```

### SEC-03 · Token verificado mas as claims nunca são usadas · [P0]

- **Arquivo:** `src/middleware/auth.js:24` (`jwt.verify(...); next();`)
- **Evidência:**
  ```javascript
  try {
    jwt.verify(cabecalho.slice(7), TOKEN_SENHA, { algorithms: ['HS512'] });
    next();
  ```
  O retorno de `jwt.verify` é **descartado**. Nenhuma parte do código lê `req.user`, `req.decoded`
  ou payload algum — confirmei com `grep -rn 'req.user\|decoded\|payload' src/ server.js`, cujo
  único resultado é `jwtid: id` em `auth.js:12` (na geração, não na leitura).
- **Impacto:** o token prova autenticidade **e nada mais**. Como o payload nunca é propagado, é
  **impossível** ao controller saber quem pediu — por isso `SEC-02` e `BUG-01` existem: sem o
  `req.user`, nenhum handler pode checar dono. Este item é a **raiz** dos 4 P0 de autorização.
- **Mudança:** guardar o payload no request e propagar `subject`/`jwtid`:
  ```javascript
  try {
    const payload = jwt.verify(cabecalho.slice(7), TOKEN_SENHA, { algorithms: ['HS512'] });
    req.user = { id: payload.jwtid, email: payload.sub };   // sub = email (ver auth.js:11)
    return next();
  } catch {
    return res.status(401).json({ message: 'Token ausente ou inválido' });
  }
  ```
  Com `req.user` preenchido, `SEC-02`/`BUG-01` viram uma checagem de uma linha.
- **Aceite:** após o middleware, `req.user.id` e `req.user.email` estão disponíveis em todo handler.
- **Verificação:**
  ```bash
  grep -n 'req.user' src/middleware/auth.js src/controllers/index.js   # existe em ambos
  ```

### SEC-02 · IDOR: qualquer token edita ou deleta qualquer cliente · [P0]

- **Arquivo:** `src/controllers/index.js:46-53` (`modificarCliente`) e `:55-59` (`removerCliente`)
- **Evidência:**
  ```javascript
  export async function modificarCliente(req, res) {
    const alteracoes = { ...req.body, changeDate: new Date() };
    ...
    const cliente = await Client.findOneAndUpdate({ id: req.params.id }, alteracoes, { new: true })
  ```
  O `id` vem **da URL** (`req.params.id`) e não há comparação nenhuma com o dono do token. Idem em
  `removerCliente` (linha 56) que faz `Client.findOneAndDelete({ id: req.params.id })`.
- **Impacto:** **Insecure Direct Object Reference clássico.** Qualquer usuário autenticado pode:
  (a) alterar a senha de **qualquer outro cliente** (`index.js:48` hashea o que vier do body) —
  tomando a conta; (b) **deletar** qualquer conta com `DELETE /api/client/<id-dele>`; (c) trocar o
  e-mail da vítima. Como o `DELETE` está roteado em `server.js:21` sem outra checagem, é reprodução
  trivial a partir do próprio Swagger/`curl`. Em sistema financeiro (carteira de ações), deletar ou
  sequestrar conta de terceiro é dano material.
- **Mudança:** checar dono em **todo** handler que recebe `:id`:
  ```javascript
  if (req.params.id !== req.user.id) return res.status(403).json({
    message: 'Acesso negado: voce so pode alterar o proprio registro' });
  ```
  (Requer `SEC-03` — sem `req.user`, a checagem é impossível.) Para operações administrativas
  legítimas, exige `role` no token, que hoje **não existe**.
- **Aceite:** token do cliente A + `PUT`/`DELETE` em `/<id-do-B>` → `403`; o próprio registro continua
  funcionando.
- **Verificação:**
  ```bash
  # criar A e B, logar como A, tentar deletar B:
  curl -s -o /dev/null -w '%{http_code}\n' -X DELETE http://localhost:3000/api/client/$ID_B \
    -H "Authorization: Bearer $TOKEN_A"     # esperado 403 (hoje: 200)
  ```

### SEC-04 · `listarClientes` devolve todos os clientes a qualquer token · [P0]

- **Arquivo:** `src/controllers/index.js:9-12` · rota `server.js:18`
- **Evidência:**
  ```javascript
  export async function listarClientes(req, res) {
    const clientes = await Client.find().lean();
    res.json(clientes.map(semSenha));
  }
  ```
  `GET /api/client` (rota pública sob `exigirToken`) retorna **a coleção inteira**, sem filtro de dono.
- **Impacto:** é **Broken Object Level Authorization** (BOLA). Um usuário com conta mínima lista
  todos os clientes da base — nomes, e-mails e datas (`src/models/index.js:4-11`). Apesar da senha
  ser omitida, vazamento de base inteira de clientes é violação de privacidade e, em sistema
  financeiro, expõe a base de usuários e o tamanho da carteira. Serve ainda de **alvo** para o
  `SEC-02`: o atacante lê a lista para escolher quem sequestrar.
- **Mudança:** (1) restringir ao próprio usuário (`Client.find({ id: req.user.id })`); (2) se houver
  necessidade de listagem admin, exigir `role: 'admin'` no token e rota separada `/api/admin/clients`;
  (3) remover ou proteger o endpoint aberto.
- **Aceite:** `GET /api/client` com token de A devolve **só** o registro de A.
- **Verificação:**
  ```bash
  curl -s -H "Authorization: Bearer $TOKEN_A" http://localhost:3000/api/client | jq length
  # esperado: 1 (hoje: N total de clientes)
  ```
### SEC-05 · Login sem rate limit (brute-force) · [P1]

- **Arquivo:** `server.js:15` (`app.post('/api/client/login', login);`)
- **Evidência:** a rota de login é pública e direta. Não existe `express-rate-limit`, nem contador em
  `login` (`src/controllers/index.js:20-27`), nem janela de bloqueio em lugar nenhum do projeto.
- **Impacto:** brute-force ilimitado contra qualquer e-mail. O BCrypt custo 10 (`index.js:41`) retarda
  o atacante, mas não o impede: com paralelismo ele faz milhares de tentativas por minuto. E como
  `SEC-04` expõe a lista de e-mails de **todos** os clientes, o ataque alvo sai de graça — ele não
  precisa descobrir usuários, já os tem todos.
- **Mudança:** (1) rate limit no login: **5 tentativas por e-mail a cada 5 min**, mais um teto por IP
  (ex.: 30/5 min) para a conta da vítima não virar alvo de DoS; (2) responder `429` com `Retry-After`;
  (3) resposta **uniforme** para e-mail inexistente e senha errada (`401` idêntico, ver `BUG-02`);
  (4) logar IP + e-mail + resultado, nunca a senha.
- **Aceite:** o 6º login errado para o mesmo e-mail em 5 min devolve `429`.
- **Verificação:**
  ```bash
  for i in $(seq 1 7); do
    curl -s -o /dev/null -w "tentativa $i -> %{http_code}\n" -X POST \
      http://localhost:3000/api/client/login -H 'Content-Type: application/json' \
      -d '{"email":"alvo@x.com","password":"errada"}'
  done
  # esperado: 401 x5, depois 429
  ```

### SEC-06 · Mass assignment em `modificarCliente` · [P1]

- **Arquivo:** `src/controllers/index.js:47` (`const alteracoes = { ...req.body, changeDate: new Date() };`)
- **Evidência:** o corpo é copiado **inteiro** para a atualização. Só `id` é removido (linha 49:
  `delete alteracoes.id`). As demais chaves do schema são aceitas à vontade.
- **Impacto:** um cliente autenticado pode alterar `email` para o de outra pessoa (o índice único de
  `models/index.js:8` então rejeita, mas com erro `500` se não tratado — vira bug do `BUG-03`), ou
  injetar `changeDate` arbitrário. Combinado com o `SEC-02` (sem checagem de dono), um atacante
  controla os campos de **qualquer** registro. É o padrão clássico de *mass assignment*.
- **Mudança:** usar **allowlist** em vez de spread:
  ```javascript
  const CAMPOS = ['name', 'password', 'email'];
  const alteracoes = {};
  for (const c of CAMPOS) if (c in req.body) alteracoes[c] = req.body[c];
  alteracoes.changeDate = new Date();
  delete alteracoes.id;
  ```
  Nunca propagar `...req.body` para operação de escrita.
- **Aceite:** enviar `{"id":"outro","role":"admin"}` e confirmar que `id`/`role` não mudam nada;
  só `name`/`password`/`email` têm efeito.
- **Verificação:**
  ```bash
  curl -s -X PUT http://localhost:3000/api/client/$MEU_ID \
    -H "Authorization: Bearer $MEU_TOKEN" -H 'Content-Type: application/json' \
    -d '{"id":"FORJADO","name":"ok"}' | grep -q FORJADO && echo FALHA || echo OK
  ```

### SEC-07 · Sem CORS declarado · [P2]

- **Arquivo:** `server.js:11` (`app.use(express.json());` — único middleware)
- **Evidência:** `grep -rn 'cors' server.js src/ package.json` → **nada**. O pacote `cors` não está
  nem em `package.json:14-19`.
- **Impacto:** sem `cors`, o Express não emite `Access-Control-Allow-Origin`, então navegadores
  bloqueiam chamadas cross-origin. **Isso é seguro por acidente**, não por decisão: (a) a API não
  funciona de um front hosteado em outro domínio sem que alguém "resolva" adicionando
  `cors()` cego — o que é exatamente como esses problemas nascem; (b) pré-flight não é tratado
  (`OPTIONS` cai em `404`), o que torna o comportamento imprevisível.
- **Mudança:** (1) declarar CORS **explícito** com allowlist de origens via env
  (`ALLOWED_ORIGINS=https://app.exemplo.com`); (2) recusar `OPTIONS` de origem não listada;
  (3) documentar no `DEVOPS-02` que a lista é obrigatória — nunca `origin: true` genérico.
- **Aceite:** origem da allowlist funciona; origem de fora é bloqueada e o pre-flight responde certo.
- **Verificação:**
  ```bash
  curl -sI -X OPTIONS http://localhost:3000/api/client \
    -H 'Origin: https://evil.example' -H 'Access-Control-Request-Method: GET' \
    -D - | grep -i 'access-control-allow-origin'   # nao deve aparecer para origem de fora
  ```

### SEC-08 · Sem `helmet` nem headers de segurança · [P2]

- **Arquivo:** `server.js:11` (nenhum middleware de cabeçalho)
- **Evidência:** o único `app.use` é `express.json()`. Não há `helmet`, nem `X-Content-Type-Options`,
  `X-Frame-Options`, `Strict-Transport-Security` ou `Content-Security-Policy`.
- **Impacto:** sem `X-Content-Type-Options: nosniff`, uma resposta JSON pode ser interpretada como
  outro tipo. Sem HSTS, o primeiro acesso pode ser HTTP. Não é explotável sozinho numa API JSON,
  mas é a camada de 5 minutos que elimina classes inteiras de problema quando o front existir.
- **Mudança:** `npm i helmet` + `app.use(helmet())` logo após `express()`; adicionar HSTS apenas
  quando houver TLS (fora de `localhost`).
- **Aceite:** toda resposta traz `x-content-type-options: nosniff` e demais headers do helmet.
- **Verificação:**
  ```bash
  curl -sI http://localhost:3000/ | grep -i 'x-content-type-options'   # nosniff
  ```

---

## 4. Bugs e defeitos funcionais

### BUG-01 · IDOR na carteira: ler e editar carteira de terceiro · [P1]

- **Arquivo:** `src/controllers/index.js:66-69` (`carteiraPorId`), `:74-96` (`registrarCarteira`)
- **Evidência:**
  ```javascript
  export async function carteiraPorId(req, res) {
    const carteira = await Wallet.findOne({ id: req.params.id }).lean();
    ...
  export async function registrarCarteira(req, res) {
    const { id, actionsAndPrice } = req.body;
    const cliente = await Client.findOne({ id });
  ```
  Em ambos os casos o `id` vem da URL (`:id`) ou do **corpo** (`req.body.id`) — nunca do token.
- **Impacto:** qualquer autenticado lê a carteira de qualquer outro (`GET /api/wallet/<id>`) e,
  pior, **altera** a carteira alheia via `POST /api/wallet/register` com `id` da vítima. Numa API de
  ações, adulterar a carteira alheia (trocar ação, preço ou quantidade) é dano financeiro direto e
  não rastreável — não há log de quem alterou. Note que `registrarCarteira` sequer exige que o `id`
  do body seja o do próprio usuário.
- **Mudança:** (1) em `carteiraPorId`, ignorar `req.params.id` e usar `req.user.id`;
  (2) em `registrarCarteira`, usar `req.user.id` **em vez** de `req.body.id` (e rejeitar se
  divergirem); (3) aplicar a mesma checagem de `SEC-02` de forma uniforme.
- **Aceite:** token de A lendo/editando carteira de B → `403`.
- **Verificação:**
  ```bash
  curl -s -o /dev/null -w '%{http_code}\n' -X POST http://localhost:3000/api/wallet/register \
    -H "Authorization: Bearer $TOKEN_A" -H 'Content-Type: application/json' \
    -d "{\"id\":\"$ID_B\",\"actionsAndPrice\":[{\"nameAction\":\"PETR4\"}]}"
  # esperado 403
  ```

### BUG-03 · Handlers async sem `try/catch`: erro vira `500` sem log · [P1]

- **Arquivo:** `src/controllers/index.js` — todos os `export async function` (linhas 9, 14, 20, 29,
  46, 55, 62, 66, 74, 99)
- **Evidência:** nenhum handler tem `try/catch`, e `server.js` não registra middleware de erro
  (não há `app.use((err, req, res, next) => ...)`). O Express **não** captura rejeição de promise
  automaticamente em handlers async.
- **Impacto:** qualquer erro de rede/Mongo (TimeExceeded, duplicate key no `index.js:34` que não é
  `findOne` prévio, etc.) rejeita a promise **sem resposta** — o cliente fica pendurado até o timeout
  do socket, e **nada é logado**. É falha silenciosa: o usuário vê "sem resposta" e o servidor não
  regista erro algum. Agravante: o `SEC-06` torna `500` por índice único provável.
- **Mudança:** (1) criar um wrapper `asyncHandler(fn)` aplicado a todos os handlers, que repassa o
  erro ao `next`; (2) adicionar middleware de erro final que **loga** o erro e devolve
  `{ message: 'Erro interno' }` sem vazar stack; (3) mapear `MongoServerError` para status adequados
  (`409` duplicado, `503` banco).
- **Aceite:** simular falha do Mongo → cliente recebe `503` e há linha de log com a causa.
- **Verificação:**
  ```bash
  # derrubar o mongo e chamar uma rota:
  docker stop $(docker ps -qf name=mongo)   # ou desligar o serviço
  curl -s -o /dev/null -w '%{http_code}\n' http://localhost:3000/api/client \
    -H "Authorization: Bearer $TOKEN"
  # esperado 503 (nao timeout) + log no servidor
  ```

### BUG-02 · `registrar` valida presença mas não formato de e-mail/senha · [P1]

- **Arquivo:** `src/controllers/index.js:29-34`
- **Evidência:**
  ```javascript
  const { name, email, password } = req.body;
  if (!name || !email || !password) {
    return res.status(400).json({ message: 'name, email e password são obrigatórios' });
  }
  ```
  Só checa **presença**. Não valida formato de e-mail, nem comprimento mínimo de senha, nem tipo.
- **Impacto:** aceita `email: "nao-e-email"` e `password: "a"` (1 caractere). Consequências:
  (a) conta com e-mail inválido que nunca pode ser usada de verdade; (b) senha de 1 caractere com
  BCrypt custo 10 é proteção de fachada; (c) o índice único de `models/index.js:8` pode rejeitar
  com `500` via `BUG-03`; (d) `name`/`email` não-string causa exceção no Mongoose com `500`.
- **Mudança:** validar tipo (`typeof === 'string'`), e-mail com formato razoável, senha **mínima de 8**
  caracteres, e limitar tamanhos (ex.: `name` 100, `email` 254). Rejeitar com `400` e mensagem que
  diga o requisito. A senha de 8+ deve virar também política do `SEC-05` (login).
- **Aceite:** `password: "a"` e `email: "x"` devolvem `400` com mensagem clara.
- **Verificação:**
  ```bash
  curl -s -o /dev/null -w '%{http_code}\n' -X POST http://localhost:3000/api/client/register \
    -H 'Content-Type: application/json' \
    -d '{"name":"x","email":"nao-e-email","password":"a"}'   # esperado 400
  ```
---

## 5. Qualidade: testes, arquitetura e observabilidade

### IMP-01 · `semSenha` não cobre carteiras e actionnames · [P2]

- **Arquivo:** `src/controllers/index.js:62-70` (`listarCarteiras`, `carteiraPorId`) e `:99-101` (`listarNomes`)
- **Evidência:** `semSenha` é aplicado a `listarClientes` (linha 11), `clientePorId` (17), `login` (26)
  e `modificarCliente` (52). Mas `listarCarteiras` devolve `Wallet.find().lean()` **cru** (linha 63),
  e `carteiraPorId` idem (linha 68).
- **Impacto:** hoje o schema de `Wallet` (`models/index.js:30-33`) só tem `id` e `actionsAndPrice`,
  então não há campo sensível — **ainda**. O risco é latente: qualquer campo novo (ex.: `owner_email`,
  `balance`, dado de corretagem) vaza automaticamente, porque não há filtro. É dívida que se materializa
  no primeiro campo novo.
- **Mudança:** aplicar um filtro de saída consistente em **toda** rota de leitura (função
  `publicar(doc, campos)`), ou melhor: definir `select: false` nos campos sensíveis do schema e
  selecionar explicitamente. Padronizar o padrão já existente em vez de só nos clientes.
- **Aceite:** adicionar um campo sensível ao schema não faz ele aparecer na resposta sem decisão
  explícita.
- **Verificação:**
  ```bash
  grep -n 'semSenha' src/controllers/index.js   # presente em todas as rotas de leitura
  ```

### IMP-02 · Respostas inconsistentes (shape) entre as rotas · [P2]

- **Arquivo:** `src/controllers/index.js` — compara `:16` (`{message: ...}` em 404) com `:43`
  (`semSenha(cliente.toObject())` em 201) e `:95` (`res.status(201).json(...)`, devolvendo o objeto cru)
- **Evidência:** `registrar` devolve o cliente (linha 43), `registrarCarteira` devolve a carteira
  **sem** filtro (linha 95), `login` devolve `{ token, client }` (linha 26) — formatos diferentes para
  operações equivalentes.
- **Impacto:** consumidor precisa tratar cada rota como um caso. Sem contrato definido, um campo novo
  numa rota não aparece nas outras e o front quebra sem aviso. Agravante: `registrarCarteira` devolve
  `Wallet` cru, ligando ao `IMP-01`.
- **Mudança:** definir contrato simples e documentar: sucesso → objeto ou `{data: ...}`; erro → sempre
  `{message: string}` com status adequado; listagens → array. Aplicar em todas as rotas de uma vez.
- **Aceite:** `GET` de qualquer rota de lista devolve array; qualquer erro devolve `{message}`.
- **Verificação:**
  ```bash
  for p in /api/client /api/wallet /api/actionnames; do
    curl -s -H "Authorization: Bearer $T" http://localhost:3000$p | jq -r 'type'
  done   # todos: array
  ```

### TEST-01 · Testes cobrem CRUD mas não autorização · [P1]

- **Arquivo:** `test/api-test.mjs` (13 asserts, linhas 36-86)
- **Evidência:** os `check()` existentes cobrem: register (201), senha omitida, 409, login token,
  401, 401 sem token, listagem, wallet 400/201/merge, `wallet/:id`, actionnames, DELETE. **Nenhum**
  cobre: (a) token de A acessando registro de B; (b) `req.user.id` vs `req.params.id`; (c) token
  expirado; (d) token forjado com outro segredo.
- **Impacto:** os 4 P0 de autorização (`SEC-02`, `SEC-03`, `SEC-04`, `BUG-01`) foram introduzidos e
  **permanecem** porque o teste valida que o fluxo feliz funciona, não que o acesso indevido falha.
  É a razão clássica de BOLA sobreviver: testa-se "funciona para mim" e nunca "não funciona para
  ele".
- **Mudança:** adicionar casos que devem **falhar**:
  | Caso | Esperado |
  |---|---|
  | A lê cliente B via `GET /api/client/:id` | `403` |
  | A edita cliente B via `PUT /api/client/:id` | `403` |
  | A deleta cliente B via `DELETE /api/client/:id` | `403` |
  | `GET /api/client` de A | lista **só** A |
  | A lê carteira de B | `403` |
  | A envia `id: B` em `POST /api/wallet/register` | `403` |
- **Aceite:** `npm test` passa **e** falha se qualquer checagem de dono for removida.
- **Verificação:**
  ```bash
  npm test    # inclui os 6 casos de autorizacao
  # remover um req.user.id check -> npm test deve FALHAR
  ```

### TEST-02 · Sem teste de token expirado ou forjado · [P1]

- **Arquivo:** novo `test/token-test.mjs`
- **Evidência:** o teste atual só prova que `login` devolve token (linha 46) e que rota sem token dá
  401 (linha 55). **Não** testa: token assinado com outro segredo, token expirado, token com `alg`
  trocado (`none`), token com payload alterado.
- **Impacto:** é exatamente o vetor do `SEC-01` — se o segredo voltar a ser o hardcoded, ou se
  `algorithms` deixar de ser fixado em `['HS512']`, nenhum teste quebra. Sem isso, uma regressão de
  criptografia passa despercebida.
- **Mudança:** cobrir: (1) token assinado com chave diferente → `401`; (2) token expirado → `401`;
  (3) `alg: none` → `401` (o `algorithms` explícito de `auth.js:24` deve impedir);
  (4) payload adulterado (trocar `sub`) → `401`; (5) sem `JWT_SECRET` o processo **não inicia**
  (trava do `SEC-01`).
- **Aceite:** `npm test` roda esses 5 casos; remover `algorithms: ['HS512']` faz o teste falhar.
- **Verificação:**
  ```bash
  node test/token-test.mjs    # 5 asserts passando
  ```

---

## 6. DevOps / Infra

### DEVOPS-01 · Tem `docker-compose.yml` mas não tem `Dockerfile` nem CI · [P2]

- **Arquivo:** `docker-compose.yml` (existe, 30 linhas) · *(ausente)* `Dockerfile` · *(ausente)* `.github/workflows/`
- **Evidência:** o inventário lista `docker-compose.yml` e `pom.xml`, mas não `Dockerfile`; não há
  `.github/workflows`. O compose referencia uma imagem que não é construída do código local.
- **Impacto:** o compose não sabe como construir a aplicação — ele parte de uma imagem pronta, o
  que significa que **o build real é manual** e pode divergir do que está no Git. Sem CI, o `npm test`
  (que é bom, 13 asserts) só roda se alguém lembrar.
- **Mudança:** (1) `Dockerfile` multi-stage (`node:20-alpine`, `npm ci --omit=dev`, `USER node`,
  `HEALTHCHECK` em `GET /`); (2) `ci.yml` com `node --check` + `npm ci` + `npm test` em
  `on: [push, pull_request]`.
- **Aceite:** `docker compose up --build` sobe a partir do código local; CI roda os testes.
- **Verificação:**
  ```bash
  docker compose up --build -d && sleep 6
  curl -s http://localhost:3000/ | jq .status     # "online"
  ```

### DEVOPS-02 · Falta `.env.example` para `JWT_SECRET` e `MONGO_URI` · [P2]

- **Arquivo:** *(ausente)* `.env.example` · variáveis em `server.js:32` e `src/middleware/auth.js:6`
- **Evidência:** `server.js:32` usa `MONGO_URI` (com default `mongodb://localhost:27017/query_money`),
  `server.js:37` usa `PORT`, e `auth.js:6` usa `JWT_SECRET`. Não há `.env.example` no repo.
- **Impacto:** após o `SEC-01` (sem fallback de segredo), quem clona **não sobe a API** sem descobrir
  a variável por leitura de código. E o default do `MONGO_URI` esconde que o endpoint é configurável.
- **Mudança:**
  ```
  # Copie para .env e preencha
  JWT_SECRET=       # obrigatorio, min 32 bytes: openssl rand -hex 32
  MONGO_URI=mongodb://localhost:27017/query_money
  PORT=3000
  ALLOWED_ORIGINS=  # lista separada por virgula (SEC-07)
  ```
- **Aceite:** `.env.example` versionado cobre todas as `process.env.*` do código.
- **Verificação:**
  ```bash
  diff <(grep -rhoE 'process\.env\.[A-Z_]+' server.js src/ | sort -u \
         | sed 's/process.env.//') \
       <(grep -oE '^[A-Z_]+' .env.example | sort -u)
  ```

### DEVOPS-03 · `java/` versionado e `target/` no disco · [P3]

- **Arquivo:** `java/` (100 arquivos trackeados), `target/` (**0** trackeados — bom)
- **Evidência:** `git ls-files \| grep -c '^java/'` → 100; `git ls-files \| grep -c '^target/'` → **0**.
  O `README.md` e o commit `9620d7d` documentam que `java/` é o código de origem preservado.
- **Impacto:** o `java/` é **intencional** (histórico preservado, decisão do projeto — não mexer).
  Porém o `target/` existe no disco com artefatos compilados (`target/classes/templates/login.html`)
  e o `.gitignore` precisa cobri-lo para não entrar por engano num `git add -A`. Também há
  duplicação: `java/main/resources/templates/*.html` e `target/classes/...` são a mesma coisa.
- **Mudança:** (1) **não** remover `java/` (decisão de design, documentada); (2) garantir que
  `target/` está no `.gitignore` (está fora do índice — confirmar a regra) e adicionar também
  `/target/` se o `.gitignore` atual for genérico demais; (3) documentar no README que `java/` é
  referência e `src/` é a implementação ativa.
- **Aceite:** `git status` nunca mostra `target/`; README diz qual diretório editar.
- **Verificação:**
  ```bash
  git check-ignore -q target/classes/login.html && echo OK
  git status --porcelain | grep -c '^?? target/'   # 0
  ```

---

## 7. Documentação

### DOC-01 · README não documenta `JWT_SECRET` como obrigatório · [P2]

- **Arquivo:** `README.md` (82 linhas)
- **Evidência:** o README descreve instalação e endpoints, mas o `JWT_SECRET` não aparece como
  variável obrigatória — e hoje **não é**, graças ao fallback de `SEC-01`. Após o `SEC-01`, a API
  passa a **não iniciar** sem ele, e o README fica desatualizado.
- **Impacto:** quem seguir o README ao pé da letra vai receber "JWT_SECRET nao definido" sem saber
  de onde veio o nome da variável — porque ela não está documentada em lugar nenhum.
- **Mudança:** seção "Variáveis de ambiente" com `JWT_SECRET` (e como gerar), `MONGO_URI`, `PORT` e
  `ALLOWED_ORIGINS`; exemplo de `curl` de login e de chamada autenticada; nota de que `java/` é
  referência e `src/` é ativo.
- **Aceite:** seguir o README do zero leva a uma API que sobe e autentica.
- **Verificação:** seguir o README em máquina limpa → `POST /api/client/login` retorna `200`.

### DOC-02 · Falta `SECURITY.md` · [P3]

- **Arquivo:** *(ausente)* `SECURITY.md`
- **Evidência:** o repositório tem `LICENSE` mas nenhum guia de reporte de vulnerabilidade nem
  threat model escrito.
- **Impacto:** as decisões de segurança desta API (claim `sub` = e-mail, `jwtid` = id, autorização
  por dono) existem só no código. Sem threat model, quem for manter não sabe quais invariantes
  proteger — e é exatamente assim que BOLA volta em refactor futuro.
- **Mudança:** criar com: fluxo de reporte; threat model resumido (BOLA/IDOR, forja de token,
  brute-force de login, vazamento de base); e a **invariante central**: *"toda rota com `:id` ou
  `id` no body deve comparar com `req.user.id`"*.
- **Aceite:** arquivo existe e declara a invariante de autorização.
- **Verificação:** `ls SECURITY.md && grep -n 'req.user.id' SECURITY.md`

---

## 8. Ordem de execução (waves)

### Wave 1 — Travar autorização e segredo (P0)
1. **`SEC-03`** — propagar `req.user` no middleware. **Sem isso nada dos próximos é possível.**
2. **`SEC-01`** — remover o fallback hardcoded e **rotacionar** o segredo.
3. **`SEC-02`** — checagem de dono em `modificarCliente` e `removerCliente`.
4. **`SEC-04`** — restringir `listarClientes` ao próprio usuário.

> Após a Wave 1, a API deixa de ser um CRUD aberto a qualquer token.

### Wave 2 — Fechar carteira e login (P1)
5. **`BUG-01`** — IDOR na carteira (`carteiraPorId`, `registrarCarteira`).
6. **`SEC-06`** — allowlist de campos em `modificarCliente`.
7. **`BUG-03`** — `asyncHandler` + middleware de erro com log (trava os `500` silenciosos).
8. **`BUG-02`** — validação de formato de e-mail/senha (senha mínima 8).
9. **`SEC-05`** — rate limit no login (5/5 min por e-mail).
10. **`TEST-01`** e **`TEST-02`** — travar tudo o que foi corrigido (BOLA + token).

### Wave 3 — Plataforma (P2)
11. **`SEC-07`** e **`SEC-08`** — CORS explícito com allowlist; `helmet`.
12. **`DEVOPS-02`** — `.env.example` (exige o `SEC-01` para listar as variáveis novas).
13. **`DEVOPS-01`** — Dockerfile + CI.
14. **`IMP-01`**, **`IMP-02`** — filtro de saída e contrato de resposta.
15. **`DOC-01`** — README com as variáveis.

### Wave 4 — Higiene (P3)
16. **`DEVOPS-03`**, **`DOC-02`**.

**Dependências que não podem ser invertidas:**
`SEC-03` antes de `SEC-02`, `SEC-04` e `BUG-01` (sem `req.user` não há como comparar) ·
`SEC-01` antes de `DEVOPS-02` e `DOC-01` (o exemplo precisa do nome da variável) ·
`SEC-06` antes de `BUG-03` (senão o `500` por índice único continua) ·
`BUG-02` antes de `SEC-05` (o rate limit conta tentativas válidas).

---

## 9. Fora de escopo / riscos

| Item | Decisão | Motivo |
|---|---|---|
| Trocar JWT por sessão com cookie | **Não** | O JWT HS512 com `algorithms` fixado está correto. O defeito é autorização, não formato de token. |
| Remover o diretório `java/` | **Não** | É o código-fonte de origem preservado por decisão (commit `9620d7d`). Ver `DEVOPS-03`. |
| Migrar Mongo → SQL | **Não** | Fora do escopo. O modelo de documentos cabe em carteira de ações. |
| Adicionar papéis (`role`) e admin API | **Não, ainda** | Só faça depois de fechar os P0 de dono; expor admin antes disso amplia o ataque. |
| Refatorar controllers em camadas (service/repository) | **Não** | Redesign de arquitetura sem benefício de segurança imediato. |

**Riscos desta execução:**

- **`SEC-01` invalida todos os tokens existentes.** Ao trocar o segredo, cada usuário precisará
  relogar. Comunique antes; não faça fallback temporário (é o que criou o problema).
- **`SEC-02`/`SEC-04`/`BUG-01` podem quebrar uso legítimo de admin.** Se algum cliente já depende
  de listar/editar terceiros, isso vai parar — **é o objetivo**, mas mapear os usos reais antes.
- **`BUG-03` muda o shape de erro** (de timeout para JSON). O front deve estar preparado para
  receber `503` em vez de pendurar.
- **`SEC-05` pode bloquear usuário real** por engano se o limite for baixo demais num NAT (IPs
  compartilhados). Use o limite **por e-mail** como primário e por IP como secundário e frouxo.

---

## 10. Definição de pronto (DoD)

**Segurança**
- [ ] `SEC-01` — sem literal de segredo; API não inicia sem `JWT_SECRET`; segredo rotacionado
- [ ] `SEC-03` — `req.user.id`/`req.user.email` disponíveis em todo handler
- [ ] `SEC-02` — editar/deletar terceiro devolve `403`
- [ ] `SEC-04` — `GET /api/client` devolve **só** o próprio usuário
- [ ] `SEC-05` — 6ª tentativa de login em 5 min devolve `429`
- [ ] `SEC-06` — campos fora da allowlist são ignorados
- [ ] `SEC-07` — CORS com allowlist explícita, sem origem aberta
- [ ] `SEC-08` — headers do `helmet` presentes nas respostas

**Funcional**
- [ ] `BUG-01` — ler/editar carteira de terceiro devolve `403`
- [ ] `BUG-02` — e-mail inválido ou senha < 8 devolve `400`
- [ ] `BUG-03` — falha do Mongo devolve `503` com log, não timeout

**Testes**
- [ ] `TEST-01` — 6 casos de autorização no `npm test`, e falham se a checagem sair
- [ ] `TEST-02` — 5 casos de token (forjado/expirado/`none`/adulterado/segredo ausente)

**Qualidade e infra**
- [ ] `IMP-01` — filtro de saída em toda rota de leitura
- [ ] `IMP-02` — contrato de resposta uniforme
- [ ] `DEVOPS-01` — Dockerfile + CI verdes
- [ ] `DEVOPS-02` — `.env.example` completo
- [ ] `DEVOPS-03` — `target/` ignorado; `java/` preservado e documentado
- [ ] `DOC-01` — README com variáveis e exemplo de uso
- [ ] `DOC-02` — `SECURITY.md` com a invariante de autorização

**Validação final:**
```bash
node --check server.js && npm test
curl -s -o /dev/null -w '%{http_code}\n' -X DELETE http://localhost:3000/api/client/$ID_B \
  -H "Authorization: Bearer $TOKEN_A"      # 403
```

---

*Fim do plano. Gerado por leitura direta do código em 2026-10-02. Nenhum item já estava corrigido*
*— todos apontam para defeitos ainda presentes.*
