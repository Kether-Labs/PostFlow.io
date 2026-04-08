<div align="center">

<img width="735" height="237" alt="postflow" src="https://github.com/user-attachments/assets/6891e2d6-c317-41a3-88e9-bbf9a84e87f8" />





**Plateforme SaaS open source de gestion et d'automatisation des publications sur les réseaux sociaux**

Incubé par [Kether Labs](https://github.com/Kether-Labs) — Structurer · Pérenniser · Transmettre

<br/>

[![License](https://img.shields.io/badge/license-MIT-6366f1?style=flat-square)](LICENSE)
[![Backend](https://img.shields.io/badge/backend-Spring%20Boot%203-6DB33F?style=flat-square&logo=spring)](backend/)
[![Frontend](https://img.shields.io/badge/frontend-Next.js%2015-000000?style=flat-square&logo=next.js)](frontend/)
[![Database](https://img.shields.io/badge/database-PostgreSQL%2015-4169E1?style=flat-square&logo=postgresql)](docker/)
[![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-2088FF?style=flat-square&logo=github-actions)](/.github/workflows)

</div>

---

## 🧭 Tu rejoins le projet ? Commence ici.

PostFlow.io est un **mono repo** avec deux équipes complètement indépendantes.
Chaque équipe a son propre environnement, ses propres outils, et son propre guide.

<table>
<tr>
<td width="50%" align="center">

### 🟩 Je suis dev Frontend

**Next.js · TypeScript · Tailwind · shadcn/ui**

Tu travailles sur l'interface utilisateur.

> ✅ Tu n'as **pas besoin** d'installer Java
> ✅ Tu n'as **pas besoin** de lancer Spring Boot
> ✅ Tu travailles avec des **mocks** ou l'**API déployée**

👉 **[Lire le guide Frontend →](frontend/README.md)**

</td>
<td width="50%" align="center">

### 🟦 Je suis dev Backend

**Spring Boot 3 · Java 17 · PostgreSQL · Redis**

Tu travailles sur l'API REST et le scheduler.

> ✅ Tu déploies l'API pour toute l'équipe
> ✅ Tu n'as **pas besoin** d'installer Node.js
> ✅ L'équipe frontend consomme ton API déployée

👉 **[Lire le guide Backend →](backend/README.md)**

</td>
</tr>
</table>

> **Nouveau contributeur ?** Lis ce README en entier puis consulte
> le [CONTRIBUTING.md](CONTRIBUTING.md) avant de choisir ton équipe.

---

## 📌 C'est quoi PostFlow.io ?

PostFlow.io permet aux créateurs de contenu, freelancers et agences marketing de
**centraliser, planifier et automatiser leurs publications**
sur plusieurs réseaux sociaux depuis un seul tableau de bord.

**Le problème :** publier sur plusieurs réseaux impose de dupliquer les contenus,
d'adapter les formats, de gérer plusieurs comptes en parallèle,
et de risquer des oublis ou une irrégularité.

**La solution :** un SaaS simple et puissant qui automatise tout ça.

---

## 🔀 Comment les deux équipes collaborent

C'est le point le plus important à comprendre avant de commencer.

```
Équipe Frontend                    Équipe Backend
──────────────────────             ──────────────────────
Next.js sur localhost:3000         Spring Boot déployé en ligne
         │                                    │
         │  Phase 1 — Backend pas encore      │
         │  déployé                           │
         │  NEXT_PUBLIC_USE_MOCK=true          │
         │  → données fictives locales         │
         │  → aucune dépendance backend        │
         │                                    │
         │  Phase 2 — API déployée             │
         │  NEXT_PUBLIC_USE_MOCK=false         │
         │  API_URL=https://api.postflow.io ───┘
         │
         ↓
  Chaque équipe avance à son rythme
  sans jamais attendre l'autre
```

### Phase 1 — Développement en parallèle *(maintenant)*

L'équipe frontend utilise des **mocks** — des données fictives qui simulent
exactement les réponses que le backend renverra.
Un seul changement dans `.env.local` pour basculer plus tard :

```bash
# Mode mock — aucun backend nécessaire
NEXT_PUBLIC_USE_MOCK=true

# Mode API réelle — quand le backend est déployé
NEXT_PUBLIC_USE_MOCK=false
NEXT_PUBLIC_API_URL=https://api.postflow.io
```

### Phase 2 — Intégration *(quand l'API est déployée)*

L'équipe backend déploie l'API sur un serveur accessible à toute l'équipe.
L'équipe frontend change `USE_MOCK=false` et pointe vers l'URL de l'API.
**Aucun autre changement de code nécessaire.**

---

## ⚙️ Stack technique

```
PostFlow.io
├── frontend/          Next.js 16 · TypeScript · Tailwind CSS
│                      shadcn/ui · TanStack Query · Zustand
│                      React Hook Form · Zod · Vitest
│
├── backend/           Spring Boot 3 · Java 17 · Maven
│                      Spring Security · JWT · JPA/Hibernate
│                      Architecture DDD (Domain Driven Design)
│                      Redis · Scheduler asynchrone
│
└── infrastructure/    PostgreSQL 15 · Redis · Docker Compose
                       Nginx · Stockage S3-compatible
                       GitHub Actions CI/CD
```

---

## 🏗️ Architecture du projet

```
postflow-io/                         ← Racine du mono repo
│
├── frontend/                        ← Application Next.js
│   ├── src/
│   │   ├── app/                     ← Pages (App Router)
│   │   ├── components/
│   │   │   ├── ui/                  ← Composants shadcn/ui
│   │   │   └── features/            ← Composants par domaine métier
│   │   │       ├── auth/
│   │   │       ├── posts/
│   │   │       ├── social/
│   │   │       └── dashboard/
│   │   ├── services/                ← Appels API (mock ou réel)
│   │   ├── mocks/                   ← Données fictives de développement
│   │   ├── store/                   ← État global Zustand
│   │   ├── hooks/                   ← Hooks React partagés
│   │   └── config/env.ts            ← Variables d'environnement centralisées
│   └── README.md                    ← Guide complet Frontend
│
├── backend/                         ← Application Spring Boot
│   └── src/main/java/io/postflow/
│       ├── identity/                ← Domaine Authentification
│       ├── user/                    ← Domaine Utilisateur
│       ├── social/                  ← Domaine Réseaux Sociaux
│       ├── post/                    ← Domaine Posts
│       ├── scheduling/              ← Domaine Planification
│       ├── media/                   ← Domaine Médias
│       ├── ai/                      ← Domaine IA
│       └── billing/                 ← Domaine Facturation
│   └── README.md                    ← Guide complet Backend
│
├── docker/                          ← Configuration Docker
│   ├── docker-compose.yml           ← Production
│   └── docker-compose.dev.yml       ← Développement local
│
├── docs/                            ← Documentation globale
│   ├── api-reference.md             ← Contrats d'API frontend ↔ backend
│   ├── environment-variables.md     ← Toutes les variables
│   └── getting-started.md
│
├── .github/
│   ├── workflows/                   ← CI/CD GitHub Actions
│   └── ISSUE_TEMPLATE/
│
├── CONTRIBUTING.md
└── README.md
```


---

## 🟦 Démarrage Backend — configuration complète

> ✅ **Prérequis : Java 17+, Maven 3.8+, Docker**
> ❌ Node.js, pnpm — **non requis pour le backend**

```bash
# 1. Cloner le repo
git clone https://github.com/Kether-Labs/PostFlow.io.git
cd PostFlow.io

# 2. Démarrer PostgreSQL et Redis
docker-compose -f docker/docker-compose.dev.yml up -d

# 3. Configurer l'environnement
cd backend
cp src/main/resources/application-example.yml \
   src/main/resources/application-dev.yml
# Remplir les variables dans application-dev.yml

# 4. Lancer le backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

> 🎉 API accessible sur **http://localhost:8080**

```bash
# Vérifier que l'API tourne
curl http://localhost:8080/api/health
# {"status": "ok", "service": "PostFlow API"}
```

> **Responsabilité de l'équipe backend :**
> Une fois l'API stable, la déployer sur un serveur
> accessible à toute l'équipe pour que le frontend
> puisse basculer de `USE_MOCK=true` vers `USE_MOCK=false`.

👉 **[Guide complet Backend →](backend/README.md)**

---

## 📋 Roadmap

### 🚀 v1.0 — MVP *(en cours)*

| Feature | Équipe | Status |
|---------|--------|--------|
| Setup mono repo | Les deux | ✅ Fait |
| Authentification JWT | Backend | 🔄 En cours |
| Pages login / register | Frontend | 🔄 En cours |
| OAuth LinkedIn + Twitter | Backend | 📋 Planifié |
| Connexion réseaux côté UI | Frontend | 📋 Planifié |
| Création et planification posts | Les deux | 📋 Planifié |
| Dashboard simple | Frontend | 📋 Planifié |
| Gestion des images | Backend | 📋 Planifié |

### ⚡ v1.5
- Facebook + Instagram
- Assistance IA — génération de contenu
- Gestion vidéo
- Calendrier éditorial

### 🌍 v2.0
- Multi-workspace et collaboration
- Analytics avancés
- Templates de posts
- IA avancée

---

## 🗄️ Base de données

Le schéma est géré exclusivement par l'équipe backend.
Les devs frontend n'ont pas besoin de connaître la structure
de la base de données — ils interagissent uniquement avec les
endpoints API documentés dans `docs/api-reference.md`.

| Schéma | Lien |
|--------|------|
| Schéma global | [Voir sur dbdiagram.io →](https://dbdiagram.io/d/Postflow-io-69b8bc2f78c6c4bc7afc13e9) |
| Schéma MVP | [Voir sur dbdiagram.io →](https://dbdiagram.io/d/Postflow-MVP-69b8c8b478c6c4bc7afc9205) |

---

## 🤝 Contribuer

```
1. Lire le CONTRIBUTING.md
2. Choisir une issue avec le label "good first issue"
3. Commenter : "Je prends cette tâche !"
4. Créer sa branche :
   feature/frontend/nom   ← pour le frontend
   feature/backend/nom    ← pour le backend
5. Coder, tester, documenter
6. PR vers develop avec : Closes #numéro
```

👉 **[Guide de contribution complet →](CONTRIBUTING.md)**

---

## 📄 Documentation

| Document | Pour qui | Description |
|----------|----------|-------------|
| [Guide Frontend](frontend/README.md) | Devs Frontend | Installation, mocks, commandes |
| [Guide Backend](backend/README.md) | Devs Backend | Installation, DDD, déploiement API |
| [CONTRIBUTING.md](CONTRIBUTING.md) | Tout le monde | Workflow Git, issues, PR |
| [Référence API](docs/api-reference.md) | Les deux équipes | Contrats d'API documentés |
| [Variables d'environnement](docs/environment-variables.md) | Les deux équipes | Toutes les variables |

---

## 📜 Licence

Ce projet est sous licence **MIT**.
Voir le fichier [LICENSE](LICENSE) pour les détails.

---

<div align="center">

Construit avec ❤️ par la communauté **Kether Labs**

[GitHub](https://github.com/Kether-Labs) ·
[CONTRIBUTING](CONTRIBUTING.md) ·
[Signaler un bug](https://github.com/Kether-Labs/PostFlow.io/issues/new)

*Structurer · Pérenniser · Transmettre*

</div>
