# 🚀 PostFlow.io

> Plateforme SaaS de gestion et d’automatisation des publications sur les réseaux sociaux.

---

## 📌 Description

**PostFlow.io** est une application SaaS permettant aux créateurs de contenu, startups et agences marketing de **centraliser, planifier et automatiser leurs publications** sur plusieurs réseaux sociaux depuis un seul tableau de bord.

👉 Objectif : **gagner du temps, automatiser la diffusion et améliorer la régularité éditoriale.**

---

## 🎯 Problème

Aujourd’hui, publier sur plusieurs réseaux implique :

- Dupliquer les contenus manuellement  
- Adapter les formats pour chaque plateforme  
- Gérer plusieurs comptes en parallèle  
- Risquer des oublis ou une irrégularité  

---

## 💡 Solution

PostFlow apporte :

- ✅ Publication multi-réseaux en un clic  
- ✅ Planification automatique des posts  
- ✅ Centralisation des comptes sociaux  
- ✅ Gestion des médias (images/vidéos)  
- ✅ Assistance IA pour générer/améliorer du contenu  
- ✅ Gestion SaaS (plans, quotas, abonnements)  

---

## 👥 Utilisateurs cibles

- Créateurs de contenu  
- Freelancers / Social Media Managers  
- Startups  
- Agences marketing  

---

## ⚙️ Stack technique

### Frontend
- Next.js  

> ⚠️ Comming Soon

### Backend
- Spring Boot  
- Architecture DDD (Domain Driven Design)  
- Système asynchrone (scheduler + workers)  

---

## 🧠 Fonctionnalités principales

### 🔐 Authentification & sécurité
- Inscription / Connexion  
- JWT (Access + Refresh Token)  
- Réinitialisation de mot de passe  
- Gestion des sessions  

---

### 👤 Gestion utilisateur
- Profil utilisateur  
- Préférences  
- Statistiques d’utilisation  

---

### 🔗 Connexion réseaux sociaux
- OAuth2 :
  - LinkedIn  
  - Twitter / X  
  - Facebook  
  - Instagram  

---

### 📝 Gestion de contenu

Statuts des posts :

- Draft  
- Scheduled  
- Published  
- Failed  
- Partial  

---

### 📅 Planification
- Sélection date/heure (timezone utilisateur)  
- Scheduler automatique  
- Exécution asynchrone  

---

### ⚡ Publication automatique
- Appels API vers réseaux sociaux  
- Retry automatique  
- Logging complet  
- Notifications utilisateur  

---

### 🖼️ Gestion des médias
- Upload images & vidéos  
- Compression & optimisation  
- Stockage S3-compatible  
- Gestion multi-médias  

---

### 🤖 Assistance IA
- Génération de contenu  
- Amélioration de texte  
- Hashtags automatiques  
- Résumé de contenu  

---

### 💳 Gestion SaaS & Billing

| Plan   | Limites |
|--------|--------|
| Free   | Usage limité |
| Pro    | Fonctionnalités avancées |
| Agency | Multi-workspace + gestion clients |

---

## 🧩 Domaines métier (DDD)

- Identity  
- User  
- Social Integration  
- Post  
- Scheduling  
- Media  
- AI  
- Billing  

---

## 🔄 Flux principal

### Publication planifiée

1. Authentification utilisateur  
2. Connexion réseau social  
3. Création du post  
4. Upload des médias  
5. Sélection des réseaux  
6. Planification  
7. Détection par scheduler  
8. Exécution du worker  
9. Mise à jour du statut  

---

### Génération IA

1. Saisie du prompt  
2. Vérification du quota  
3. Traitement asynchrone  
4. Retour du résultat  

---

## 🗄️ Schéma de base de données

### 📌 Schéma global
https://dbdiagram.io/d/Postflow-io-69b8bc2f78c6c4bc7afc13e9

### 📌 Schéma MVP
https://dbdiagram.io/d/Postflow-MVP-69b8c8b478c6c4bc7afc9205

---

## 🐳 Infrastructure & déploiement

- Docker  
- Docker Compose  
- PostgreSQL  
- Redis  
- Stockage S3-compatible  
- Nginx  
- CI/CD via GitHub Actions  

---

## 🔐 Contraintes techniques

### Sécurité
- Chiffrement des tokens OAuth (AES-256)  
- Hash mots de passe (bcrypt)  
- HTTPS obligatoire  
- Rate limiting  
- Validation stricte des inputs  

---

### Performance
- API < 500ms (hors IA)  
- Traitements asynchrones  
- Pagination obligatoire  
- Index base de données  

---

### Scalabilité
- Support jusqu’à 10k utilisateurs (v1)  
- Redis pour jobs + sessions  
- Stockage externe pour médias  

---

## ⚠️ Contraintes externes

- APIs réseaux sociaux (quotas, limitations)  
- Coût API Twitter/X  
- Validation Facebook / Instagram  
- Gestion des tokens OAuth  

---

## 📈 Roadmap

### 🚀 v1.0 (MVP)
- Authentification  
- LinkedIn + Twitter  
- Création & planification  
- Images  
- Dashboard simple  

---

### ⚡ v1.5
- Facebook + Instagram  
- IA  
- Vidéo  
- Calendrier éditorial  

---

### 🌍 v2.0
- Multi-workspace  
- Collaboration  
- Analytics avancés  
- Templates  
- IA avancée  

---

## ⚠️ Risques

- Changements API (Twitter)  
- Délais validation (Meta)  
- Coût IA  
- Sécurité des tokens  

---

## 📄 Documentation

- `PostFlow_Specifications_Fonctionnelles_V1.docx`

---

## 🤝 Contribution

Merci de respecter :

- Clarté du code  
- Cohérence avec les specs  
- Respect des domaines métier  
- Pas d’ajout d’architecture non validée  

---

## 📜 Licence

Projet privé — usage interne uniquement.
