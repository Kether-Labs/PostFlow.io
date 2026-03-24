# PostFlow.io — Backend

---

## Prerequis

| Outil | Version minimale | Verification |
|---|---|---|
| Java (JDK) | 21 | `java -version` |
| Maven | 3.9+ | `mvn -version` |
| Docker | 24+ | `docker -v` |
| Docker Compose | 2.x | `docker compose version` |
| Git | 2.x | `git --version` |

---

## Installation

### 1. Cloner le depot

```bash
git clone https://github.com/your-org/postflow-backend.git
cd postflow-backend
```

### 2. Copier le fichier d'environnement

```bash
cp .env.example .env
```

Editer `.env` avec les valeurs locales avant de continuer.

### 3. Generer les cles RSA (RS256)

Les cles ne doivent jamais etre committees dans le depot.

```bash
mkdir -p src/main/resources/keys

openssl genrsa -out src/main/resources/keys/private.pem 2048

openssl rsa -in src/main/resources/keys/private.pem \
            -pubout \
            -out src/main/resources/keys/public.pem
```

Verifier que `.gitignore` contient bien :

```
src/main/resources/keys/
```

### 4. Generer la cle AES (chiffrement tokens OAuth)

```bash
openssl rand -base64 32
```

Copier la valeur obtenue dans la variable `AES_SECRET_KEY` du `.env`.

### 5. Demarrer les services locaux

```bash
docker compose -f docker/docker-compose.yml up -d
```

```bash
docker compose -f docker/docker-compose.yml ps
```

### 6. Compiler

```bash
mvn clean install -DskipTests
```

---

## Configuration

`src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect

  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}
      password: ${REDIS_PASSWORD:}

  mail:
    host: ${MAIL_HOST}
    port: ${MAIL_PORT}
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}

security:
  jwt:
    private-key-path: classpath:keys/private.pem
    public-key-path: classpath:keys/public.pem
    access-token-ttl: 900
    refresh-token-ttl: 604800
    refresh-token-remember-me-ttl: 2592000

app:
  base-url: ${APP_BASE_URL}
  encryption:
    aes-key: ${AES_SECRET_KEY}

server:
  port: ${SERVER_PORT:8080}
```

---

## Variables d'environnement

| Variable | Description | Exemple |
|---|---|---|
| `DB_URL` | JDBC URL PostgreSQL | `jdbc:postgresql://localhost:5432/postflow` |
| `DB_USERNAME` | Utilisateur PostgreSQL | `postflow` |
| `DB_PASSWORD` | Mot de passe PostgreSQL | `changeme` |
| `REDIS_HOST` | Hote Redis | `localhost` |
| `REDIS_PORT` | Port Redis | `6379` |
| `REDIS_PASSWORD` | Mot de passe Redis (vide si non configure) | `` |
| `SERVER_PORT` | Port d'ecoute | `8080` |
| `MAIL_HOST` | Hote SMTP | `localhost` |
| `MAIL_PORT` | Port SMTP | `1025` |
| `MAIL_USERNAME` | Utilisateur SMTP | `` |
| `MAIL_PASSWORD` | Mot de passe SMTP | `` |
| `APP_BASE_URL` | URL publique du frontend | `http://localhost:3000` |
| `AES_SECRET_KEY` | Cle AES-256 pour chiffrement tokens OAuth | *(generer avec openssl)* |

---

## Lancement

```bash
mvn spring-boot:run
```

Swagger UI : `http://localhost:8080/swagger-ui.html`

### Avec un profil Spring

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Build JAR

```bash
mvn clean package -DskipTests
java -jar target/postflow-backend-*.jar
```
