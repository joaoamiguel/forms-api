# IA Assessment Platform

Aplicação web completa para diagnóstico de prontidão em IA, com autenticação, formulários dinâmicos, persistência em MySQL, cálculo automático de índices e dashboard administrativo.

## Stack

- Backend: Java 21 + Spring Boot
- Segurança: Spring Security + JWT
- Persistência: Spring Data JPA + Flyway
- Banco: MySQL
- Frontend: React + Next.js + HTML/CSS/JS
- Containerização: Docker + Docker Compose

## Como executar localmente

### 1. Backend

```bash
cd backend
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`.

### 2. Frontend

```bash
cd frontend
npm install
npm run dev
```

O frontend sobe em `http://localhost:3000`.

### 3. Banco de dados

Suba um MySQL local ou use o Docker Compose abaixo.

## Executar com Docker

```bash
docker compose up --build
```

Isso sobe:
- MySQL em `3306`
- Backend em `8080`
- Frontend em `3000`

## Credenciais iniciais

O backend cria automaticamente, na primeira subida, dois usuários:

- **Admin** (role `ADMIN`, acesso ao Formulário 1, Formulário 2 e Dashboard)
  - Email: `admin@local`
  - Senha: `admin123`
- **Usuário padrão** (sem role, acesso apenas ao Formulário 1)
  - Email: `user@local`
  - Senha: `user123`

Qualquer novo cadastro feito pela tela de registro nasce sem role (mesmo comportamento do usuário padrão acima) — a role `ADMIN` não pode ser autoatribuída.

## Variáveis de ambiente

Copie `.env.example` para um `.env` e ajuste quando necessário.

## Estrutura principal

- `backend/` — API Spring Boot
- `frontend/` — app Next.js
- `docs/` — espaço para documentação adicional

## Endpoints principais

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `GET /api/forms`
- `GET /api/forms/{code}`
- `POST /api/submissions/{formCode}`
- `GET /api/submissions/{formCode}/mine`
- `GET /api/admin/dashboard`

## Regras dos formulários e roles

A aplicação possui uma única role: `ADMIN`. Usuários sem essa role são
usuários comuns.

### Formulário 1
- disponível para **qualquer usuário autenticado** (com ou sem role)
- mede organização, times, pessoas, literacia e oportunidade
- contém itens reversos e perguntas objetivas com gabarito
- calcula prontidão, literacia, oportunidade e quadrante

### Formulário 2
- disponível **apenas para usuários com a role `ADMIN`**
- mede dados, validação, governança, estratégia e ROI
- calcula o índice técnico final

### Dashboard administrativo
- disponível apenas para usuários com a role `ADMIN`

## Observações

- O sistema impede duplicidade de resposta por usuário e formulário.
- O frontend busca os formulários dinamicamente da API.
- A lógica de cálculo fica centralizada no backend.
