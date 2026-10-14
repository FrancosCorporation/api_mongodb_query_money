import jwt from 'jsonwebtoken';

// HMAC512, expiração 24h. Sem fallback: cada ambiente precisa de uma chave própria.
const TIME_EXPIRATION = 86400000;
const TOKEN_SENHA = process.env.JWT_SECRET;
if (!TOKEN_SENHA || Buffer.byteLength(TOKEN_SENHA, 'utf8') < 32) {
  throw new Error('JWT_SECRET não definido ou curto; configure pelo menos 32 bytes antes de iniciar.');
}

export function gerarToken(email, id) {
  return jwt.sign({}, TOKEN_SENHA, {
    algorithm: 'HS512',
    subject: email,
    jwtid: id,
    expiresIn: TIME_EXPIRATION / 1000
  });
}

export function exigirToken(req, res, next) {
  const cabecalho = req.headers.authorization;
  if (!cabecalho || !cabecalho.startsWith('Bearer ')) {
    return res.status(401).json({ message: 'Token ausente ou inválido' });
  }

  try {
    // jsonwebtoken grava a opção `jwtid` como claim `jti` — a verificação
    // precisa ler o nome do claim, não o da opção (bug corrigido 2026-10-14).
    const payload = jwt.verify(cabecalho.slice(7), TOKEN_SENHA, { algorithms: ['HS512'] });
    if (!payload || typeof payload !== 'object' || typeof payload.jti !== 'string'
        || !payload.jti.trim() || typeof payload.sub !== 'string' || !payload.sub.trim()) {
      return res.status(401).json({ message: 'Token ausente ou inválido' });
    }
    req.user = { id: payload.jti, email: payload.sub };
    return next();
  } catch {
    return res.status(401).json({ message: 'Token ausente ou inválido' });
  }
}
