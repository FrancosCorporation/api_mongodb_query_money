# API MongoDB Query Money

## 🐳 Instalação e Execução (Docker) — recomendado

### Pré-requisitos
- [Docker](https://docs.docker.com/get-docker/) + Docker Compose

### Rodar com Docker
```bash
docker compose up --build
```
```bash
docker run --rm -v $(pwd):/src -w /src maven:3.8-openjdk-17 mvn spring-boot:run
```

### Sem Docker (local)
```bash
# Requer JDK 17 + Maven
mvn spring-boot:run
```

API REST em **Spring Boot** para consulta e organização de dados do mercado financeiro (cotações da B3) sobre **MongoDB**, com autenticação **JWT** e documentação interativa via **Swagger**.

![Java](https://img.shields.io/badge/Java-11-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.x-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-47A248?style=flat-square&logo=mongodb&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=flat-square&logo=jsonwebtokens)
![License](https://img.shields.io/badge/license-MIT-green?style=flat-square)
![Status](https://img.shields.io/badge/status-conclu%C3%ADdo%20(estudo)-blue?style=flat-square)

## Sobre

Back-end de estudo para armazenar e consultar dados de ações negociadas na B3
(por exemplo, `AGRO3.SA`) e montar visões/layouts por cliente. O projeto explora
uma arquitetura em camadas (controllers, services, repositories, dtos, models),
segurança com Spring Security + JWT e persistência em MongoDB, além de um
workflow de build e push de imagem para Amazon ECR/ECS.

> **Projeto de estudo** — desenvolvido em 2022 para praticar Spring Boot,
> MongoDB e autenticação por token.

## Funcionalidades

Comprovadas pelo código em `src/main/java`:

- **Clientes**: cadastro (`Dtos_cliente_create`) e login (`Dtos_cliente_login`)
  com senha e geração de token.
- **Dados da B3**: modelos `Models_data_b3` / `Models_data_b3_names` e
  repositórios para consulta das cotações e dos nomes dos papéis.
- **Listas e layouts por cliente**: `Repository_data_list_names`,
  `Repository_layout_data_client` e serviços correspondentes para montar a
  visão personalizada de cada cliente.
- **Segurança**: filtro e validação de JWT (`security/Auth_*`) e configuração de
  CORS/segurança (`Auth_config`).
- **Documentação**: **Swagger** habilitado via SpringFox (`SpringFoxConfig`),
  acessível em `/swagger-ui/`.
- **CI/CD**: workflow em `.github/workflows/aws.yml` para build da imagem Docker
  e deploy no Amazon ECS (requer secrets de AWS configurados).

## Stack

- **Java** + **Spring Boot** (Web, Data MongoDB, Security)
- **MongoDB** (driver oficial)
- **JWT** (jjwt) + BCrypt
- **SpringFox** (Swagger/OpenAPI)
- **Maven** (wrapper incluso: `./mvnw`)
- **Docker** + **Amazon ECR/ECS** (workflow opcional)

## Como rodar

Requisitos: JDK 11+ e um MongoDB acessível.

```bash
# opção 1: MongoDB local
# (no Windows havia um mongod.exe versionado em db/; recomendado instalar
#  o MongoDB Community Server e removê-lo do repositório)

# configuração
# ajuste a URI do MongoDB em src/main/resources/application.properties

# rodar
./mvnw spring-boot:run
```

A documentação do Swagger fica disponível em `http://localhost:8080/swagger-ui/`.

## Estrutura do projeto

```
src/main/java/com/api_mongo/api_mongodb_query_money/
├── config/          # Configurações gerais e Swagger
├── controllers/     # Endpoints REST
├── dtos/            # Objetos de entrada/saída
├── models/          # Documentos do MongoDB
├── repositories/    # Interfaces do Spring Data
├── security/        # Filtros e validação de JWT
└── services/        # Regras de negócio
```

## Licença

MIT — veja [LICENSE](LICENSE).
