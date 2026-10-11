import { createHash } from 'node:crypto';

const JANELA_MS = 5 * 60 * 1000;
const MAX_FALHAS_EMAIL = 5;
const MAX_FALHAS_IP = 30;
const MAX_CHAVES = 10000;
const falhasPorEmail = new Map();
const falhasPorIp = new Map();

function chaveEmail(email) {
  return createHash('sha256').update(email).digest('hex');
}

function obterFalhas(mapa, chave, agora) {
  const registros = (mapa.get(chave) || []).filter((momento) => agora - momento < JANELA_MS);
  if (registros.length) mapa.set(chave, registros);
  else mapa.delete(chave);
  return registros;
}

function guardarFalha(mapa, chave, registros, agora) {
  mapa.set(chave, [...registros, agora]);
  if (mapa.size <= MAX_CHAVES) return;

  for (const [chaveAntiga, momentos] of mapa) {
    if (momentos.every((momento) => agora - momento >= JANELA_MS)) mapa.delete(chaveAntiga);
    if (mapa.size <= MAX_CHAVES) break;
  }
  while (mapa.size > MAX_CHAVES) mapa.delete(mapa.keys().next().value);
}

function segundosAteLiberar(registros, agora) {
  return Math.max(1, Math.ceil((registros[0] + JANELA_MS - agora) / 1000));
}

export function limitarTentativasLogin(req, res, next) {
  const emailBruto = typeof req.body?.email === 'string' ? req.body.email.trim().toLowerCase() : '';
  const email = emailBruto.length <= 254 ? emailBruto : '';
  const ip = req.ip || req.socket.remoteAddress || 'desconhecido';
  const agora = Date.now();
  const falhasEmail = email ? obterFalhas(falhasPorEmail, chaveEmail(email), agora) : [];
  const falhasIp = obterFalhas(falhasPorIp, ip, agora);

  if (falhasEmail.length >= MAX_FALHAS_EMAIL || falhasIp.length >= MAX_FALHAS_IP) {
    const espera = Math.max(
      falhasEmail.length >= MAX_FALHAS_EMAIL ? segundosAteLiberar(falhasEmail, agora) : 0,
      falhasIp.length >= MAX_FALHAS_IP ? segundosAteLiberar(falhasIp, agora) : 0
    );
    res.set('Retry-After', String(espera));
    return res.status(429).json({ message: 'Muitas tentativas de login. Tente novamente mais tarde.' });
  }

  req.loginRateLimit = { email, emailKey: email ? chaveEmail(email) : null, ip };
  return next();
}

export function registrarResultadoLogin(req, sucesso) {
  const contexto = req.loginRateLimit;
  if (!contexto) return;

  const agora = Date.now();
  if (sucesso) {
    if (contexto.emailKey) falhasPorEmail.delete(contexto.emailKey);
  } else {
    if (contexto.emailKey) {
      guardarFalha(
        falhasPorEmail,
        contexto.emailKey,
        obterFalhas(falhasPorEmail, contexto.emailKey, agora),
        agora
      );
    }
    guardarFalha(falhasPorIp, contexto.ip, obterFalhas(falhasPorIp, contexto.ip, agora), agora);
  }

  console.info(JSON.stringify({
    evento: 'tentativa_login',
    ip: contexto.ip,
    email: contexto.email || null,
    resultado: sucesso ? 'sucesso' : 'falha'
  }));
}
