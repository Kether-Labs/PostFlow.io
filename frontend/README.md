# Frontend - PostFlow.io

Bienvenue dans le frontend de PostFlow.io ! Ce guide vous permettra de lancer le projet en quelques minutes.

## 📋 Prérequis

- **Node.js** 18+ ([télécharger](https://nodejs.org/))
- **pnpm** 8+ (`npm install -g pnpm`)

Vérifiez vos versions :

```bash
node --version  # v18.0.0 ou supérieur
pnpm --version  # 8.0.0 ou supérieur
```

---

## ⚙️ Installation

Lancez ces 4 commandes pour démarrer :

```bash
# 1. Installer les dépendances
pnpm install

# 2. Configurer les variables d'environnement (mode mock par défaut)
cp .env.example .env.local  # Si le fichier existe, sinon créez-le

# 3. Démarrer le serveur de développement
pnpm run dev
```

---

## 🔄 Mode Mock

Par défaut, l'application fonctionne en **mode mock** (données simulées) pour tester sans backend.

### Activer/désactiver le mode mock

Ajoutez cette variable à votre fichier `.env.local` :

```bash
# Mode mock ON (données simulées depuis le frontend)
NEXT_PUBLIC_USE_MOCK=true

# Mode mock OFF (requêtes API vers le backend localhost:8080)
NEXT_PUBLIC_USE_MOCK=false
```

### Quand utiliser quoi ?

| Quand                        | Configurer                                         |
| ---------------------------- | -------------------------------------------------- |
| Développement frontend isolé | `NEXT_PUBLIC_USE_MOCK=true`                        |
| Tests avec le backend local  | `NEXT_PUBLIC_USE_MOCK=false` + démarrer le backend |
| Tests d'intégration          | Voir [CONTRIBUTING.md](../CONTRIBUTING.md)         |

---

## 📁 Structure du projet

### `src/features/`

Chaque fonctionnalité métier est isolée dans son dossier :

```
src/features/
├── auth/              # Authentification, login, register
├── posts/             # Posts, composition, planification
└── social/            # Intégrations Facebook, LinkedIn, etc.
```

**Chaque feature contient :**

- `components/` - Composants métier
- `hooks/` - Hooks React personnalisés
- `services/` - Appels API, logique métier
- `types/` - Schémas et types TypeScript
- `utils/` - Utilitaires spécifiques à la feature
- `index.ts` - Exports publics de la feature

### `src/components/`

Composants réutilisables génériques (UI primitives) :

```
src/components/
├── ui/                # Composants Shadcn/UI
└── shad_ui/          # Composants Shadcn personnalisés
```

### Convention d'imports

**Importez depuis l'`index.ts` des features :**

```typescript
// ✅ BON - Import depuis l'index de la feature
import { useAuth, AuthGuard, LoginForm } from '@/features/auth'
import { usePosts, PostCard } from '@/features/posts'

// ❌ MAUVAIS - Import direct des fichiers
import { useAuth } from '@/features/auth/hooks/useAuth'
import PostCard from '@/features/posts/components/PostCard'
```

---

## 📜 Scripts disponibles

```bash
# Démarrage
pnpm run dev              # Serveur de développement
pnpm run start            # Serveur de production

# Build
pnpm run build            # Compiler pour la production

# Code Quality
pnpm run lint             # ESLint
pnpm run format           # Prettier (formater le code)
pnpm run format:check     # Vérifier la formatage

# Tests
pnpm run test             # Tests en watch mode (Vitest)
pnpm run test:ui          # Dashboard de tests interactif
pnpm run test:run         # Tests une seule fois
pnpm run test:coverage    # Tests avec couverture de code
```

---

## 🚀 Premiers pas

1. **Lire le code existant**
   - Regarder les composants dans `src/features/`
   - Comprendre le pattern des hooks

2. **Contribuer**
   - Consulter [CONTRIBUTING.md](../CONTRIBUTING.md) pour les conventions
   - Les PR doivent passer lint, format et tests

3. **Questions ?**
   - Vérifier les issues existantes
   - Consulter l'[architecture du backend](../backend/ARCHITECTURE.md)

---

## 📚 Stack technique

- **Next.js 16** - Framework React
- **React 19** - UI library
- **TypeScript** - Typage fort
- **TailwindCSS** - Styling
- **React Query** - Data fetching & caching
- **React Hook Form** - Forms
- **Zustand** - State management
- **Vitest** - Testing unitaire
- **Shadcn/UI** - Components UI

---

## 💡 Besoin d'aide ?

-  Lire [CONTRIBUTING.md](../CONTRIBUTING.md) complet
-  Consulter [ARCHITECTURE.md](../backend/ARCHITECTURE.md) pour l'intégration backend
-  Signaler un bug / demander une feature via les issues

**Bon code ! 🎉**
