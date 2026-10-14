// Bootstrap da suíte de integração da API.
// Define um segredo JWT EFÊMERO apenas para o processo de testes
// (auth.js falha no import sem JWT_SECRET ≥ 32 bytes) e delega à suíte real.
// Nenhum valor é persistido ou commitado.
import { randomBytes } from 'node:crypto';

process.env.NODE_ENV = 'test';
if (!process.env.JWT_SECRET || Buffer.byteLength(process.env.JWT_SECRET, 'utf8') < 32) {
  process.env.JWT_SECRET = randomBytes(48).toString('hex');
}

await import('./api-test.mjs');
