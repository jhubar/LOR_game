# Algorithme de déplacement (Dijkstra)

## Objectif

Calculer si une unité peut atteindre une case destination avec ses **points de mouvement (PM)** restants, en tenant compte :

- de la **topologie hexagonale** (6 voisins par case) ;
- du **coût du terrain** ;
- de la **règle spéciale des unités lourdes** ;
- des **cases occupées** (impossible de traverser une autre unité).

Fichier principal : `src/helmosdeep/domains/MoveManager.java`

---

## Grille hexagonale — voisins

Chaque case a des coordonnées `(x, y)` où `x` = ligne, `y` = colonne.

La méthode `Coordinates.getNeighbours()` retourne **6 voisins**.  
La disposition dépend de la parité de la ligne (`x % 2`) : c'est une grille **hexagonale décalée** (style « odd-r » ou variante proche).

```
Exemple conceptuel (ligne paire vs impaire) :

    voisins incluent (x, y±1), (x±1, y)
    + deux diagonales selon parité de x
```

Sans `equals()` et `hashCode()` corrects sur `Coordinates`, l'algorithme ne fonctionnerait pas (clés de `HashMap` invalides). Ces méthodes ont été ajoutées/corrigées pour ça.

---

## Coût d'une case

### Terrain (`ETileType`)

| Terrain | Coût PM |
|---------|---------|
| Plaine (`L`) | 1 |
| Forêt (`F`) | 2 |
| Montagne (`M`) | 3 |

### Unité lourde (`EUnitType.LOURDE`)

Les Ents et Trolls **ignorent le terrain** : chaque pas coûte **1 PM**, même en montagne.

Code dans `MiddleEarth.getMovementCost` :

```java
if (unitType == EUnitType.LOURDE) {
    return 1;
}
return getTileWithCoord(dest).getTilType().getCoutMvt();
```

### Points de mouvement par type d'unité

| Type | Force | PM max / tour |
|------|-------|----------------|
| Légère | 1 | 4 |
| Moyenne | 2 | 3 |
| Lourde | 3 | 2 |
| Général | 4 | 3 |

Le libellé affiché sous l'unité est `force-PMrestants` (ex. `3-2`).

---

## Cases traversables

`MiddleEarth.isPassable(coord, from)` :

- `false` si hors carte ;
- `true` si `coord == from` (rester sur place) ;
- `true` si la case est **vide** ;
- `false` si une **autre unité** occupe la case.

Conséquence : on ne peut **pas** se déplacer sur un ennemi. Il faut utiliser le **combat** (Espace sur ennemi adjacent).

---

## Algorithme : Dijkstra élagué

### Idée

On explore la carte à partir de la position source, en accumulant le coût des pas.  
On s'arrête dès qu'un chemin dépasse le budget `mvt` (PM restants).

C'est une variante de **Dijkstra** avec :

- file de priorité (`PriorityQueue`) triée par coût cumulé ;
- map `bestCosts` : meilleur coût connu pour chaque case ;
- élagage : si `newCost > mvt`, on n'explore pas ce voisin.

### Méthodes exposées

| Méthode | Rôle |
|---------|------|
| `lowerCost(src, dest, mvt, ...)` | Coût minimal vers `dest`, ou `-1` si impossible |
| `reachableCosts(src, mvt, ...)` | Toutes les cases atteignables + leur coût |
| `findPath(src, dest, mvt, ...)` | Chemin complet reconstruit (`Path`) |

### Pseudo-code

```
bestCosts[src] = 0
file ← (src, 0)

tant que file non vide :
    (coord, costSoFar) ← extraire le plus petit coût
    si costSoFar > bestCosts[coord] : ignorer (doublon périmé)

    pour chaque voisin de coord :
        si hors carte ou non traversable : continuer
        stepCost ← coût terrain (ou 1 si lourde)
        newCost ← costSoFar + stepCost
        si newCost > mvt : continuer
        si newCost < bestCosts[voisin] :
            bestCosts[voisin] ← newCost
            parent[voisin] ← coord
            file ← (voisin, newCost)
```

### Reconstruction du chemin (`Path.fromParents`)

Partir de `dest`, remonter via `parent` jusqu'à `src`, inverser la liste.

---

## Intégration dans la partie

`HelmosDeepGame.moveSelectedUnit(destination)` :

1. Vérifie qu'une unité est sélectionnée.
2. Appelle `moveManager.lowerCost(...)` avec les PM restants.
3. Si coût ≥ 0 : `middleEarth.moveUnit` + `spendMovement(cost)`.

Le superviseur appelle ensuite `view.moveUnit(...)` pour l'animation.

---

## Exemple concret

Unité lourde avec **2 PM** sur une plaine :

- Peut marcher sur 2 cases adjacentes (coût 1+1).
- Peut aussi faire 1 pas en montagne (coût 1 pour une lourde).

Unité moyenne avec **3 PM** :

- Plaine → plaine → forêt : coût 1+1+2 = 4 → **impossible** en un seul déplacement.

---

## Tests

`tests/helmosdeep/domains/MoveManagerTest.java` vérifie :

- coût 0 sur place ;
- voisin plaine = 1 ;
- case occupée = inaccessible (`-1`) ;
- budget insuffisant = `-1` ;
- lourde en montagne = coût 1 ;
- `findPath` reconstruit un chemin valide.

Lancer (avec Java 21 et classpath des libs) :

```bash
./run-move-tests.sh
```

---

## Schéma mental

```
        [Source PM=3]
           / | \
      coût1 coût2 coût3
         /    |     \
    cases atteignables si somme ≤ 3
    cases bloquées si unité présente
    cases ignorées si somme > 3
```
