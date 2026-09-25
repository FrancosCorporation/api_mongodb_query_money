# API MongoDB Query Money

![Node](https://img.shields.io/badge/Node-Express-green?logo=node.js&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-Mongoose-47A248?logo=mongodb&logoColor=white)
![JWT](https://img.shields.io/badge/auth-JWT%20HS512-red)
![Tests](https://img.shields.io/badge/testes-13%2F13%20passando-brightgreen)
![License](https://img.shields.io/badge/license-MIT-green)

API REST de **carteira de ações** — port **Node/Express + Mongoose** do projeto
**Spring Boot** de 2022, com os 29 fontes Java originais preservados em [`java/`](java/).

## Endpoints

| Método | Rota | Auth | Descrição |
|---|---|---|---|
| POST | `/api/client/register` | pública | cria cliente (BCrypt) |
| POST | `/api/client/login` | pública | devolve **JWT HS512** (24h) |
| GET | `/api/client` | Bearer | lista clientes (sem senhas) |
| GET/PUT/DELETE | `/api/client/{id}` | Bearer | busca / altera / remove cliente |
| POST | `/api/wallet/register` | Bearer | cria/faz **merge** da carteira (só ações existentes em `ActionName`) |
| GET | `/api/wallet` · `/api/wallet/all` | Bearer | lista carteiras |
| GET | `/api/wallet/{id}` | Bearer | carteira do cliente |
| GET | `/api/actionnames` | Bearer | nomes de ações cadastrados |

## Instalação e execução

**Com Docker (recomendado — sobe Mongo + API):**
```bash
docker compose up
# API em http://localhost:3000
```

**Local (requer MongoDB em localhost:27017):**
```bash
npm install
npm start
```

**Testes (Mongo em memória, sem Docker):**
```bash
npm test   # 13 testes de integração HTTP reais
```

Variáveis de ambiente: `MONGO_URI` (default `mongodb://localhost:27017/query_money`),
`JWT_SECRET` (default = o secret histórico do projeto — troque em produção), `PORT` (3000).

## Fidelidade ao Spring Boot original

| Original (Spring Boot) | Port (Node) |
|---|---|
| `@RestController` + `@RequestMapping("/api/...")` | rotas Express idênticas |
| Spring Security `Auth_config`/`Auth_filter` (Bearer) | middleware `exigirToken` |
| `Auth_token`: JWT HS512, expiração 86400000 ms, subject=e-mail, jwtid=id | `jsonwebtoken` igualzinho |
| `BCryptPasswordEncoder` | `bcryptjs` (hash custo 10) |
| `Services_layout_data_client.returnNameActionsExist` (filtra ações) + merge de carteira | mesma regra no controller de wallet |
| SpringFox/Swagger | endpoints na tabela acima (README como documentação) |

## Estrutura

```
api_mongodb_query_money/
├── server.js                 # app Express + rotas + conexão Mongo
├── src/
│   ├── models/index.js       # Client, ActionName, Wallet, DataB3 (Mongoose)
│   ├── middleware/auth.js    # JWT HS512 (geração + verificação Bearer)
│   └── controllers/index.js  # regras dos 3 controllers originais
├── test/api-test.mjs         # 13 testes de integração (Mongo em memória)
├── docker-compose.yml        # mongo + api prontos
└── java/                     # ✔ PROJETO ORIGINAL 2022 preservado (29 fontes)
    └── main/java/com/api_mongo/api_mongodb_query_money/
```

### Rodar a versão Java original (histórica)

```bash
cd java
mvn spring-boot:run   # requer JDK 17+ e Maven
```

## Licença

MIT — veja [LICENSE](LICENSE).
