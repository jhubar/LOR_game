# Journal des mises à jour

Ce document liste les **évolutions et corrections** apportées au projet HelmosDeep, dans l'ordre logique de développement.  
Chaque entrée indique **quoi**, **pourquoi**, et **où** dans le code.

---

## 1. Algorithme de déplacement (Dijkstra)

**Quoi :** Implémentation complète du pathfinding sur grille hexagonale.

**Fichiers :**
- `MoveManager.java` — Dijkstra avec file de priorité et élagage par PM
- `Path.java` — reconstruction du chemin via map `parent`
- `Coordinates.java` — `getNeighbours()`, `equals()`, `hashCode()`

**Pourquoi :** Calculer le coût minimal entre deux cases en respectant terrain, unités lourdes, cases occupées et budget PM.

**Tests :** `MoveManagerTest.java`, script `run-move-tests.sh`

---

## 2. Intégration des déplacements dans la partie

**Quoi :** L'unité sélectionnée peut se déplacer vers une case vide atteignable.

**Fichiers :**
- `HelmosDeepGame.moveSelectedUnit`
- `MiddleEarth.moveUnit`, `isPassable`, `getMovementCost`
- `GameSupervisor.onAction` — branche déplacement
- `GameScene.moveUnit` — animation

**Pourquoi :** Relier l'algorithme au gameplay réel.

---

## 3. Correction focus clavier (Espace inactif)

**Problème :** La touche Espace ne déclenchait aucune action.

**Cause :** Focus sur les panneaux UI, pas sur `GameScene`.

**Correction :** `setFocusable`, `KeyListener`, `requestFocusInWindow`, panneaux non focusables.

**Fichier :** `GameScene.java`

---

## 4. Correction noms de sprites

**Problème :** Aragorn (et potentiellement d'autres unités) invisible.

**Cause :** Typo `"Aragon"` vs fichier `aragorn.png`.

**Correction :** Noms alignés sur les fichiers PNG dans `Unit.getUnitWithLetter`.

**Fichier :** `Unit.java`

---

## 5. Correction sélection après changement de tour

**Problème :** Les unités des Hommes difficiles ou impossibles à sélectionner après le tour du Mordor.

**Cause :** `selectedUnit` non réinitialisé dans le modèle ; décalage vue/modèle.

**Correction :**
- `HelmosDeepGame.endTurn()` → `selectedUnit = null` + reset PM armée active
- `GameSupervisor.clearSelection()` en fin de tour
- Bascule de sélection vers une autre unité alliée au Espace

**Fichiers :** `HelmosDeepGame.java`, `GameSupervisor.java`

---

## 6. Correction unité invisible après déplacement

**Problème :** Le sprite disparaissait après un déplacement.

**Cause :** `UnitTile` conservait l'id de l'ancienne case.

**Correction :** Recréation du sprite à la destination dans `moveUnit`.

**Fichier :** `GameScene.java`

---

## 7. Correction crash ConcurrentModificationException

**Problème :** Plantage après déplacement puis Entrée (fin de tour).

**Cause :** Enqueue de tween dans `.then()` pendant l'itération de `Tweens.update`.

**Correction :** Suppression du callback `.then(() -> newUnit.select())`.

**Fichier :** `GameScene.java`

---

## 8. Implémentation du combat

**Quoi :** Système d'attaque complet entre unités adjacentes.

**Fichiers créés / modifiés :**

| Fichier | Rôle |
|---------|------|
| `AttackOutcome.java` | Résultat structuré pour la vue |
| `Unit.canAttack`, `Unit.attack`, `getAttackPower` | Règles de duel |
| `Army.removeUnit`, `recordKill`, `getUnitCount` | Gestion armée |
| `MiddleEarth.removeUnit`, `getArmyFor` | Retrait carte + armée |
| `HelmosDeepGame.attackSelectedUnit`, `isGameOver` | Orchestration |
| `GameSupervisor.applyAttackOutcome` | Mise à jour graphique |
| `GameScene` | Texte panneau Commandes |

**Règles implémentées :**
- Attaque uniquement sur ennemi **adjacent**
- Puissance = force + alliés voisins + dé 1–6
- Perdant éliminé ; gagnant occupe la case
- Généraux n'attaquent pas
- Unités légères : 1 PM après attaque réussie
- Fin de partie si une armée est à 0 unité

**Doc détaillée :** [03-algorithme-de-combat.md](./03-algorithme-de-combat.md)

---

## 9. Améliorations `findUnitAt` (robustesse vue)

**Quoi :** Recherche d'unité par id **ou** par position monde (fallback).

**Pourquoi :** Éviter les échecs de sélection/déplacement si id et position divergent temporairement.

**Fichier :** `GameScene.findUnitAt`

---

## Récapitulatif par couche

| Couche | Mises à jour principales |
|--------|--------------------------|
| **domains** | Dijkstra, déplacement, combat, fin de partie, Coordinates |
| **supervisors** | onAction (move + attack), clearSelection, fin de tour |
| **views** | Focus clavier, moveUnit, removeUnit, commandes |

---

## 10. Affichage des stats de combat et fin de partie

**Quoi :** Détail des combats et scores branchés sur l'interface.

**Fonctionnalités :**
- Breakdown `force+alliés+dé` figé par combat (`AttackPowerBreakdown`, `CombatReport`)
- Panneau Situation : scores live + dernier combat + unité sélectionnée
- Écran fin de partie : unités restantes, kills, vainqueur, historique des 8 derniers combats

**Fichiers :** voir [09-stats-et-affichage-combat.md](./09-stats-et-affichage-combat.md)

**Doc détaillée :** [09-stats-et-affichage-combat.md](./09-stats-et-affichage-combat.md)

**Inventaire complet des fichiers :** [00-inventaire-des-modifications.md](./00-inventaire-des-modifications.md)

---

## 11. Nettoyage et finitions UX

**Quoi :**

- Suppression du code mort (`customNode.java`, dossier `temporaire/`)
- Surbrillance des cases **atteignables** quand une unité est sélectionnée (`MoveManager.reachableCosts` + `GameSupervisor`)
- Description du type d'unité dans le panneau Situation
- Dernier combat effacé à la fin de tour (`HelmosDeepGame.endTurn`)
- Tests combat déterministes (`Unit.resolveAttack` + `UnitCombatTest`)
- Doc consolidée : [06-installation-et-tests.md](./06-installation-et-tests.md)

**Fichiers :** `GameSupervisor.java`, `Unit.java`, `HelmosDeepGame.java`, `tests/helmosdeep/domains/UnitCombatTest.java`

---

## Ce qui reste à faire (évolutions futures)

Fonctionnalités **non** implémentées — pistes pour la suite :

| Piste | État |
|-------|------|
| IA adversaire | Non |
| Attaque à distance / capacités spéciales | Non |
| Animation pas à pas le long de `findPath` | Non (téléportation en un clic) |
| Choix de carte dans le menu | Non (menu fixe → `level-1.txt` ; `level-0.txt` via code) |

**Déjà en place :**

- Détail des combats dans le panneau Situation (`CombatReport`)
- Scores live et écran de fin alimenté par les stats réelles
- Surbrillance des cases atteignables à la sélection
- Description du type d'unité sélectionnée (`EUnitType.getDescr()`)
- Tests unitaires combat avec dés fixés (`UnitCombatTest`)
- Effacement du dernier combat à chaque fin de tour

---

## Comment vérifier une mise à jour

1. Lire le fichier concerné dans `src/helmosdeep/...`
2. Lancer `./run-game.sh` et reproduire le scénario
3. Pour le déplacement : `./run-move-tests.sh`
4. Pour l'architecture : tests `ArchTest.java` (Eclipse / JUnit)

---

## Glossaire rapide

| Terme | Signification |
|-------|----------------|
| PM | Points de mouvement restants |
| Domaine | Règles pures du jeu, sans UI |
| Superviseur | Traduit actions utilisateur → modèle → vue |
| Curseur | Case active (hex surligné) |
| Sélection | Unité choisie pour agir ce tour |
