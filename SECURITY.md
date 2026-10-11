# Política de segurança

## Reportar uma vulnerabilidade

Não publique detalhes de uma falha explorável em uma issue pública. Use um aviso privado de
segurança do GitHub quando estiver disponível para o repositório. Caso contrário, contate os
mantenedores pelos canais oficiais do projeto e envie os passos de reprodução, o impacto e uma
forma segura de contato. Não inclua senhas, tokens ou dados pessoais de usuários na comunicação.

## Limites e proteções

Esta API recebe pedidos HTTP autenticados por JWT HS512, acessa dados de clientes e carteiras no
MongoDB e aplica um limite em memória para tentativas inválidas de login. O segredo JWT deve ser
exclusivo por ambiente, mantido fora do Git e configurado com pelo menos 32 bytes. O tráfego de
produção deve usar TLS na borda. O limitador de login é local ao processo; várias instâncias
precisam de armazenamento compartilhado para contar tentativas em conjunto.

## Invariante de autorização

Em toda rota protegida, qualquer `:id` da URL ou `id` recebido no corpo precisa corresponder a
`req.user.id` antes de ler ou alterar o recurso. Carteiras usam o ID do cliente autenticado como
dono. Nenhum campo arbitrário do corpo pode substituir a identidade do cliente.

O middleware JWT valida o algoritmo e a assinatura e define `req.user.id` a partir de `jwtid` e
`req.user.email` a partir de `sub`. A autorização de cada recurso continua sendo responsabilidade
do handler da rota; autenticação válida, sozinha, não concede acesso a outros clientes.
