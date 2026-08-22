# PostFlow.io — Backend

---

## Prerequis

| Outil | Version minimale | Verification |
|---|---|---|
| Java (JDK) | 25 | `java -version` |
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
docker compose up -d
```

```bash
docker compose ps
```

### 6. Compiler

```bash
mvn clean install -DskipTests
```

---

## Configuration

`src/main/resources/application.yaml` charge automatiquement le fichier `.env`
placé à la racine du backend. Les variables d'environnement du système restent
prioritaires sur celles du fichier.

Le chargement est configure par
`spring.config.import: optional:file:./.env[.properties]`. Aucun package dotenv
supplementaire n'est necessaire.

---

## Variables d'environnement

| Variable | Description | Exemple |
|---|---|---|
| `POSTGRES_HOST` | Hote PostgreSQL | `localhost` |
| `POSTGRES_PORT` | Port PostgreSQL | `5432` |
| `POSTGRES_DB` | Base PostgreSQL | `postflow` |
| `POSTGRES_USER` | Utilisateur PostgreSQL | `postflow` |
| `POSTGRES_PASSWORD` | Mot de passe PostgreSQL | `changeme` |
| `REDIS_HOST` | Hote Redis | `localhost` |
| `REDIS_PORT` | Port Redis | `6379` |
| `SERVER_PORT` | Port d'ecoute | `8080` |
| `SMTP_HOST` | Hote SMTP | `localhost` |
| `SMTP_PORT` | Port SMTP | `1025` |
| `SMTP_USERNAME` | Utilisateur SMTP | `` |
| `SMTP_PASSWORD` | Mot de passe SMTP | `` |
| `MAIL_FROM` | Adresse d'expedition | `no-reply@postflow.local` |
| `APP_BASE_URL` | URL publique du frontend | `http://localhost:3000` |

---

## Lancement

```bash
mvn spring-boot:run
```

Swagger UI : `http://localhost:8080/swagger-ui.html`

## API d'authentification

Toutes les routes sont publiques et préfixées par `/api/auth`.

| Méthode | Route | Entrée | Réponse |
|---|---|---|---|
| `POST` | `/register` | `{ firstname, lastname, email, password }` | `201 Created` |
| `POST` | `/login` | `{ email, password }` | `200 OK`, tokens dans le body et refresh token dans un cookie `HttpOnly` |
| `POST` | `/refresh` | Cookie `refreshToken` ou `{ token }` | `200 OK` |
| `POST` | `/logout` | `{ accessToken }` (optionnel) | `200 OK`, suppression du cookie de refresh |
| `POST` | `/verify-email?token=...` | Query parameter `token` | `200 OK` |
| `POST` | `/forgot-password` | `{ email }` | `200 OK` avec une réponse anti-énumération |
| `POST` | `/reset-password` | `{ token, newPassword, confirmPassword }` | `200 OK` |

Le cookie de refresh est limité à `/api/auth`, `HttpOnly`, `Secure`,
`SameSite=Strict`, et expire après sept jours. Si le cookie et le body sont tous
les deux fournis à `/refresh`, le cookie est prioritaire.

Les erreurs métier sont retournées sous la forme :

```json
{
  "errorCode": "INVALID_TOKEN",
  "errorMessage": "token is invalid or not found"
}
```

Pour exécuter les tests :

```bash
mvn test
```

### Avec un profil Spring

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Build JAR

```bash
mvn clean package -DskipTests
java -jar target/postflow-backend-*.jar
```
