# Finch Java Developer Test - Auth & TVMaze Integration

Este projeto é um módulo de autenticação e gerenciamento de usuários integrado a uma funcionalidade de consulta de shows e episódios através da API externa [TVMaze](https://www.tvmaze.com/api).

O sistema utiliza Spring Security com JWT (JSON Web Token) para proteção de endpoints e persistência de dados em banco de dados H2 (em memória para desenvolvimento) ou PostgreSQL.

## 🛠 Tecnologias Utilizadas

- **Java 25**
- **Spring Boot 3.5.7**
- **Spring Security** (OAuth2 Resource Server com JWT)
- **Spring Data JPA**
- **Flyway** (Migração de banco de dados)
- **H2 Database** (Desenvolvimento/Testes)
- **OpenAPI / Swagger** (Documentação)
- **Lombok**
- **Maven**

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
- JDK 25 instalado
- Maven instalado (ou use o wrapper `./mvnw`)

### Passos para execução
1. Clone o repositório.
2. Navegue até a pasta raiz do projeto.
3. **Importante**: No IntelliJ IDEA, se a opção do Maven não aparecer, clique com o botão direito no arquivo `pom.xml` e selecione **"Add as Maven Project"**.
4. Execute o comando:
   ```bash
   ./mvnw spring-boot:run
   ```
4. A aplicação estará disponível em `http://localhost:8080`.

### Acesso ao Banco de Dados (H2)
Em ambiente de desenvolvimento (`dev`), você pode acessar o console do H2 em:
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:tvshow-db`
- **User**: `root`
- **Password**: `root`

---

## 📖 Documentação da API (Swagger)

A documentação interativa da API está disponível em:
- `http://localhost:8080/swagger-ui/index.html`

---

## 🔒 Autenticação

A maioria dos endpoints exige um token JWT no cabeçalho da requisição:
`Authorization: Bearer <seu_token_aqui>`

---

## 📁 Endpoints Principais

### 1. Autenticação

#### **POST** `/api/auth/login`
Realiza o login e retorna o token JWT.
- **Corpo da requisição:**
  ```json
  {
    "username": "seu_usuario",
    "password": "sua_senha"
  }
  ```

### 2. Gerenciamento de Usuários (`/api/users`)

- **POST** `/api/users`: Cria um novo usuário (Aberto).
  - **Payload:**
    ```json
    {
      "username": "user_test",
      "password": "password123",
      "role": "USER",
      "enabled": true
    }
    ```
    *(Roles permitidas: `ADMIN`, `USER`)*

- **GET** `/api/users`: Lista todos os usuários (Requer `ADMIN`).
  - Suporta paginação: `?page=0&size=10&sortField=id&sortOrder=ASC`
- **GET** `/api/users/{id}`: Busca um usuário específico (Requer `ADMIN` ou `USER`).
- **PUT** `/api/users/{id}`: Atualiza um usuário (Requer `ADMIN`).
- **DELETE** `/api/users/{id}`: Remove um usuário (Requer `ADMIN`).

### 3. Shows e Episódios (`/api/v1/shows`)

Estes endpoints permitem buscar dados da API TVMaze e salvá-los localmente.

#### **GET** `/api/v1/shows?showName={nome}`
Busca um show pelo nome na API externa, salva o show e seus episódios no banco de dados local e retorna os detalhes.
- **Exemplo:** `GET /api/v1/shows?showName=Girls`

#### **GET** `/api/v1/shows/episodes/average`
Calcula a média de rating dos episódios salvos no banco de dados, agrupados por temporada.
- **Retorno esperado:**
  ```json
  [
    {
      "season": 1,
      "averageRating": 7.5
    },
    {
      "season": 2,
      "averageRating": 8.2
    }
  ]
  ```

## 📊 Monitoramento (Spring Actuator)

O projeto possui o **Spring Boot Actuator** configurado para monitoramento da saúde e métricas da aplicação.

Os endpoints de gerenciamento estão disponíveis sob o prefixo `/management`:

- **Health Check**: `http://localhost:8080/management/health` (Verifica se a aplicação e o banco de dados estão operando corretamente).
- **Informações da App**: `http://localhost:8080/management/info`
- **Métricas**: `http://localhost:8080/management/metrics`

---

## 📝 Notas Adicionais

- O sistema utiliza **Flyway** para gerenciar o esquema do banco de dados. As migrações estão localizadas em `src/main/resources/db/migration`.
- As permissões são baseadas em perfis:
    - `USER`: Acesso a consultas básicas e seu próprio perfil.
    - `ADMIN`: Acesso total ao gerenciamento de usuários e gatilhos de integração.
