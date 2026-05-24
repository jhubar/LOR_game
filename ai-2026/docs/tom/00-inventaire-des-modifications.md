# Inventaire des modifications — HelmosDeep

Ce document recense **tous les fichiers touchés** pendant le développement du projet (déplacement, combat, interface, tests, documentation).  
Il sert de **vue d’ensemble** pour Tom : quoi a été créé, modifié ou supprimé, et pourquoi.

Pour le détail fonctionnel, voir aussi [05-journal-des-mises-a-jour.md](./05-journal-des-mises-a-jour.md).

---

## Résumé en chiffres

| Catégorie | Nouveaux | Modifiés | Supprimés |
|-----------|----------|----------|-----------|
| Domaine (`domains/`) | 8 | 8 | 1 |
| Superviseurs | 0 | 3 | 0 |
| Vues | 0 | 2 | 0 |
| Point d’entrée | 0 | 1 | 0 |
| Tests | 2 | 0 | 0 |
| Scripts shell | 4 | 0 | 0 |
| Documentation Tom | 10 | 1 | 0 |
| Code brouillon | 0 | 0 | 8 |

---

## Fichiers supprimés (nettoyage)

| Fichier | Raison |
|---------|--------|
| `src/helmosdeep/domains/customNode.java` | Prototype de pathfinding abandonné, jamais branché au jeu |
| `src/temporaire/General.java` | Code d’essai non intégré |
| `src/temporaire/Legere.java` | idem |
| `src/temporaire/Lourde.java` | idem |
| `src/temporaire/Moyenne.java` | idem |
| `src/temporaire/Rohirrim.java` | idem |
| `src/temporaire/Unit.java` | idem |
| `src/temporaire/Wargs.java` | idem |

---

## Domaine — fichiers créés

| Fichier | Rôle |
|---------|------|
| `src/helmosdeep/domains/MoveManager.java` | Dijkstra : coût minimal, cases atteignables, `findPath` |
| `src/helmosdeep/domains/Path.java` | Chemin reconstruit (liste de `Coordinates` + coût total) |
| `src/helmosdeep/domains/AttackOutcome.java` | Résultat structuré d’une attaque pour le superviseur / la vue |
| `src/helmosdeep/domains/AttackPowerBreakdown.java` | Détail `force + alliés + dé` et format d’affichage compact |
| `src/helmosdeep/domains/CombatReport.java` | Compte-rendu d’un duel + lignes pour panneau Situation / fin de partie |
| `src/helmosdeep/domains/CombatSelfTest.java` | Tests combat en ligne de commande (dés fixés, sans JUnit) |
| `src/helmosdeep/domains/MoveManagerSelfTest.java` | Tests déplacement en ligne de commande (sans JUnit) |
| `src/helmosdeep/domains/MoveDemo.java` | Démo console du pathfinding sur une petite carte |

---

## Domaine — fichiers modifiés

| Fichier | Modifications principales |
|---------|---------------------------|
| `Coordinates.java` | `getNeighbours()` (6 hex), `equals` / `hashCode` pour Dijkstra |
| `MiddleEarth.java` | `isPassable`, `getMovementCost`, `moveUnit`, `removeUnit`, `getArmyFor` |
| `HelmosDeepGame.java` | Sélection, `moveSelectedUnit`, `attackSelectedUnit`, fin de tour, fin de partie, scores, `lastCombatReport`, historique combats |
| `Unit.java` | Noms sprites corrigés, PM, `canAttack`, `attack` / `resolveAttack` (dés injectables), puissance d’attaque |
| `Army.java` | `removeUnit`, `recordKill`, `getKillCount`, `getUnitCount`, `getNbOfAllies` |
| `ETileType.java` | Couleurs / libellés d’affichage pour le panneau Situation |
| `Tile.java` | Accès unité + type de terrain (support déplacement / UI) |
| `HelmosDeepGameFactory.java` | Création de partie depuis fichier carte `.txt` |

---

## Superviseurs — fichiers modifiés

| Fichier | Modifications principales |
|---------|---------------------------|
| `supervisors/game/GameSupervisor.java` | Boucle complète : sélection, déplacement, attaque, fin de tour ; panneau Situation enrichi ; **surbrillance des cases atteignables** ; gestion `AttackOutcome` |
| `supervisors/gameover/GameOverSupervisor.java` | Reçoit `IGameFactory` ; affiche stats réelles (unités restantes, kills, vainqueur, historique combats) |
| `supervisors/mainmenu/MainMenuSupervisor.java` | Lance `level-1.txt` au « Nouvelle partie » ; nettoyage commentaires |

---

## Vues — fichiers modifiés

| Fichier | Modifications principales |
|---------|---------------------------|
| `views/GameScene.java` | Focus clavier (Espace / Entrée) ; `moveUnit` recréé le sprite ; `removeUnit` ; texte panneau Commandes (déplacement + attaque) ; `findUnitAt` robuste |
| `views/components/UnitTile.java` | Chargement sprites par nom (`resources/sprites/<nom>.png`) |

---

## Point d’entrée

| Fichier | Modifications principales |
|---------|---------------------------|
| `HelmosDeep.java` | Passe `IGameFactory` à `GameOverSupervisor` pour l’écran de fin |
| `.project` | Retrait plugins PMD (import Eclipse sans extensions obligatoires) |
| `HelmosDeep.launch` | Configuration Run avec working directory = racine projet |

---

## Tests — fichiers créés

| Fichier | Rôle |
|---------|------|
| `tests/helmosdeep/domains/MoveManagerTest.java` | Tests JUnit : coûts, cases occupées, unités lourdes, `findPath` |
| `tests/helmosdeep/domains/UnitCombatTest.java` | Tests JUnit : égalité → attaquant gagne, élimination, PM légère, généraux, format `CombatReport` |

Les tests d’acceptation du cours **n’ont pas été modifiés** :

- `tests/helmosdeep/acceptance/AI1Test.java`
- `tests/helmosdeep/acceptance/AI2Test.java`
- `tests/helmosdeep/acceptance/AI3Test.java`
- `tests/helmosdeep/ArchTest.java`

---

## Scripts shell — fichiers créés

| Script | Commande | Usage |
|--------|----------|--------|
| `run-game.sh` | `./run-game.sh` | Lance le jeu (Java 21) |
| `run-move-tests.sh` | `./run-move-tests.sh` | Tests déplacement (`MoveManagerSelfTest`) |
| `run-combat-tests.sh` | `./run-combat-tests.sh` | Tests combat (`CombatSelfTest`) |
| `run-move-demo.sh` | `./run-move-demo.sh` | Démo console pathfinding |

---

## Documentation Tom — fichiers créés

| Fichier | Contenu |
|---------|---------|
| `docs/tom/README.md` | Index, FAQ, symboles carte, liens vers les chapitres |
| `docs/tom/00-inventaire-des-modifications.md` | **Ce document** — liste exhaustive des changements |
| `docs/tom/01-architecture-du-projet.md` | Couches domain / supervisors / views |
| `docs/tom/02-algorithme-de-deplacement.md` | Dijkstra, coûts terrain, PM |
| `docs/tom/03-algorithme-de-combat.md` | Règles de duel, flux, diagramme |
| `docs/tom/04-boucle-de-jeu-et-controles.md` | Clavier, curseur vs sélection, corrections UI |
| `docs/tom/05-journal-des-mises-a-jour.md` | Journal chronologique des évolutions |
| `docs/tom/06-installation-et-tests.md` | Java 21, scripts, tests unitaires et TA |
| `docs/tom/07-import-eclipse.md` | Guide import Eclipse + dépannage |
| `docs/tom/09-stats-et-affichage-combat.md` | Scores, détail combats, écran fin de partie |

---

## Fonctionnalités ajoutées (par thème)

### Déplacement
- Pathfinding Dijkstra sur hex (`MoveManager`)
- Déplacement au clavier : sélection → curseur → Espace
- Coûts terrain + règle unités lourdes + cases occupées bloquantes

### Combat
- Attaque sur ennemi **adjacent** (Espace)
- Puissance = force + alliés voisins + dé 1–6
- Égalité → attaquant gagne ; perdant retiré de la carte
- Généraux ne peuvent pas attaquer
- Unités légères : 1 PM après attaque réussie
- Fin de partie si une armée n’a plus d’unités

### Interface / UX
- Focus clavier corrigé sur `GameScene`
- Sprites : noms alignés sur les PNG (`Aragorn`, `Rohirrim`, …)
- Unité recréée après déplacement (plus de sprite perdu)
- Plus de crash `ConcurrentModificationException` en fin de tour
- Panneau Situation : scores, dernier combat, unité sélectionnée + description du type
- Surbrillance des cases **atteignables** à la sélection
- Écran fin de partie avec stats réelles et historique des combats
- Dernier combat effacé à chaque fin de tour

---

## Fichiers non modifiés (référence)

Ces fichiers existent dans le projet mais n’ont **pas** été au cœur des changements gameplay :

- `views/MainMenuScene.java`, `views/GameOverScene.java`
- `views/components/HexTile.java`, `views/FontUtils.java`
- `supervisors/game/GameView.java`, `GameViewListener.java`
- `supervisors/mainmenu/*`, `supervisors/gameover/GameOverView.java`
- `domains/EUnitType.java`, `IGameFactory.java`, `TileInfo.java`
- `util/Contract.java`, `util/TxtFileReader.java`
- Ressources : `resources/maps/`, `resources/sprites/`, `resources/fonts/`, `resources/libs/`

---

## Cartes et ressources utilisées

| Ressource | Chemin | Note |
|-----------|--------|------|
| Carte principale | `resources/maps/level-1.txt` | Chargée par le menu « Nouvelle partie » |
| Carte entraînement | `resources/maps/level-0.txt` | 3×3, pour tests / démos (pas dans le menu) |
| Moteur graphique | `resources/libs/ai-engine-v1.2.0.jar` | Nécessite **Java 21** |

---

## Ce qui n’a pas été fait (volontairement)

Pour ne pas casser les tests d’acceptation **AI-1** (menu à 2 entrées) :

- Pas de choix de niveau dans le menu (toujours `level-1.txt`)
- Pas d’IA adversaire
- Pas d’animation pas à pas le long de `findPath` (déplacement en un clic)
- Pas de capacités spéciales / attaque à distance

---

## Ordre de lecture recommandé pour Tom

1. [00-inventaire-des-modifications.md](./00-inventaire-des-modifications.md) ← ce fichier  
2. [01-architecture-du-projet.md](./01-architecture-du-projet.md)  
3. [05-journal-des-mises-a-jour.md](./05-journal-des-mises-a-jour.md)  
4. Chapitres 02–04 selon le sujet (déplacement, combat, contrôles)  
5. [06-installation-et-tests.md](./06-installation-et-tests.md) pour lancer et tester  

---

## Vérification rapide

```bash
cd ai-2026
./run-move-tests.sh    # déplacement OK ?
./run-combat-tests.sh  # combat OK ?
./run-game.sh          # jeu graphique (Java 21)
```

Tests d’acceptation : lancer `AI1Test`, `AI2Test`, `AI3Test` depuis Eclipse.
