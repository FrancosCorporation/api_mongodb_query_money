import { randomUUID } from 'node:crypto';
import bcrypt from 'bcryptjs';
import { Client, Wallet, ActionName } from '../models/index.js';
import { gerarToken } from '../middleware/auth.js';

const semSenha = ({ password, ...c }) => c;

// ---------- /api/client (Controller_client + Services_Clients) ----------
export async function listarClientes(req, res) {
  const clientes = await Client.find().lean();
  res.json(clientes.map(semSenha));
}

export async function clientePorId(req, res) {
  const cliente = await Client.findOne({ id: req.params.id }).lean();
  if (!cliente) return res.status(404).json({ message: 'Cliente não encontrado' });
  res.json(semSenha(cliente));
}

export async function login(req, res) {
  const { email, password } = req.body;
  const cliente = await Client.findOne({ email }).lean();
  if (!cliente || !bcrypt.compareSync(password, cliente.password)) {
    return res.status(401).json({ message: 'Credenciais inválidas' });
  }
  res.json({ token: gerarToken(cliente.email, cliente.id), client: semSenha(cliente) });
}

export async function registrar(req, res) {
  const { name, email, password } = req.body;
  if (!name || !email || !password) {
    return res.status(400).json({ message: 'name, email e password são obrigatórios' });
  }
  if (await Client.findOne({ email })) {
    return res.status(409).json({ message: 'E-mail já cadastrado' });
  }
  const cliente = await Client.create({
    id: randomUUID(),
    name,
    email,
    password: bcrypt.hashSync(password, 10) // BCryptPasswordEncoder.encode()
  });
  res.status(201).json(semSenha(cliente.toObject()));
}

export async function modificarCliente(req, res) {
  const alteracoes = { ...req.body, changeDate: new Date() };
  if (alteracoes.password) alteracoes.password = bcrypt.hashSync(alteracoes.password, 10);
  delete alteracoes.id;
  const cliente = await Client.findOneAndUpdate({ id: req.params.id }, alteracoes, { new: true }).lean();
  if (!cliente) return res.status(404).json({ message: 'Cliente não encontrado' });
  res.json(semSenha(cliente));
}

export async function removerCliente(req, res) {
  const cliente = await Client.findOneAndDelete({ id: req.params.id }).lean();
  if (!cliente) return res.status(404).json({ message: 'Cliente não encontrado' });
  res.json({ message: 'Cliente removido' });
}

// ---------- /api/wallet (Controller_layout_client + Services_layout_data_client) ----------
export async function listarCarteiras(req, res) {
  res.json(await Wallet.find().lean());
}

export async function carteiraPorId(req, res) {
  const carteira = await Wallet.findOne({ id: req.params.id }).lean();
  if (!carteira) return res.status(404).json({ message: 'Carteira não encontrada' });
  res.json(carteira);
}

// POST /api/wallet/register — port do CreateNewWallet:
// valida cliente; mantém só ações que existem em ActionName; faz merge se já houver carteira
export async function registrarCarteira(req, res) {
  const { id, actionsAndPrice } = req.body;
  const cliente = await Client.findOne({ id });
  if (!cliente) return res.status(404).json({ message: 'Cliente não encontrado' });

  const nomesExistentes = new Set((await ActionName.find().lean()).map((a) => a.nameAction));
  const validas = (actionsAndPrice || []).filter((a) => nomesExistentes.has(a.nameAction));
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
  res.json(await ActionName.find().lean());
}
