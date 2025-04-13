# API-Kt

API RESTful desenvolvida em Kotlin com Spring Boot e MongoDB.

## Configuração

### Variáveis de Ambiente

A aplicação utiliza um arquivo `.env` para configurar variáveis de ambiente. Para configurar:

1. Crie um arquivo `.env` na raiz do projeto baseado no `.env.example`
2. Personalize as variáveis conforme necessário

Exemplo de arquivo `.env`:

```
# Configuração do MongoDB - Ambiente Padrão
MONGODB_URI=mongodb://usuario:senha@localhost:27017/apiKt
MONGODB_DATABASE=apiKt

# JWT Security
JWT_SECRET=sua_chave_secreta_aqui_com_pelo_menos_32_caracteres
JWT_EXPIRATION=86400000 

# Outras configurações por perfil...
```

### Variáveis suportadas

| Variável             | Descrição                                     | Valor padrão                      |
|----------------------|-----------------------------------------------|-----------------------------------|
| MONGODB_URI          | URI de conexão com o MongoDB (padrão)         | mongodb://localhost:27017/apiKt   |
| MONGODB_DATABASE     | Nome do banco de dados (padrão)               | apiKt                             |
| SERVER_PORT          | Porta do servidor (padrão)                    | 8080                              |
| APP_NAME             | Nome da aplicação (padrão)                    | api-kt                            |
| LOG_LEVEL            | Nível de log principal (INFO, DEBUG, etc)     | INFO                              |
| MONGO_LOG_LEVEL      | Nível de log do MongoDB                       | DEBUG                             |
| JWT_SECRET           | Chave secreta para tokens JWT                 | (Necessário definir)              |
| JWT_EXPIRATION       | Tempo de expiração do token em ms             | 86400000 (24 horas)               |

### Variáveis por perfil (dev)

| Variável             | Descrição                                     | Valor padrão                      |
|----------------------|-----------------------------------------------|-----------------------------------|
| MONGODB_URI_DEV      | URI do MongoDB (ambiente dev)                 | mongodb://localhost:27017/apiKtDev|
| MONGODB_DATABASE_DEV | Nome do banco de dados (ambiente dev)         | apiKtDev                          |
| SERVER_PORT_DEV      | Porta do servidor (ambiente dev)              | 8081                              |
| APP_NAME_DEV         | Nome da aplicação (ambiente dev)              | api-kt-dev                        |
| LOG_LEVEL_DEV        | Nível de log (ambiente dev)                   | DEBUG                             |
| MONGO_LOG_LEVEL_DEV  | Nível de log do MongoDB (ambiente dev)        | DEBUG                             |

### Variáveis por perfil (prod)

| Variável             | Descrição                                     | Valor padrão                      |
|----------------------|-----------------------------------------------|-----------------------------------|
| MONGODB_URI_PROD     | URI do MongoDB (ambiente prod)                | mongodb://localhost:27017/apiKtProd|
| MONGODB_DATABASE_PROD| Nome do banco de dados (ambiente prod)        | apiKtProd                         |
| SERVER_PORT_PROD     | Porta do servidor (ambiente prod)             | 8082                              |
| APP_NAME_PROD        | Nome da aplicação (ambiente prod)             | api-kt-prod                       |
| LOG_LEVEL_PROD       | Nível de log (ambiente prod)                  | INFO                              |
| MONGO_LOG_LEVEL_PROD | Nível de log do MongoDB (ambiente prod)       | INFO                              |

## Perfis de Execução

A aplicação suporta diferentes perfis de execução, cada um com suas configurações específicas:

```bash
# Perfil padrão - Porta 8080, database apiKt
./gradlew bootRun

# Perfil de desenvolvimento - Porta 8081, database apiKtDev
./gradlew bootRun --args='--spring.profiles.active=dev'

# Perfil de produção - Porta 8082, database apiKtProd
./gradlew bootRun --args='--spring.profiles.active=prod'
```

## Sistema de Autenticação JWT

A API utiliza autenticação baseada em tokens JWT (JSON Web Tokens) para proteger rotas específicas.

### Usuários Padrão

A aplicação vem configurada com dois usuários padrão:

1. **Admin**
   - Username: `admin`
   - Password: `admin123`
   - Role: `ROLE_ADMIN`

2. **Usuário Comum**
   - Username: `user`
   - Password: `user123`
   - Role: `ROLE_USER`

### Endpoints de Autenticação

- `POST /api/auth/login` - Autenticar usuário existente e obter token JWT
- `POST /api/auth/register` - Registrar novo usuário e obter token JWT
- `GET /api/auth/me` - Obter informações do usuário autenticado

### Níveis de Acesso

A API possui três níveis de acesso:

1. **Público** (`/api/public/**`): Acessível sem autenticação
2. **Usuário** (`/api/players/**`): Requer autenticação com role `ROLE_USER` ou `ROLE_ADMIN`
3. **Admin** (`/api/admin/**`): Requer autenticação com role `ROLE_ADMIN`

### Exemplo de Autenticação

Para acessar endpoints protegidos, inclua o token JWT no cabeçalho de autorização:

```bash
curl -X GET http://localhost:8080/api/players/test \
  -H "Authorization: Bearer seu_token_jwt_aqui"
```

## Interface Web

A aplicação inclui uma interface web simples para testar a API:

1. Acesse `http://localhost:8080/` em seu navegador
2. Faça login com as credenciais padrão (admin/admin123 ou user/user123)
3. Use os botões para testar diferentes endpoints

## Endpoints

### Endpoints de Teste

- `GET /api/public/test` - Endpoint público para testar acesso sem autenticação
- `GET /api/players/test` - Endpoint protegido (requer ROLE_USER ou ROLE_ADMIN)
- `GET /api/admin/test` - Endpoint administrativo (requer ROLE_ADMIN)

### Jogadores (Players)

- `GET /api/players` - Lista todos os jogadores
- `GET /api/players/{id}` - Busca jogador por ID
- `GET /api/players/username/{username}` - Busca jogador por username
- `POST /api/players` - Cria um novo jogador
- `PUT /api/players/{id}` - Atualiza um jogador existente
- `DELETE /api/players/{id}` - Remove um jogador