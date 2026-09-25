import jwt from 'jsonwebtoken';

// Port de Auth_token.java/Auth_valid.java: HMAC512, expiração 24h (86400000 ms).
// O secret do original era fixo no código; aqui via env com fallback documentado.
const TIME_EXPIRATION = 86400000;
const TOKEN_SENHA = process.env.JWT_SECRET || '6d79be3b-d3e7-461b-9803-0ee869959df6';

export function gerarToken(email, id) {
  return jwt.sign({}, TOKEN_SENHA, {
    algorithm: 'HS512',
    subject: email,
    jwtid: id,
    expiresIn: TIME_EXPIRATION / 1000
  });
}

// Middleware do Auth_filter: Bearer obrigatório, exceto rotas públicas do Auth_config
export function exigirToken(req, res, next) {
  const cabecalho = req.headers.authorization;
  if (!cabecalho || !cabecalho.startsWith('Bearer ')) {
    return res.status(401).json({ message: 'Token ausente ou inválido' });
  }
  try {
    jwt.verify(cabecalho.slice(7), TOKEN_SENHA, { algorithms: ['HS512'] });
    next();
  } catch {
    return res.status(401).json({ message: 'Token ausente ou inválido' });
  }
}
