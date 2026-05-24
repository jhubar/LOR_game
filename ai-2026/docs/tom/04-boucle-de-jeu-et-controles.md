# Boucle de jeu et contrôles

## Séparation curseur / unité sélectionnée

Deux concepts distincts :

| Concept | Géré par | Rôle |
|---------|----------|------|
| **Curseur** | `GameSupervisor` (`currentRow`, `currentCol`) | Case surlignée (hex actif), déplacée avec les flèches |
| **Unité sélectionnée** | `HelmosDeepGame.selectedUnit` | Unité prête à agir (déplacer ou attaquer) |

Le curseur peut être sur une case différente de l'unité sélectionnée.

Exemple :

1. Espace sur Rohirrim → Rohirrim sélectionné.
2. Flèches vers une case vide → curseur se déplace, Rohirrim reste sélectionné.
3. Espace → Rohirrim se déplace vers le curseur.

---

## Touches clavier

| Touche | Action |
|--------|--------|
| ◀ ▶ ▲ ▼ | Déplacer le curseur |
| **Espace** | Sélectionner / Désélectionner / Déplacer / **Attaquer** |
| **Entrée** | Fin de tour (alterne Mordor ↔ Hommes) |
| **Échap** | Retour menu principal |
| P / M | Zoom caméra |

Texte affiché dans le panneau **Commandes** de `GameScene`.

---

## Logique de la touche Espace (`GameSupervisor.onAction`)

```
si aucune unité sélectionnée :
    si case curseur = unité alliée → sélectionner
    sinon → rien

sinon (unité U sélectionnée) :
    si curseur = position de U → désélectionner

    sinon si curseur = ennemi adjacent → attaquer

    sinon si déplacement possible vers curseur → déplacer + désélectionner

    sinon si curseur = autre unité alliée → changer de sélection
```

---

## Fin de tour

`HelmosDeepGame.endTurn()` :

1. Inverse `mordorTurn`.
2. Efface `selectedUnit` et `lastCombatReport`.
3. Remet les PM de **toutes** les unités de l'armée qui commence à jouer.

Le superviseur :

- Désélectionne visuellement ;
- Place le curseur sur la première unité de la nouvelle armée (souvent Sauron au début).

---

## Focus clavier (correction importante)

**Problème initial :** Espace ne faisait rien.

**Cause :** Le focus clavier était sur les panneaux latéraux (`TitledPanel`), pas sur `GameScene`. Les touches n'atteignaient pas le listener.

**Correction dans `GameScene` :**

- `setFocusable(true)` sur la scène ;
- `setFocusable(false)` sur les panneaux ;
- `KeyListener` sur la scène ;
- `requestFocusInWindow()` à l'entrée en partie et au clic souris ;
- `resetBoard()` dans `onEnter` pour repartir d'une scène propre.

---

## Sprites et noms d'unités

**Problème initial :** Aragorn invisible (`aragon.png` introuvable).

**Cause :** Le nom dans `Unit.java` était `"Aragon"` mais le fichier est `aragorn.png`.  
`UnitTile` charge `resources/sprites/<nom en minuscules>.png`.

**Correction :** nom `"Aragorn"` (et `"Rohirrim"` pour correspondre à `rohirrim.png`).

---

## Disparition d'unité après déplacement (correction)

**Problème :** L'unité disparaissait après un déplacement réussi.

**Cause :** `UnitTile` gardait un identifiant lié à l'**ancienne** case. Après `moveTo`, le moteur ne retrouvait plus le sprite.

**Correction dans `GameScene.moveUnit` :**

1. Détacher l'ancien `UnitTile`.
2. Créer un **nouveau** `UnitTile` avec l'id de la destination (`UnitTile.formatId(toRow, toCol)`).
3. Animer du point de départ vers l'arrivée.

---

## Crash `ConcurrentModificationException` (correction)

**Problème :** Crash après déplacement + Entrée.

**Cause :** Un callback `.then(() -> newUnit.select())` enregistrait un tween **pendant** la mise à jour des tweens (`Tweens.update`), ce qui modifiait la collection en cours d'itération.

**Correction :** Suppression de ce `.then()` ; la sélection est gérée explicitement par le superviseur.

---

## Sélection bloquée après fin de tour (correction)

**Problème :** Impossible de resélectionner les Hommes après le tour du Mordor.

**Cause :** L'unité restait sélectionnée dans le **modèle** alors que la vue avait changé.

**Correction :**

- `endTurn()` met `selectedUnit = null` ;
- `GameSupervisor.onEndTurn()` appelle `clearSelection()` ;
- Changement de sélection si Espace sur une **autre** unité alliée.

---

## Animations (`Tweens`)

| Action | Animation |
|--------|-----------|
| Déplacement unité | Translation 300 ms |
| Suppression unité | Réduction largeur → 0, puis `detach` |
| Caméra | Déplacement fluide vers hex actif |
| Sélection | Pulsation contour (`UnitTile.select`) |

**Règle :** Ne pas enchaîner des `Tweens.instance().enqueue(...)` dans un `.then()` qui s'exécute pendant `update()` sans comprendre le cycle du moteur.

---

## Panneau Situation

Mis à jour à chaque action via `refreshStatusPanel()` :

| Ligne (typique) | Contenu |
|-----------------|---------|
| 1 | Titre match : « Le Mordor vs les Hommes » |
| 2 | Tour actuel |
| 3 | Coordonnées du curseur |
| 4 | Type de terrain sous le curseur (ex. `FORÊT [2]`) |
| 5 | Scores unités restantes / kills par armée |
| 6 | Dernier combat (si récent, même tour) |
| 7+ | Unité sélectionnée + description de son type |

Les tests d'acceptation AI-2 ne vérifient que les **4 premières lignes**.

---

## Cases atteignables (surbrillance)

Quand une unité alliée est **sélectionnée** et qu'il lui reste des PM :

1. `MoveManager.reachableCosts` calcule toutes les cases accessibles ;
2. `GameSupervisor` éclaircit leur couleur (par rapport à la couleur de terrain mémorisée) ;
3. À la désélection, fin de tour ou déplacement, les couleurs d'origine sont restaurées.

Cela aide à voir où l'unité peut se déplacer avant d'appuyer sur Espace.

---

## Schéma de la boucle de jeu

```
┌─────────────────────────────────────┐
│  Tour Mordor ou Hommes              │
│  ┌─────────────────────────────┐  │
│  │ Sélectionner unité (Espace)  │  │
│  │ Déplacer / Attaquer (Espace) │  │
│  │ Répéter pour d'autres unités │  │
│  └─────────────────────────────┘  │
│  Fin de tour (Entrée)             │
└─────────────────────────────────────┘
         ↓ si une armée à 0 unité
    Écran Fin de partie
```
