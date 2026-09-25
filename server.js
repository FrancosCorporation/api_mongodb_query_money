// API MongoDB Query Money — port Node/Express do projeto Spring Boot (2022).
// Clientes com JWT (HS512/24h) + BCrypt, carteira de ações com validação de nomes.
import express from 'express';
import mongoose from 'mongoose';
import { listarClientes, clientePorId, login, registrar, modificarCliente, removerCliente,
         listarCarteiras, carteiraPorId, registrarCarteira, listarNomes } from './src/controllers/index.js';
import { exigirToken } from './src/middleware/auth.js';

export function criarApp() {
  const app = express();
  app.use(express.json());

  // Rotas públicas (Auth_config.java: register/login)
  app.post('/api/client/register', registrar);
  app.post('/api/client/login', login);

  // Rotas protegidas por Bearer (Auth_filter)
  app.get('/api/client', exigirToken, listarClientes);
  app.get('/api/client/:id', exigirToken, clientePorId);
  app.put('/api/client/:id', exigirToken, modificarCliente);
  app.delete('/api/client/:id', exigirToken, removerCliente);
  app.get('/api/wallet', exigirToken, listarCarteiras);
  app.get('/api/wallet/all', exigirToken, listarCarteiras);
  app.get('/api/wallet/:id', exigirToken, carteiraPorId);
  app.post('/api/wallet/register', exigirToken, registrarCarteira);
  app.get('/api/actionnames', exigirToken, listarNomes);

  app.get('/', (req, res) => res.json({ api: 'api_mongodb_query_money', status: 'online' }));
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
      console.error('Falha ao conectar no MongoDB:', erro.message);
      process.exit(1);
    });
}
