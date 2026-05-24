# Installation et tests

## Prérequis

| Outil | Version |
|-------|---------|
| JDK | **21** (obligatoire pour `ai-engine-v1.2.0.jar`) |
| Shell | bash ou zsh |

Vérifier la version :

```bash
java -version
```

---

## Lancer le jeu

```bash
cd ai-2026
./run-game.sh
```

Le menu propose **Nouvelle partie** (carte `level-1.txt`) et **Quitter**.

---

## Cartes disponibles

| Fichier | Usage |
|---------|--------|
| `resources/maps/level-1.txt` | Partie complète (Helm's Deep) — lancée depuis le menu |
| `resources/maps/level-0.txt` | Mini-carte 3×3 pour tests manuels ou démos |

Pour tester la mini-carte en code :

```java
factory.newGame("resources/maps/level-0.txt");
```

Voir aussi `MoveDemo.java` et `./run-move-demo.sh`.

---

## Tests unitaires

### Déplacement (Dijkstra)

```bash
./run-move-tests.sh
```

Couvre `MoveManagerTest.java` : coûts, cases occupées, unités lourdes, reconstruction de chemin.

### Combat (dés fixés)

```bash
./run-combat-tests.sh
```

Ou avec JUnit depuis Eclipse : `UnitCombatTest.java`.

---

## Tests d'acceptation (cours)

Lancer depuis Eclipse ou JUnit :

| Classe | Contenu |
|--------|---------|
| `AI1Test` | Menu principal (2 entrées, quit, nouvelle partie) |
| `AI2Test` | Curseur, terrain, 4 premières lignes du panneau Situation |
| `AI3Test` | Fin de partie / navigation |

Les tests AI-2 ne lisent que les **4 premières lignes** du panneau Situation ; les lignes supplémentaires (scores, combat, sélection) n'impactent pas les TA.

---

## Architecture (ArchUnit)

`tests/helmosdeep/ArchTest.java` vérifie que `domains` ne dépend pas de `views` ni de `supervisors`.

---

## Dépannage rapide

| Problème | Piste |
|----------|--------|
| `UnsupportedClassVersionError` | Passer à Java 21 |
| Espace inactif | Cliquer sur la carte pour redonner le focus |
| Unité invisible après déplacement | Vérifier `GameScene.moveUnit` (recréation du sprite) |
| Aragorn invisible | Nom `"Aragorn"` → fichier `aragorn.png` |
