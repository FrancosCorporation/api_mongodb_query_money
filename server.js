// API MongoDB Query Money — port Node/Express do projeto Spring Boot (2022).
// Clientes com JWT (HS512/24h) + BCrypt, carteira de ações com validação de nomes.
import express from 'express';
import helmet from 'helmet';
import mongoose from 'mongoose';
import { listarClientes, clientePorId, login, registrar, modificarCliente, removerCliente,
         listarCarteiras, carteiraPorId, registrarCarteira, listarNomes } from './src/controllers/index.js';
import { exigirToken } from './src/middleware/auth.js';
import { limitarTentativasLogin } from './src/middleware/login-rate-limit.js';

const METODOS_CORS = new Set(['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS']);
const CABECALHOS_CORS = new Set(['authorization', 'content-type']);

function origensPermitidas() {
  return new Set((process.env.ALLOWED_ORIGINS || '').split(',').map((valor) => valor.trim())
    .filter(Boolean).map((valor) => {
      let origem;
      try {
        origem = new URL(valor);
      } catch {
        throw new Error(`ALLOWED_ORIGINS contém uma origem inválida: ${valor}`);
      }
      if (!['http:', 'https:'].includes(origem.protocol) || origem.username || origem.password
          || origem.pathname !== '/' || origem.search || origem.hash) {
        throw new Error(`ALLOWED_ORIGINS contém uma origem inválida: ${valor}`);
      }
      return origem.origin;
    }));
}

function corsComAllowlist(permitidas) {
  return (req, res, next) => {
    const origem = req.get('Origin');
    if (origem) res.vary('Origin');
    const autorizada = origem && permitidas.has(origem);

    if (autorizada) {
      res.set('Access-Control-Allow-Origin', origem);
      res.set('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
      res.set('Access-Control-Allow-Headers', 'Authorization, Content-Type');
      res.set('Access-Control-Max-Age', '600');
    }

    if (req.method === 'OPTIONS') {
      if (origem && !autorizada) {
        return res.status(403).json({ message: 'Origem não permitida' });
      }
      const metodo = req.get('Access-Control-Request-Method');
      const cabecalhos = (req.get('Access-Control-Request-Headers') || '').split(',')
        .map((valor) => valor.trim().toLowerCase()).filter(Boolean);
      if ((metodo && !METODOS_CORS.has(metodo.toUpperCase()))
          || cabecalhos.some((cabecalho) => !CABECALHOS_CORS.has(cabecalho))) {
        return res.status(403).json({ message: 'Preflight não permitido' });
      }
      return res.status(204).end();
    }

    return next();
  };
}

function lidarAsync(handler) {
  return (req, res, next) => Promise.resolve(handler(req, res, next)).catch(next);
}

function lidarErroApi(erro, req, res, next) {
  if (res.headersSent) return next(erro);

  let status = 500;
  let mensagem = 'Erro interno do servidor';
  const erroJson = erro?.type === 'entity.parse.failed'
    || (erro instanceof SyntaxError && Object.hasOwn(erro, 'body'));

  if (erroJson || erro?.name === 'ValidationError' || erro?.name === 'CastError') {
    status = 400;
    mensagem = 'Requisição inválida';
  } else if (erro?.code === 11000) {
    status = 409;
    mensagem = 'Registro já existe';
  } else if (mongoose.connection.readyState !== 1
      || ['MongoServerSelectionError', 'MongoNetworkError', 'MongoTopologyClosedError']
        .includes(erro?.name)
      || (typeof erro?.code === 'string' && erro.code.startsWith('ECONN'))) {
    status = 503;
    mensagem = 'Banco de dados temporariamente indisponível';
  }

  console.error(JSON.stringify({
    evento: 'erro_api',
    metodo: req.method,
    rota: req.route?.path || req.path,
    status,
    nome: erro?.name || 'Error',
    codigo: erro?.code ?? null
  }));
  return res.status(status).json({ message: mensagem });
}

export function criarApp() {
  const app = express();
  const permitidas = origensPermitidas();
  const proxiesConfiaveis = (process.env.TRUST_PROXY || '').split(',').map((valor) => valor.trim())
    .filter(Boolean);
  if (proxiesConfiaveis.length) app.set('trust proxy', proxiesConfiaveis);
  app.disable('x-powered-by');
  app.use(helmet({
    contentSecurityPolicy: { directives: { 'upgrade-insecure-requests': null } },
    strictTransportSecurity: false
  }));
  app.use(express.json({ limit: '1mb' }));
  app.use(corsComAllowlist(permitidas));

  // Rotas públicas (Auth_config.java: register/login).
  app.post('/api/client/register', lidarAsync(registrar));
  app.post('/api/client/login', limitarTentativasLogin, lidarAsync(login));

  // Rotas protegidas por Bearer (Auth_filter).
  app.get('/api/client', exigirToken, lidarAsync(listarClientes));
  app.get('/api/client/:id', exigirToken, lidarAsync(clientePorId));
  app.put('/api/client/:id', exigirToken, lidarAsync(modificarCliente));
  app.delete('/api/client/:id', exigirToken, lidarAsync(removerCliente));
  app.get('/api/wallet', exigirToken, lidarAsync(listarCarteiras));
  app.get('/api/wallet/all', exigirToken, lidarAsync(listarCarteiras));
  app.get('/api/wallet/:id', exigirToken, lidarAsync(carteiraPorId));
  app.post('/api/wallet/register', exigirToken, lidarAsync(registrarCarteira));
  app.get('/api/actionnames', exigirToken, lidarAsync(listarNomes));

  app.get('/', (req, res) => res.json({ api: 'api_mongodb_query_money', status: 'online' }));
  app.use((req, res) => res.status(404).json({ message: 'Rota não encontrada' }));
  app.use(lidarErroApi);
  return app;
}

const MONGO_URI = process.env.MONGO_URI || 'mongodb://localhost:27017/query_money';

if (process.env.NODE_ENV !== 'test') {
  mongoose.connect(MONGO_URI)
    .then(() => {
      const porta = process.env.PORT || 3000;
      criarApp().listen(porta, () =>
        console.log(`API MongoDB Query Money em http://localhost:${porta} (Mongo conectado)`));
    })
    .catch((erro) => {
      console.error(JSON.stringify({
        evento: 'falha_conexao_mongodb',
        nome: erro?.name || 'Error',
        codigo: erro?.code ?? null
      }));
      process.exit(1);
    });
}
