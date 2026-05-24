# HelmosDeep — Guide pour Tom

Ce dossier regroupe la documentation du projet **HelmosDeep** (jeu de stratégie au tour par tour sur carte hexagonale, univers Seigneur des Anneaux).

L'objectif est de t'aider à comprendre **ce qui a été ajouté ou corrigé**, et **comment chaque algorithme fonctionne**, en lien direct avec le code source.

---

## Par où commencer ?

| Ordre | Fichier | Contenu |
|-------|---------|---------|
| 0 | [00-inventaire-des-modifications.md](./00-inventaire-des-modifications.md) | **Liste complète** des fichiers créés, modifiés et supprimés |
| 1 | [01-architecture-du-projet.md](./01-architecture-du-projet.md) | Organisation des packages, rôle de chaque couche |
| 2 | [02-algorithme-de-deplacement.md](./02-algorithme-de-deplacement.md) | Dijkstra, coûts de terrain, points de mouvement |
| 3 | [03-algorithme-de-combat.md](./03-algorithme-de-combat.md) | Puissance d'attaque, résolution d'un duel, élimination |
| 4 | [04-boucle-de-jeu-et-controles.md](./04-boucle-de-jeu-et-controles.md) | Superviseur, vue, touches clavier, flux utilisateur |
| 5 | [05-journal-des-mises-a-jour.md](./05-journal-des-mises-a-jour.md) | Liste chronologique des corrections et évolutions |
| 6 | [06-installation-et-tests.md](./06-installation-et-tests.md) | Java 21, scripts, tests unitaires et d'acceptation |
| 7 | [09-stats-et-affichage-combat.md](./09-stats-et-affichage-combat.md) | Stats live, détail des combats, écran de fin |

---

## Lancer le projet

```bash
cd ai-2026
./run-game.sh
```

**Prérequis :** Java 21 (le moteur graphique `ai-engine-v1.2.0.jar` est compilé pour Java 21).

---

## Carte et fichiers utiles

| Ressource | Chemin |
|-----------|--------|
| Carte niveau 1 | `resources/maps/level-1.txt` |
| Carte entraînement 3×3 | `resources/maps/level-0.txt` |
| Sprites unités | `resources/sprites/*.png` |
| Code domaine (règles du jeu) | `src/helmosdeep/domains/` |
| Superviseur (logique UI → modèle) | `src/helmosdeep/supervisors/` |
| Vue graphique | `src/helmosdeep/views/` |
| Tests déplacement | `tests/helmosdeep/domains/MoveManagerTest.java` |
| Tests combat | `tests/helmosdeep/domains/UnitCombatTest.java` |

---

## Principe architectural (résumé)

```
Utilisateur (clavier)
        ↓
   GameScene (vue)
        ↓ événements
   GameSupervisor
        ↓ modifie d'abord
   HelmosDeepGame + MiddleEarth + Unit + MoveManager
        ↓ puis rafraîchit
   GameScene (sprites, hexagones, panneaux)
```

Le **domaine** ne connaît pas l'interface graphique. Le **superviseur** fait le lien : il met à jour le modèle, puis la vue.

---

## Symboles sur la carte (`level-1.txt`)

**Terrain** (lignes 2–6) : `L` plaine, `F` forêt, `M` montagne.

**Unités** (lignes 8–12) :

| Lettre | Unité | Armée | Type |
|--------|-------|-------|------|
| A | Aragorn | Hommes | Général |
| G | Gondoriens | Hommes | Moyenne |
| R | Rohirrim | Hommes | Légère |
| E | Ents | Hommes | Lourde |
| S | Sauron | Mordor | Général |
| O | Orcs | Mordor | Moyenne |
| W | Wargs | Mordor | Légère |
| T | Trolls | Mordor | Lourde |
| . | (vide) | — | — |

---

## Questions fréquentes

**Pourquoi mon unité disparaît après un combat ?**  
→ Voir [03-algorithme-de-combat.md](./03-algorithme-de-combat.md) et [09-stats-et-affichage-combat.md](./09-stats-et-affichage-combat.md) : le panneau Situation affiche le détail `(force+alliés+dé vs ...)`.

**Pourquoi je ne peux pas marcher sur une case avec un ennemi ?**  
→ Le déplacement ne cible que les cases **vides**. Pour combattre, il faut **Espace** sur l'ennemi adjacent (voir [04-boucle-de-jeu-et-controles.md](./04-boucle-de-jeu-et-controles.md)).

**Pourquoi Aragorn / Sauron n'attaquent pas ?**  
→ Les **généraux** ne peuvent pas initier une attaque ; ils participent seulement à la défense (bonus d'alliés voisins pour le calcul de puissance).
