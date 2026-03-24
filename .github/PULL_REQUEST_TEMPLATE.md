## Résumé
<!-- Décrivez clairement ce que fait cette PR.
     Expliquez le POURQUOI, pas seulement le QUOI. -->

## Issue liée
<!-- Utilisez "Closes #XXX" pour fermer automatiquement l'issue au merge. -->
Closes #

---

## Partie concernée

- [ ] 🟩 **Frontend** — Next.js / TypeScript / React
- [ ] 🟦 **Backend** — Spring Boot / Java
- [ ] 🐳 **Infrastructure** — Docker / CI/CD / GitHub Actions
- [ ] 📝 **Documentation** — README / guides / commentaires

---

## Type de changement

- [ ] `feat` — Nouvelle fonctionnalité
- [ ] `fix` — Correction de bug
- [ ] `docs` — Documentation uniquement
- [ ] `test` — Ajout ou modification de tests
- [ ] `refactor` — Refactoring sans changement de comportement
- [ ] `chore` — Maintenance, CI/CD, dépendances
- [ ] `breaking change` — Modifie l'API publique ou un contrat frontend ↔ backend

---

## Changements apportés
<!--
  Listez les modifications concrètes.
  Exemples :
  - Ajout du hook useLogin() avec TanStack Query
  - Création de l'endpoint POST /api/auth/login
  - Mise à jour du store Zustand pour persister le token
-->
-
-
-

---

## Comment tester

<!-- Donnez les étapes exactes pour vérifier que ça fonctionne. -->

### 🟩 Frontend
<!-- Supprimez cette section si la PR ne concerne pas le frontend -->
```bash
cd frontend
pnpm install
pnpm dev
# Aller sur http://localhost:3000/...
```

```typescript
// Exemple de comportement attendu
```

### 🟦 Backend
<!-- Supprimez cette section si la PR ne concerne pas le backend -->
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
# Tester l'endpoint
curl -X POST http://localhost:8080/api/...
```

```json
// Réponse attendue
{
  "status": "ok"
}
```

---

## Checklist

### 🟩 Frontend — à cocher si la PR touche au frontend
- [ ] `pnpm lint` passe sans erreur
- [ ] `pnpm format:check` passe sans erreur
- [ ] `pnpm build` passe sans erreur
- [ ] `pnpm test` passe sans erreur
- [ ] La couverture de code ne régresse pas (`pnpm test:coverage`)
- [ ] Les composants sont correctement typés — pas de `any`
- [ ] Les hooks utilisent TanStack Query pour les appels API
- [ ] Le state global utilise Zustand — pas de prop drilling excessif
- [ ] Les imports respectent la convention `@/components/features/...`

### 🟦 Backend — à cocher si la PR touche au backend
- [ ] `mvn test` passe sans erreur
- [ ] `mvn verify` passe sans erreur (couverture JaCoCo)
- [ ] La couverture de code ne régresse pas (> 80% sur les services)
- [ ] Tous les endpoints sont correctement typés et validés (`@Valid`)
- [ ] Les erreurs retournent les bons codes HTTP
- [ ] Aucune credential ou secret dans le code — uniquement dans `.env`
- [ ] La logique métier est dans les services — pas dans les controllers

### 📝 Documentation
- [ ] J'ai mis à jour la documentation si nécessaire
- [ ] Si un nouvel endpoint est créé — il est documenté dans `docs/api-reference.md`
- [ ] Si une variable d'environnement est ajoutée — elle est dans `.env.example`

### 🔀 Git
- [ ] Ma branche est à jour avec `develop` — pas de conflits
- [ ] Mes commits suivent la convention `type(frontend|backend): description`
- [ ] Il n'y a pas de commits de debug ou temporaires (`console.log`, `System.out.println`)
- [ ] Ma PR cible `develop` et non `main`

---

## Captures d'écran
<!-- Si pertinent — interface avant/après, résultat de tests, logs. -->
<!-- Supprimez cette section si non applicable. -->

**Avant :**

**Après :**

---

## Notes pour le reviewer
<!--
  Zones d'attention particulière, choix techniques à valider,
  questions ouvertes, points de compromis.
-->