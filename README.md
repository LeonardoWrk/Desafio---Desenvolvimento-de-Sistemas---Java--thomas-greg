
# 📝 Client Registration System - Setup Rápido

## ✅ Tecnologias Utilizadas

- Java 8 (JDK + JRE)
- Maven 3.9.9
- Wildfly (Deployment do Frontend WAR)
- Spring Boot (Backend API)
- Docker + Docker Compose (Banco de dados)

---

## 📦 Pré-requisitos

Antes de rodar o projeto, certifique-se de ter instalado:

| Software       | Versão Recomendada |
|----------------|-------------------:|
| Java JDK + JRE | >= 1.8.0_202       |
| Apache Maven   | 3.9.9              |
| Docker         | 24.x               |
| Docker Compose | 2.x                |
| Wildfly        | Configurado        |

---

## 🚀 Passo a Passo Rápido

### 1️⃣ Preparar Ambiente

- Copie a pasta **java** do repositório para `C:\Program Files\`
- Execute o script `maven.bat` para configurar o Maven
- Execute o script `frontDeployWildfly.bat` para deploy do frontend

### 2️⃣ Banco de Dados (Docker Compose)

Levantar o ambiente do banco de dados:

```bash
docker-compose up -d --build
```

### 3️⃣ Rodar Backend (Spring Boot)

Execute a classe principal da API Backend:

```bash
./mvnw spring-boot:run
```
ou

```bash
java -jar target/seu-backend-api.jar
```

Classe principal: `CadastroApiApplication`

### 4️⃣ Cadastrar Usuário Admin (via Postman/API)

O sistema só pode ser completamente utilizado por usuários com o perfil `ROLE_ADMIN`.  
Este tipo de usuário deve ser criado manualmente via API, pois **não é exposto no frontend**.

Exemplo de requisição (POST):

- URL: `http://localhost:9090/api/auth/signup`
- Body (JSON):

```json
{
  "username": "usuario",
  "email": "usuario1@email.com",
  "password": "senha123",
  "roles": ["ROLE_ADMIN"]
}
```

### 5️⃣ Verificar API

Confirme se a API responde corretamente:

- [http://localhost:8080/api/clients](http://localhost:8080/api/clients)

---

## 🗒️ Observações Importantes

- Verifique as variáveis de ambiente (`JAVA_HOME`, `MAVEN_HOME`, `PATH`) caso encontre erros.
- O deploy do WAR no Wildfly depende de configuração correta do servidor.
- O **Docker Compose** deve estar ativo para o banco de dados funcionar corretamente.
- A criação de usuários `ROLE_ADMIN` é feita diretamente via API (não via frontend).

---

## 🔑 Usuário Padrão (Exemplo de Criação)

| Usuário | Senha     |
|---------|-----------|
| `usuario` | `senha123` |

