import { randomUUID } from 'node:crypto';
import bcrypt from 'bcryptjs';
import { Client, Wallet, ActionName } from '../models/index.js';
import { gerarToken } from '../middleware/auth.js';
import { registrarResultadoLogin } from '../middleware/login-rate-limit.js';

const semSenha = ({ id, name, email, dataCreate, changeDate }) => ({
  id, name, email, dataCreate, changeDate
});
const formatoEmail = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;

function emailValido(email) {
  return typeof email === 'string' && email.length <= 254 && formatoEmail.test(email.trim());
}

function emailComoFiltro(email) {
  const escapado = email.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  return new RegExp(`^${escapado}$`, 'i');
}

function senhaValida(password) {
  return typeof password === 'string'
    && password.length >= 8
    && Buffer.byteLength(password, 'utf8') <= 72;
}

function exigirProprioCliente(req, res) {
  if (req.params.id !== req.user?.id) {
    res.status(403).json({ message: 'Acesso negado: o recurso não pertence a você' });
    return false;
  }
  return true;
}

// ---------- /api/client (Controller_client + Services_Clients) ----------
export async function listarClientes(req, res) {
  const clientes = await Client.find({ id: req.user.id }).lean();
  res.json(clientes.map(semSenha));
}

export async function clientePorId(req, res) {
  if (!exigirProprioCliente(req, res)) return;
  const cliente = await Client.findOne({ id: req.params.id }).lean();
  if (!cliente) return res.status(404).json({ message: 'Cliente não encontrado' });
  res.json(semSenha(cliente));
}

export async function login(req, res) {
  const email = typeof req.body?.email === 'string' ? req.body.email.trim() : '';
  const password = req.body?.password;
  const cliente = email && email.length <= 254
    ? await Client.findOne({ email: emailComoFiltro(email) }).lean()
    : null;
  const autenticado = cliente && typeof password === 'string'
    ? await bcrypt.compare(password, cliente.password)
    : false;
  if (!autenticado) {
    registrarResultadoLogin(req, false);
    return res.status(401).json({ message: 'Credenciais inválidas' });
  }
  registrarResultadoLogin(req, true);
  res.json({ token: gerarToken(cliente.email, cliente.id), client: semSenha(cliente) });
}

export async function registrar(req, res) {
  const { name, email, password } = req.body || {};
  if (typeof name !== 'string' || !name.trim() || name.trim().length > 100
      || !emailValido(email) || !senhaValida(password)) {
    return res.status(400).json({ message: 'Informe nome (até 100 caracteres), e-mail válido e senha de 8 a 72 bytes' });
  }
  const emailNormalizado = email.trim().toLowerCase();
  if (await Client.findOne({ email: emailComoFiltro(emailNormalizado) })) {
    return res.status(409).json({ message: 'E-mail já cadastrado' });
  }
  const cliente = await Client.create({
    id: randomUUID(),
    name: name.trim(),
    email: emailNormalizado,
    password: await bcrypt.hash(password, 10) // BCryptPasswordEncoder.encode()
  });
  res.status(201).json(semSenha(cliente.toObject()));
}

export async function modificarCliente(req, res) {
  if (!exigirProprioCliente(req, res)) return;
  const alteracoes = {};
  const corpo = req.body || {};
  if (typeof corpo !== 'object' || Array.isArray(corpo)) {
    return res.status(400).json({ message: 'O corpo deve ser um objeto JSON' });
  }

  if ('name' in corpo) {
    if (typeof corpo.name !== 'string' || !corpo.name.trim() || corpo.name.trim().length > 100)
      return res.status(400).json({ message: 'name deve ter entre 1 e 100 caracteres' });
    alteracoes.name = corpo.name.trim();
  }
  if ('email' in corpo) {
    if (!emailValido(corpo.email)) return res.status(400).json({ message: 'E-mail inválido' });
    alteracoes.email = corpo.email.trim().toLowerCase();
  }
  if ('password' in corpo) {
    if (!senhaValida(corpo.password))
      return res.status(400).json({ message: 'A senha deve ter pelo menos 8 caracteres e no máximo 72 bytes' });
    alteracoes.password = await bcrypt.hash(corpo.password, 10);
  }
  if (Object.keys(alteracoes).length === 0)
    return res.status(400).json({ message: 'Informe name, email ou password para atualizar' });
  alteracoes.changeDate = new Date();
  const cliente = await Client.findOneAndUpdate({ id: req.params.id }, alteracoes, { new: true }).lean();
  if (!cliente) return res.status(404).json({ message: 'Cliente não encontrado' });
  res.json(semSenha(cliente));
}

export async function removerCliente(req, res) {
  if (!exigirProprioCliente(req, res)) return;
  const cliente = await Client.findOneAndDelete({ id: req.params.id }).lean();
  if (!cliente) return res.status(404).json({ message: 'Cliente não encontrado' });
  res.json({ message: 'Cliente removido' });
}

// ---------- /api/wallet (Controller_layout_client + Services_layout_data_client) ----------
export async function listarCarteiras(req, res) {
  res.json(await Wallet.find({ id: req.user.id }).select({ id: 1, actionsAndPrice: 1, _id: 0 }).lean());
}

export async function carteiraPorId(req, res) {
  if (!exigirProprioCliente(req, res)) return;
  const carteira = await Wallet.findOne({ id: req.params.id })
    .select({ id: 1, actionsAndPrice: 1, _id: 0 }).lean();
  if (!carteira) return res.status(404).json({ message: 'Carteira não encontrada' });
  res.json(carteira);
}

// POST /api/wallet/register — port do CreateNewWallet:
// valida cliente; mantém só ações que existem em ActionName; faz merge se já houver carteira
export async function registrarCarteira(req, res) {
  const { actionsAndPrice } = req.body || {};
  const id = req.user.id;
  if (req.body?.id && req.body.id !== id)
    return res.status(403).json({ message: 'Acesso negado: a carteira pertence a outro cliente' });
  if (!Array.isArray(actionsAndPrice) || actionsAndPrice.length > 500) {
    return res.status(400).json({ message: 'actionsAndPrice deve ser uma lista com até 500 ações' });
  }
  const camposAcao = ['dateBuy', 'priceBuy', 'quantity'];
  const acoesRecebidas = [];
  for (const acao of actionsAndPrice) {
    if (!acao || typeof acao !== 'object' || Array.isArray(acao)
        || typeof acao.nameAction !== 'string' || !acao.nameAction.trim()
        || acao.nameAction.length > 30) {
      return res.status(400).json({ message: 'Cada ação precisa ter um nameAction válido' });
    }
    const acaoSegura = { nameAction: acao.nameAction.trim() };
    for (const campo of camposAcao) {
      if (acao[campo] === undefined) continue;
      if (typeof acao[campo] !== 'string' || acao[campo].length > 100) {
        return res.status(400).json({ message: `${campo} deve ser um texto de até 100 caracteres` });
      }
      acaoSegura[campo] = acao[campo];
    }
    acoesRecebidas.push(acaoSegura);
  }
  const cliente = await Client.findOne({ id });
  if (!cliente) return res.status(404).json({ message: 'Cliente não encontrado' });

  const nomesExistentes = new Set((await ActionName.find().select({ nameAction: 1, _id: 0 }).lean())
    .map((acao) => acao.nameAction));
  const validas = acoesRecebidas.filter((acao) => nomesExistentes.has(acao.nameAction));
  if (validas.length === 0) {
    return res.status(400).json({ message: 'Nenhuma das ações existe na base (ActionName)' });
  }

  const existente = await Wallet.findOne({ id }).lean();
  if (existente) {
    const atuais = new Map(existente.actionsAndPrice.map((a) => [a.nameAction, a]));
    for (const a of validas) atuais.set(a.nameAction, a); // merge: novas sobrescrevem antigas
    const atualizada = await Wallet.findOneAndUpdate(
      { id }, { actionsAndPrice: [...atuais.values()] }, { new: true }
    ).lean();
    return res.json(atualizada);
  }

  res.status(201).json(await Wallet.create({ id, actionsAndPrice: validas }).then((c) => c.toObject()));
}

// ---------- /api/actionnames (Controller_list_names) ----------
export async function listarNomes(req, res) {
  res.json(await ActionName.find().select({ id: 1, nameAction: 1, _id: 0 }).lean());
}
