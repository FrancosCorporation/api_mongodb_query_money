# API MongoDB Query Money

![Node](https://img.shields.io/badge/Node-Express-green?logo=node.js&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-Mongoose-47A248?logo=mongodb)
![JWT](https://img.shields.io/badge/auth-JWT%20HS512-red)
![License](https://img.shields.io/badge/license-MIT-green)

API REST para carteira de ações. A implementação ativa usa **Node.js, Express e Mongoose**;
o código Spring Boot de 2022 foi preservado em [`java/`](java/) como referência. Edite `src/`
e `server.js` para alterar a API em uso.

## Endpoints

| Método | Rota | Autenticação | Descrição |
|---|---|---|---|
| POST | `/api/client/register` | pública | cria cliente com senha BCrypt |
| POST | `/api/client/login` | pública | devolve JWT HS512 válido por 24 horas |
| GET | `/api/client` | Bearer | devolve somente o cliente do token |
| GET/PUT/DELETE | `/api/client/{id}` | Bearer | consulta, altera ou remove o próprio cliente |
| POST | `/api/wallet/register` | Bearer | cria ou mescla a carteira do próprio cliente |
| GET | `/api/wallet` · `/api/wallet/all` | Bearer | devolve somente a carteira do token |
| GET | `/api/wallet/{id}` | Bearer | consulta a carteira do próprio cliente |
| GET | `/api/actionnames` | Bearer | lista os nomes de ações cadastrados |

Para rotas protegidas, envie `Authorization: Bearer <token>`. Um cliente não pode consultar,
alterar ou remover o registro ou a carteira de outro cliente; IDs enviados no corpo não trocam o
dono da operação.

## Configuração

Copie `.env.example` para `.env` e gere um segredo exclusivo para cada ambiente:

```bash
cp .env.example .env
openssl rand -hex 32
```

Cole o resultado em `JWT_SECRET`. A API recusa um segredo ausente ou com menos de 32 bytes. O
segredo histórico publicado no repositório deve ser considerado comprometido: configure um novo
valor no ambiente de produção; isso invalida os tokens anteriores.

Variáveis disponíveis:

| Variável | Uso |
|---|---|
| `JWT_SECRET` | obrigatório; pelo menos 32 bytes |
| `MONGO_URI` | conexão MongoDB; localmente usa `mongodb://localhost:27017/query_money` |
| `PORT` | porta HTTP; padrão `3000` |
| `ALLOWED_ORIGINS` | origens web exatas, separadas por vírgula; vazio bloqueia chamadas cross-origin em navegadores |
| `TRUST_PROXY` | IPs ou sub-redes exatos dos proxies confiáveis, separados por vírgula |

`TRUST_PROXY` fica vazio por padrão. Configure-o somente com os endereços reais dos proxies que
encaminham pedidos à API. Não use `true` nem confie em todo o cabeçalho `X-Forwarded-For`.

## Executar

**Com Docker Compose:** o arquivo `.env` é carregado pelo Compose. A API fica acessível em
`http://localhost:3000` na máquina local; o MongoDB não publica uma porta no host.

```bash
docker compose up --build
```

**Localmente:** requer Node.js 18+ e MongoDB disponível em `MONGO_URI`.

```bash
npm ci
set -a
. ./.env
set +a
npm start
```

O login aceita no máximo cinco tentativas inválidas por e-mail e trinta por IP em uma janela de
cinco minutos. Os contadores são mantidos na memória de cada processo e reiniciam quando ele
reinicia; instalações com várias instâncias precisam de um armazenamento compartilhado para
aplicar o mesmo limite entre elas.

Para servir uma interface em outro domínio, informe a origem exata em `ALLOWED_ORIGINS`, por
exemplo `https://app.exemplo.com`. Configure TLS no proxy de borda. A API não ativa HSTS no
transporte HTTP local.

## Fidelidade ao Spring Boot original

| Original (Spring Boot) | Implementação Node |
|---|---|
| Rotas `/api/...` | rotas Express correspondentes |
| Bearer JWT HS512, 24h | middleware `exigirToken` |
| `BCryptPasswordEncoder` | `bcryptjs`, custo 10 |
| Filtragem de ações e mesclagem da carteira | controller de wallet |

## Estrutura

```text
server.js                 Express, rotas, cabeçalhos, CORS e conexão Mongo
src/models/index.js       schemas Mongoose
src/middleware/           JWT e limite de tentativas de login
src/controllers/index.js  regras de clientes e carteiras
test/                     testes de integração existentes
Dockerfile                imagem Node sem dependências de desenvolvimento
docker-compose.yml        MongoDB e API
java/                     fontes Java preservadas como referência histórica
```

## Segurança

Consulte [SECURITY.md](SECURITY.md) para reporte de vulnerabilidades e invariantes de autorização.

## Licença

MIT — veja [LICENSE](LICENSE).
