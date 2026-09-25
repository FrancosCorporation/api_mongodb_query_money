// Testes de integração da API — Mongo em memória + HTTP real (sem Docker).
// Uso: npm test  (baixa um binário do mongod na primeira execução)
import { MongoMemoryServer } from 'mongodb-memory-server';
import mongoose from 'mongoose';
import { criarApp } from '../server.js';
import { ActionName } from '../src/models/index.js';
import { randomUUID } from 'node:crypto';

let falhas = 0;
function check(nome, cond) {
  console.log(`${cond ? 'PASS' : 'FAIL'} - ${nome}`);
  if (!cond) falhas++;
}

const mongod = await MongoMemoryServer.create();
await mongoose.connect(mongod.getUri('query_money_test'));
const app = criarApp();
const server = app.listen(0);
const base = `http://localhost:${server.address().port}`;
const chamar = (metodo, rota, corpo, token) =>
  fetch(base + rota, {
    method: metodo,
    headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: `Bearer ${token}` } : {}) },
    body: corpo ? JSON.stringify(corpo) : undefined
  }).then(async (r) => ({ status: r.status, corpo: await r.json().catch(() => null) }));

// semeia ações válidas (como o Service espera na coleção ActionName)
await ActionName.create([
  { id: randomUUID(), nameAction: 'PETR4' },
  { id: randomUUID(), nameAction: 'VALE3' }
]);

// 1) registro de cliente (rota pública)
const novo = { name: 'Rodolfo', email: 'rodolfo@teste.dev', password: 'senha123' };
const r1 = await chamar('POST', '/api/client/register', novo);
check('register cria cliente (201)', r1.status === 201 && r1.corpo.id);
check('register não devolve a senha', r1.corpo.password === undefined);
const clienteId = r1.corpo.id;

// 2) e-mail duplicado (409)
const r2 = await chamar('POST', '/api/client/register', novo);
check('register com e-mail repetido devolve 409', r2.status === 409);

// 3) login correto devolve token
const r3 = await chamar('POST', '/api/client/login', { email: novo.email, password: novo.password });
check('login devolve token (JWT HS512)', r3.status === 200 && typeof r3.corpo.token === 'string');
const token = r3.corpo.token;

// 4) login errado (401)
const r4 = await chamar('POST', '/api/client/login', { email: novo.email, password: 'errada' });
check('login com senha errada devolve 401', r4.status === 401);

// 5) rota protegida sem token (401)
const r5 = await chamar('GET', '/api/client');
check('GET /api/client sem token devolve 401', r5.status === 401);

// 6) rota protegida com token (200) e lista o cliente criado
const r6 = await chamar('GET', '/api/client', null, token);
check('GET /api/client com token lista o cliente', r6.status === 200 && r6.corpo.some((c) => c.id === clienteId));

// 7) carteira: ação inexistente é recusada
const r7 = await chamar('POST', '/api/wallet/register',
  { id: clienteId, actionsAndPrice: [{ nameAction: 'NAO_EXISTE', dateBuy: '2026-09-25', priceBuy: '10', quantity: '2' }] }, token);
check('wallet rejeita ação inexistente (400)', r7.status === 400);

// 8) carteira: ação válida é criada (201)
const r8 = await chamar('POST', '/api/wallet/register',
  { id: clienteId, actionsAndPrice: [{ nameAction: 'PETR4', dateBuy: '2026-09-25', priceBuy: '38,50', quantity: '100' }] }, token);
check('wallet registra ação válida (201)', r8.status === 201 && r8.corpo.actionsAndPrice.length === 1);

// 9) merge: segunda ação adiciona sem duplicar carteira
const r9 = await chamar('POST', '/api/wallet/register',
  { id: clienteId, actionsAndPrice: [{ nameAction: 'VALE3', dateBuy: '2026-09-25', priceBuy: '60,00', quantity: '10' }] }, token);
check('wallet faz merge das ações (2 itens)', r9.status === 200 && r9.corpo.actionsAndPrice.length === 2);

// 10) GET /api/wallet/:id retorna a carteira
const r10 = await chamar('GET', `/api/wallet/${clienteId}`, null, token);
check('GET /api/wallet/:id devolve carteira com 2 ações', r10.status === 200 && r10.corpo.actionsAndPrice.length === 2);

// 11) actionnames lista as ações semeadas
const r11 = await chamar('GET', '/api/actionnames', null, token);
check('GET /api/actionnames lista PETR4 e VALE3', r11.status === 200 && r11.corpo.length === 2);

// 12) delete do cliente
const r12 = await chamar('DELETE', `/api/client/${clienteId}`, null, token);
check('DELETE remove o cliente', r12.status === 200);

server.close();
await mongoose.disconnect();
await mongod.stop();

console.log(falhas === 0 ? '\nTODOS OS TESTES PASSARAM ✔ (API real + Mongo em memória)' : `\n${falhas} TESTE(S) FALHARAM ✘`);
process.exit(falhas === 0 ? 0 : 1);
