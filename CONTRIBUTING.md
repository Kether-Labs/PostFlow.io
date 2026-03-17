
# Guide de Contribution — PostFlow.io

Bienvenue sur PostFlow.io, un projet open source incubé par **Kether Labs**.  
Ce guide vous explique comment contribuer efficacement, quel que soit votre niveau.

---

## Table des matières

1. [Code de conduite](#-code-de-conduite)
2. [Architecture du projet](#-architecture-du-projet)
3. [Prérequis](#-prérequis)
4. [Installation de l'environnement](#-installation-de-lenvironnement)
5. [Structure du projet](#-structure-du-projet)
6. [Stratégie de branches](#-stratégie-de-branches)
7. [Créer une contribution](#-créer-une-contribution)
8. [Standards de qualité](#-standards-de-qualité)
9. [Ouvrir une Pull Request](#-ouvrir-une-pull-request)
10. [Processus de Code Review](#-processus-de-code-review)
11. [Signaler un bug](#-signaler-un-bug)
12. [Proposer une fonctionnalité](#-proposer-une-fonctionnalité)
13. [Les rôles dans le projet](#-les-rôles-dans-le-projet)
14. [Communication](#-communication)

---

## Code de conduite

Ce projet adhère au [Contributor Covenant](https://www.contributor-covenant.org/fr/version/2/1/code_of_conduct/).  
En participant, vous vous engagez à maintenir un environnement **respectueux, inclusif et bienveillant** pour tous.

**Comportements attendus :**
- Utiliser un langage accueillant et inclusif
- Respecter les points de vue différents
- Accepter les critiques constructives avec ouverture
- Se concentrer sur ce qui est le mieux pour la communauté

**Comportements inacceptables :**
- Harcèlement sous toute forme
- Commentaires insultants ou dégradants
- Trolling ou attaques personnelles

Tout manquement peut être signalé à : **contact@ketherlabs.dev**

---

## Architecture du projet

PostFlow.io est organisé en **mono repo** avec deux parties distinctes :

| Partie | Technologie | Rôle |
|--------|-------------|------|
| `frontend/` | Next.js + TypeScript | Interface utilisateur, planification des posts |
| `backend/` | Spring Boot (Java 17) | API REST, scheduler, intégrations réseaux sociaux |

**Communication frontend ↔ backend :** API REST JSON  
**Scheduler :** Spring Batch + Spring Scheduler  
**Base de données :** PostgreSQL  
**Infrastructure :** Docker Compose

---

## Prérequis

### Pour le Frontend (Next.js)

- **Node.js 18+** — [nodejs.org](https://nodejs.org/)
- **npm** ou **yarn**
- Un compte **GitHub**

```bash
node --version   # v18.x ou supérieur
npm --version    # 9.x ou supérieur
```

### Pour le Backend (Spring Boot)

- **Java 17+** — [adoptium.net](https://adoptium.net/)
- **Maven 3.8+** — [maven.apache.org](https://maven.apache.org/)
- **Docker + Docker Compose** — [docker.com](https://www.docker.com/)
- Un compte **GitHub**

```bash
java --version   # Java 17.x ou supérieur
mvn --version    # Apache Maven 3.8.x ou supérieur
docker --version # Docker 24.x ou supérieur
```

### Pour tous

- **Git** — [git-scm.com](https://git-scm.com/)
- **PostgreSQL 15+** (ou via Docker — recommandé)

---

## Installation de l'environnement

### 1. Forker et cloner le dépôt

```bash
# 1. Forkez le projet depuis GitHub (bouton "Fork" en haut à droite)

# 2. Clonez votre fork localement
git clone https://github.com/Kether-Labs/postflow-io.git
cd postflow-io

# 3. Ajoutez le dépôt original comme remote "upstream"
git remote add upstream https://github.com/Kether-Labs/postflow-io.git
```

### 2. Démarrer la base de données avec Docker

```bash
# Depuis la racine du projet
docker-compose -f docker/docker-compose.dev.yml up -d

# Vérifier que PostgreSQL tourne
docker ps
```

### 3. Installation du Frontend (Next.js)

```bash
cd frontend

# Copier le fichier d'environnement
cp .env.example .env.local

# Installer les dépendances
npm install

# Lancer en mode développement
npm run dev
```

Le frontend sera accessible sur `http://localhost:3000`

### 4. Installation du Backend (Spring Boot)

```bash
cd backend

# Copier le fichier d'environnement
cp src/main/resources/application-example.yml src/main/resources/application-dev.yml

# Remplir les variables dans application-dev.yml
# (clés API Facebook, LinkedIn, PostgreSQL, etc.)

# Compiler et lancer
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

L'API sera accessible sur `http://localhost:8080`

### 5. Vérifier que tout fonctionne

```bash
# Frontend — depuis /frontend
npm run lint
npm run build

# Backend — depuis /backend
mvn test
mvn verify
```

Si toutes les commandes passent ✅ votre environnement est prêt !

---

## Structure du projet

```
postflow-io/
│
├── frontend/                         ← Next.js + TypeScript
│   ├── src/
│   │   ├── app/                      ← Pages (App Router Next.js 14)
│   │   ├── components/               ← Composants réutilisables
│   │   │   ├── ui/                   ← Composants UI génériques
│   │   │   └── features/             ← Composants métier
│   │   ├── hooks/                    ← Custom React Hooks
│   │   ├── services/                 ← Appels API vers le backend
│   │   ├── store/                    ← État global
│   │   ├── types/                    ← Types TypeScript partagés
│   │   └── utils/                    ← Fonctions utilitaires
│   ├── public/
│   ├── package.json
│   ├── tsconfig.json
│   ├── next.config.js
│   └── .env.example
│
├── backend/                          ← Spring Boot (Java 17)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── io/postflow/
│   │   │   │       ├── PostFlowApplication.java
│   │   │   │       ├── controllers/  ← Endpoints REST
│   │   │   │       ├── services/     ← Logique métier
│   │   │   │       ├── repositories/ ← Accès base de données (JPA)
│   │   │   │       ├── models/       ← Entités JPA
│   │   │   │       ├── dto/          ← Data Transfer Objects
│   │   │   │       ├── config/       ← Configuration Spring
│   │   │   │       ├── security/     ← Spring Security + JWT
│   │   │   │       ├── scheduler/    ← Jobs de publication planifiée
│   │   │   │       └── integrations/ ← Facebook, LinkedIn, etc.
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       ├── application-dev.yml
│   │   │       └── application-prod.yml
│   │   └── test/
│   │       └── java/io/postflow/     ← Tests unitaires et intégration
│   └── pom.xml                       ← Dépendances Maven
│
├── docker/                           ← Configuration Docker
│   ├── docker-compose.yml            ← Production
│   ├── docker-compose.dev.yml        ← Développement local
│   ├── Dockerfile.frontend
│   └── Dockerfile.backend
│
├── docs/                             ← Documentation globale
│   ├── architecture.md
│   ├── api-reference.md
│   └── getting-started.md
│
├── .github/
│   ├── workflows/                    ← CI/CD GitHub Actions
│   ├── ISSUE_TEMPLATE/
│   └── PULL_REQUEST_TEMPLATE.md
│
├── .gitignore
├── README.md
├── CONTRIBUTING.md
└── LICENSE
```

---

## Stratégie de branches

Nous utilisons un **Git Flow simplifié** :

| Branche | Rôle |
|---------|------|
| `main` | Code stable — releases officielles uniquement |
| `develop` | Branche d'intégration — toujours à jour |
| `feature/frontend/xxx` | Nouvelle fonctionnalité Next.js |
| `feature/backend/xxx` | Nouvelle fonctionnalité Spring Boot |
| `fix/frontend/xxx` | Correction de bug Next.js |
| `fix/backend/xxx` | Correction de bug Spring Boot |
| `docs/xxx` | Amélioration de la documentation |
| `test/xxx` | Ajout ou amélioration de tests |
| `chore/xxx` | Maintenance (CI, Docker, dépendances) |

> **Ne jamais pousser directement sur `main` ou `develop`.**  
> Toute modification passe obligatoirement par une Pull Request.

### Nommage des branches

```bash
# Bons exemples
feature/frontend/post-scheduler-ui
feature/backend/facebook-oauth-integration
fix/backend/jwt-token-expiry
fix/frontend/dashboard-loading-state
docs/api-reference-update
test/backend/scheduler-integration

# Mauvais exemples
my-branch / fix / update / new-feature
```

---

## Créer une contribution

### Étape 1 — Trouver ou créer une Issue

- Consultez les [Issues ouvertes](https://github.com/Kether-Labs/postflow-io/issues)
- Cherchez le label **`good first issue`** si vous débutez
- Si votre idée n'existe pas encore, créez une Issue avant de coder

### Étape 2 — S'assigner l'Issue

Commentez dans l'Issue :

```
Je prends cette tâche ! Je vais commencer par [décrivez brièvement votre approche].
```

Un mainteneur vous assignera officiellement.

### Étape 3 — Créer votre branche

```bash
# Mettez votre develop local à jour
git checkout develop
git pull upstream develop

# Créez votre branche de travail
git checkout -b feature/frontend/nom-de-votre-feature
# ou
git checkout -b feature/backend/nom-de-votre-feature
```

### Étape 4 — Coder et committer

```bash
# Format : type(scope): description courte
git commit -m "feat(frontend): add post scheduler calendar view"
git commit -m "feat(backend): implement LinkedIn OAuth2 flow"
git commit -m "fix(backend): fix scheduled posts not firing on Sunday"
git commit -m "docs(api): add endpoint reference for /posts"
git commit -m "test(backend): add integration tests for FacebookService"
```

**Types de commits acceptés :**

| Type | Usage |
|------|-------|
| `feat` | Nouvelle fonctionnalité |
| `fix` | Correction de bug |
| `docs` | Documentation uniquement |
| `test` | Ajout ou modification de tests |
| `refactor` | Refactoring sans changement de comportement |
| `chore` | Maintenance, dépendances, CI |
| `style` | Formatage, espaces (sans logique) |

### Étape 5 — Pousser et ouvrir une PR

```bash
git push origin feature/frontend/nom-de-votre-feature
```

Puis ouvrez une **Pull Request** vers la branche `develop` sur GitHub.

---

## Standards de qualité

### Frontend (Next.js + TypeScript)

**TypeScript strict — obligatoire**

```typescript
// ✅ Correct
interface Post {
  id: string
  content: string
  scheduledAt: Date
  platforms: Platform[]
}

async function schedulePost(post: Post): Promise<void> {
  // ...
}

// ❌ Incorrect
async function schedulePost(post: any) {
  // ...
}
```

**Linting et formatage**

```bash
npm run lint        # Vérifier le linting
npm run format      # Formater le code
npm run type-check  # Vérifier les types TypeScript
npm run test        # Lancer les tests
npm run test:coverage  # Avec couverture
```

La couverture de code doit rester **au-dessus de 70%**.

---

### Backend (Spring Boot + Java)

**Structure des controllers — REST propre**

```java
// ✅ Correct
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDTO> schedulePost(
            @Valid @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        PostDTO post = postService.schedule(request, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }
}

// ❌ Incorrect — logique métier dans le controller
@PostMapping
public ResponseEntity<Post> schedulePost(@RequestBody Post post) {
    post.setStatus("SCHEDULED");
    postRepository.save(post);  // Ne jamais faire ça dans un controller
    return ResponseEntity.ok(post);
}
```

**Tests obligatoires**

```bash
mvn test           # Lancer tous les tests
mvn verify         # Tests avec couverture JaCoCo
```

La couverture de code doit rester **au-dessus de 80%** sur les services.

**Variables d'environnement — jamais en dur**

```java
// ✅ Correct — via application.yml
@Value("${meta.app.id}")
private String metaAppId;

// ❌ Incorrect — jamais de credentials dans le code
private String metaAppId = "1458612725884938";
```

---

## Ouvrir une Pull Request

Remplissez le template fourni quand vous ouvrez une PR :

```markdown
## Description
Décrivez clairement les changements apportés et pourquoi.

## Issue liée
Closes #42

## Type de changement
- [ ] Bug fix (frontend)
- [ ] Bug fix (backend)
- [ ] Nouvelle fonctionnalité (frontend)
- [ ] Nouvelle fonctionnalité (backend)
- [ ] Documentation
- [ ] Infrastructure / Docker
- [ ] Autre : ___

## Checklist
- [ ] Mon code respecte les standards du projet
- [ ] J'ai ajouté des tests pour mes changements
- [ ] Tous les tests existants passent (npm test / mvn test)
- [ ] J'ai mis à jour la documentation si nécessaire
- [ ] Mes commits suivent la convention définie
- [ ] Ma PR cible la branche `develop` et non `main`
```

**Règles pour une bonne PR :**
- Une PR = une fonctionnalité ou un fix (pas tout à la fois)
- Pas de PR directement sur `main` — toujours vers `develop`
- Titre clair et descriptif
- Liez toujours l'Issue correspondante avec `Closes #numéro`
- Taille raisonnable — évitez les PR de 50 fichiers modifiés

---

## Processus de Code Review

### Pour les contributeurs

- Soyez réactif aux commentaires (répondez sous 48h si possible)
- Ne prenez pas les retours personnellement — ils améliorent le code
- Si vous n'êtes pas d'accord, expliquez calmement votre point de vue

### Pour les reviewers

- Soyez bienveillant et constructif dans vos retours
- Expliquez *pourquoi* un changement est nécessaire
- Distinguez ce qui est bloquant (`BLOCKING:`) de ce qui est une suggestion (`SUGGESTION:`)
- Validez avec un ✅ quand c'est bien fait

### Critères de validation d'une PR

- [ ] Au moins **1 Lead** a approuvé
- [ ] Tous les checks **CI/CD sont verts**
- [ ] Aucun conflit de merge
- [ ] La checklist PR est complète

---

## Signaler un bug

```markdown
## Description du bug
Une description claire et concise du problème.

## Partie concernée
- [ ] Frontend (Next.js)
- [ ] Backend (Spring Boot)
- [ ] Infrastructure (Docker)

## Comment reproduire
1. Se connecter avec '...'
2. Cliquer sur '...'
3. Voir l'erreur

## Comportement attendu / Comportement observé

## Environnement
- OS :
- Node.js / Java :
- Navigateur :

## Logs / Stack trace
```

---

## Proposer une fonctionnalité

1. **Vérifiez** qu'elle n'est pas déjà dans les Issues
2. **Ouvrez une Issue** avec le label `feature`
3. **Attendez validation** d'un Lead avant de commencer à coder

---

## Les rôles dans le projet

| Rôle | Qui ? | Responsabilités |
|------|-------|-----------------|
| **Lead Frontend** | Core Team | Architecture Next.js, validation PR frontend |
| **Lead Backend** | Core Team | Architecture Spring Boot, validation PR backend |
| **DevOps Lead** | Core Team | Docker, CI/CD, déploiement |
| **UI/UX Lead** | Core Team | Maquettes, système de design |
| **Builder** | Contributeurs actifs | Développe des features, corrige des bugs |
| **Support** | Volontaires | Documentation, tests manuels, recherche |

Les contributeurs réguliers et de qualité peuvent rejoindre la **Core Team** sur invitation.

---

## Communication

| Canal | Usage |
|-------|-------|
| **GitHub Issues** | Bugs, fonctionnalités, tâches — tout ce qui concerne le code |
| **GitHub Discussions** | Débats d'architecture, idées générales |
| **Groupe WhatsApp** | Chat en temps réel, entraide rapide, annonces |
| **Point hebdomadaire** | Sync chaque lundi matin — priorités de la semaine |

> **Règle d'or :** toute décision technique importante doit être documentée dans une Issue ou une Discussion GitHub, même si elle a été prise oralement.

---

## Vos premières contributions

```
1. Donnez une étoile au projet sur GitHub ⭐
2. Lisez ce CONTRIBUTING.md en entier
3. Présentez-vous dans notre espace communautaire
4. Configurez votre environnement de développement
5. Cherchez une issue avec le label "good first issue"
6. Commentez l'issue pour vous l'approprier
7. Codez, testez, documentez
8. Ouvrez votre première Pull Request
9. Recevez un feedback et apprenez !
```

**Pas de panique si vous débutez.** Chaque expert a un jour ouvert sa première PR avec des erreurs. L'équipe est là pour vous aider.

**Bienvenue dans l'équipe PostFlow.io et dans la famille Kether Labs ! 🔥**

---

*Ce guide est lui-même open source. Si vous pensez qu'il peut être amélioré, ouvrez une PR !*
