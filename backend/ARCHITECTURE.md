# Architecture DDD Hexagonale — PostFlow.io

schema de base de donnée : 
# **MVP**
* https://dbdiagram.io/d/Postflow-MVP-69b8c8b478c6c4bc7afc9205
# **COMPLET**
* https://dbdiagram.io/d/Postflow-io-69b8bc2f78c6c4bc7afc13e9


> **Une classe = une responsabilité. Le domain ne connaît rien de l'extérieur.**

---

## Qu'est-ce que c'est ?

Une façon d'organiser un backend Spring Boot où la **logique métier** est totalement isolée de la technologie (base de données, framework, API externe). Inspirée du **Domain-Driven Design (DDD)** et de l'**Architecture Hexagonale** (Ports & Adapters).

---

## Les 3 couches

```
┌─────────────────────────────────────────┐
│           INFRASTRUCTURE                │  ← Adapters sortants
│  ┌───────────────────────────────────┐  │     JPA, Redis, SMTP, APIs...
│  │         APPLICATION               │  │  ← Adapters entrants
│  │  ┌─────────────────────────────┐  │  │     Controllers, Schedulers
│  │  │         DOMAIN              │  │  │  ← Logique métier pure
│  │  │  UseCases · Ports · Events  │  │  │     POJO Java, 0 framework
│  │  └─────────────────────────────┘  │  │
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

**Règle absolue** : les dépendances vont uniquement **vers l'intérieur**. Le Domain n'importe jamais Spring, JPA ou quoi que ce soit d'externe.

---

## Les 5 concepts clés

### 1. UseCase — une classe, une opération
Remplace le `@Service` classique qui grossit indéfiniment. Chaque opération métier est une classe autonome avec une seule méthode `execute()`.

```
RegisterUserUseCase.execute(RegisterCommand) → RegisterResult
LoginUseCase.execute(LoginCommand)           → LoginResult
SchedulePostUseCase.execute(ScheduleCommand) → ScheduleResult
```

### 2. Port — l'interface que le Domain définit
Ce dont le UseCase a besoin, exprimé comme un contrat. Le Domain dit *quoi*, pas *comment*.

```java
// domain/port/UserRepositoryPort.java
public interface UserRepositoryPort {
    void save(User user);
    Optional<User> findByEmail(String email);
}
```

### 3. Adapter — la classe qui implémente le Port
Vit dans Infrastructure. Contient la technologie concrète (JPA, Redis, SMTP...).

```java
// infrastructure/persistence/UserRepositoryAdapter.java
@Component
public class UserRepositoryAdapter implements UserRepositoryPort {
    private final UserJpaRepository jpa;
    // implements save(), findByEmail()...
}
```

### 4. Domain Event — la communication entre domaines
Un domaine publie un *fait* passé. Les autres domaines écoutent sans couplage direct.

```java
// identity publie — ne sait pas qui écoute
events.publishEvent(new UserRegisteredEvent(user.getId()));

// billing écoute — ne sait pas qui a publié
@EventListener
public void on(UserRegisteredEvent e) { createFreeSubscription(e.userId()); }
```

### 5. Entité Domain vs Entité JPA
Deux classes distinctes. L'entité domain est un POJO pur avec la logique métier. L'entité JPA est la projection en base. L'Adapter fait le mapping.

```
User.java (domain)         ←→   UserJpaEntity.java (infrastructure)
POJO pur, logique métier         @Entity, colonnes DB
```

---

## Structure de packages

```
src/main/java/io/postflow/
└── {domaine}/                    ← ex: identity/, post/, billing/
    ├── domain/
    │   ├── entity/               ← POJO métier (User, Post...)
    │   ├── usecase/              ← RegisterUserUseCase, LoginUseCase...
    │   ├── port/                 ← interfaces (UserRepositoryPort...)
    │   ├── event/                ← UserRegisteredEvent...
    │   └── exception/            ← EmailAlreadyExistsException...
    ├── application/
    │   ├── controller/           ← AuthController (@RestController)
    │   ├── dto/                  ← RegisterRequest, LoginResponse...
    │   └── handler/              ← ExceptionHandler (@RestControllerAdvice)
    └── infrastructure/
        ├── persistence/          ← UserJpaEntity, UserRepositoryAdapter
        ├── redis/                ← RedisBlacklistAdapter
        ├── email/                ← SmtpEmailAdapter
        └── config/               ← @Bean qui wire UseCase ← Adapters
```

---

## Le flux d'une requête

```
HTTP POST /api/auth/register
        ↓
AuthController        [Application]   adapte HTTP → Command
        ↓
RegisterUserUseCase   [Domain]        logique métier, appelle Ports
        ↓
UserRepositoryPort    [Domain]        interface — save(user)
        ↓
UserRepositoryAdapter [Infrastructure] implémente avec JPA
        ↓
201 Created ←─────────────────────────── retour inverse
```

---

## Pourquoi cette architecture ?

| Question | Réponse |
|---|---|
| Peut-on tester le UseCase sans DB ? | **Oui** — injecter un FakeRepository en mémoire |
| Peut-on changer de DB ? | **Oui** — créer un nouvel Adapter, 0 changement dans le Domain |
| Peut-on voir le catalogue fonctionnel ? | **Oui** — lister les classes UseCase |
| Un domaine peut-il casser un autre ? | **Non** — communication uniquement via Events |

---

## Domaines PostFlow

| Domaine | UseCases principaux |
|---|---|
| `identity` | Register, Login, RefreshToken, Logout, VerifyEmail |
| `user` | GetProfile, UpdateProfile, GetNotifications |
| `post` | CreatePost, SchedulePost, CancelSchedule, GetPosts |
| `scheduling` | CreatePublishJob, ExecutePublishJob, RetryPublishJob |
| `social` | InitiateOAuth, HandleOAuthCallback, RevokeSocialAccount |
| `billing` | GetSubscription, CheckFeatureLimit, UpgradePlan |
| `media` | UploadMedia, DeleteMedia |
| `ai` | GenerateContent, ImprovePost, GenerateHashtags |

---

*Pour une explication complète avec exemples de code pas à pas → voir le tutoriel `DDD_Hexagonal_Tutorial.docx`*
