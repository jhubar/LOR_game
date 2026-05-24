# Importer le projet dans Eclipse

Guide pas à pas pour Tom. Le projet Eclipse se trouve dans le dossier **`ai-2026`** (pas à la racine `LOR_game`).

> **Fichier résumé à la racine du projet :** [`IMPORT-ECLIPSE.txt`](../IMPORT-ECLIPSE.txt)

---

## Le bouton « Terminer » / « Finish » reste grisé

Eclipse **n’active Terminer que si au moins un projet est coché** dans la liste.  
Liste vide ou case non cochée → bouton gris. Ce n’est pas un bug du projet.

### Diagnostic rapide

| Ce que Tom voit | Cause | Solution |
|-----------------|-------|----------|
| Liste **vide**, aucun projet | Mauvais dossier sélectionné | Browse → dossier **`ai-2026`** (voir ci-dessous) |
| Liste vide après avoir choisi `LOR_game` | Pas de `.project` à la racine | Cocher **« Search for nested projects »** **ou** Browse directement **`ai-2026`** |
| Projet visible mais **case décochée** | Rien n’est sélectionné | Cocher **`helmosdeep.dister.tom`** |
| Message *already exists in workspace* | Déjà importé une fois | Voir [Projet déjà dans le workspace](#projet-déjà-dans-le-workspace) |
| Repo cloné **dans** le workspace Eclipse | Import classique inadapté | Utiliser [Méthode B — Open Projects](#méthode-b--open-projects-from-file-system-recommandée-si-import-grisé) |

### Vérifier le bon dossier (Finder / Explorateur)

Ouvrir **`ai-2026`** et confirmer la présence de :

```
.project          ← obligatoire (fichier caché sur Mac : Cmd+Shift+.)
.classpath
src/
tests/
resources/
```

Si **`.project` est absent**, Tom n’a pas la bonne copie du projet (Git incomplet ou mauvais zip).

---

## Méthode A — Import (projet en dehors du workspace)

1. **File → Import…**
2. **General → Existing Projects into Workspace**
3. **Next**
4. **Select root directory** → **Browse…**
5. Choisir le dossier **`ai-2026`** (chemin du type `…/LOR_game/ai-2026`)
6. **Cocher** la case devant **`helmosdeep.dister.tom`** dans le tableau
7. **Finish** / **Terminer** (actif seulement si l’étape 6 est faite)

**Option si Tom a sélectionné `LOR_game` par erreur :**  
cocher **« Search for nested projects »** → le projet **`helmosdeep.dister.tom`** doit apparaître → le cocher → **Finish**.

---

## Méthode B — Open Projects from File System (recommandée si Import grisé)

À utiliser si le dépôt Git est **déjà copié dans** le dossier workspace d’Eclipse (cas fréquent en cours).

1. **File → Open Projects from File System…**  
   (parfois : **File → Import… → General → Projects from Folder or Archive**)
2. **Directory…** → sélectionner **`ai-2026`**
3. Cocher **`helmosdeep.dister.tom`**
4. **Finish**

---

## Méthode C — Ligne de commande (Terminal / CMD)

Utile si l’import graphique bloque (bouton Terminer grisé) ou pour automatiser l’ouverture.

### 1. Vérifier que le projet Eclipse est bien là

**macOS / Linux (Terminal) :**

```bash
cd /chemin/vers/LOR_game/ai-2026
ls -la .project .classpath
ls src tests resources
```

**Windows (CMD ou PowerShell) :**

```cmd
cd C:\chemin\vers\LOR_game\ai-2026
dir /a .project .classpath
dir src tests resources
```

Si `.project` est introuvable → mauvais dossier ou copie incomplète.

### 2. Cloner le dépôt (si pas encore fait)

```bash
git clone <url-du-repo> LOR_game
cd LOR_game/ai-2026
```

### 3. Ouvrir Eclipse **avec import automatique**

Eclipse accepte l’option **`-import`** au démarrage.

**macOS :**

```bash
/Applications/Eclipse.app/Contents/MacOS/eclipse \
  -data "$HOME/eclipse-workspace" \
  -import "$HOME/Documents/LOR_game/ai-2026"
```

**Windows (CMD) :**

```cmd
"C:\Program Files\Eclipse\eclipse.exe" ^
  -data "C:\Users\Tom\eclipse-workspace" ^
  -import "C:\Users\Tom\Documents\LOR_game\ai-2026"
```

**Windows (PowerShell) :**

```powershell
& "C:\Program Files\Eclipse\eclipse.exe" `
  -data "$env:USERPROFILE\eclipse-workspace" `
  -import "$env:USERPROFILE\Documents\LOR_game\ai-2026"
```

Adapter :

- le chemin vers **eclipse.exe** / **Eclipse.app**
- `-data` → dossier workspace Eclipse de Tom
- `-import` → chemin **absolu** vers le dossier **`ai-2026`**

Au lancement, le projet **`helmosdeep.dister.tom`** doit apparaître dans le Package Explorer sans passer par Import.

### 4. Variante : projet déjà dans le workspace

Si Tom a cloné le repo **directement dans** son workspace Eclipse :

```text
~/eclipse-workspace/LOR_game/ai-2026/
```

Alors :

```bash
/Applications/Eclipse.app/Contents/MacOS/eclipse \
  -data "$HOME/eclipse-workspace"
```

Puis dans Eclipse : **File → Open Projects from File System…**  
Ou en une ligne (Eclipse récent) :

```bash
eclipse -data "$HOME/eclipse-workspace" -import "$HOME/eclipse-workspace/LOR_game/ai-2026"
```

### 5. Compiler / tester **sans Eclipse** (Terminal / CMD)

Pour vérifier que le code fonctionne avant Eclipse (Java 21 requis pour le jeu graphique) :

**macOS / Linux :**

```bash
cd ai-2026
./run-move-tests.sh
./run-combat-tests.sh
./run-game.sh
```

**Windows (Git Bash ou WSL)** — mêmes scripts si bash est disponible.

**Windows (CMD pur)** — compilation manuelle :

```cmd
cd C:\chemin\vers\LOR_game\ai-2026
mkdir bin
dir /s /b src\*.java > sources.txt
javac -d bin -cp "resources\libs\*;src" @sources.txt
java -cp "bin;resources\libs\*" helmosdeep.HelmosDeep
```

(`javac` et `java` doivent être en **Java 21** — vérifier avec `java -version`.)

### 6. Configurer Java 21 en CMD (Windows)

```cmd
where java
java -version
```

Si ce n’est pas la 21, installer [Temurin JDK 21](https://adoptium.net/) puis :

```cmd
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%
java -version
```

**macOS :**

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
java -version
```

---

## Projet déjà dans le workspace

Si **`helmosdeep.dister.tom`** apparaît déjà dans le **Package Explorer** (grisé ou fermé) :

- **Clic droit → Open Project**  
  Pas besoin d’importer à nouveau.

Pour **réimporter** après un échec :

1. Clic droit sur l’ancien projet → **Delete**
2. **Ne pas** cocher « Delete project contents on disk »
3. Refaire Méthode A ou B

---

---

## Prérequis

| Élément | Détail |
|---------|--------|
| **Eclipse IDE for Java Developers** | Version récente (2023-06 ou plus) avec support **Java 21** |
| **JDK 21** | Obligatoire — le moteur `ai-engine-v1.2.0.jar` est compilé pour Java 21 |
| **JUnit 5** | Inclus dans Eclipse Java ; nécessaire pour lancer les tests |

### Installer Java 21 (macOS)

1. Télécharger [Eclipse Temurin JDK 21](https://adoptium.net/) ou Oracle JDK 21.
2. Installer le `.pkg` / `.dmg`.
3. Vérifier dans un terminal :

```bash
/usr/libexec/java_home -V
java -version
```

Vous devez voir une ligne avec **21**.

---

## Étape 1 — Ouvrir le bon dossier

Le projet Eclipse = dossier **`ai-2026`**.

```
LOR_game/
└── ai-2026/          ← importer CE dossier
    ├── .project
    ├── .classpath
    ├── src/
    ├── tests/
    └── resources/
```

Ne pas importer `LOR_game` seul : Eclipse ne trouvera pas `.project` à la racine (sauf avec « Search for nested projects »).

---

## Étape 2 — Configurer le JDK 21

1. Clic droit sur le projet → **Properties**
2. **Java Build Path → Libraries**
3. Sélectionner **JRE System Library**
4. **Edit…** → **Installed JREs…**
5. **Add… → Standard VM** → indiquer le chemin du JDK 21  
   (ex. macOS : `/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home`)
6. Cocher JDK 21 → **Apply**
7. Choisir **Execution environment : JavaSE-21** pour le JRE du projet
8. **Apply and Close**

Vérifier aussi :

- **Properties → Java Compiler** → **Compiler compliance level : 21**

(C’est déjà dans `.settings/org.eclipse.jdt.core.prefs`, mais le JRE doit exister sur la machine.)

---

## Étape 3 — Vérifier le classpath

**Properties → Java Build Path → Libraries** doit contenir :

| Entrée | Rôle |
|--------|------|
| JRE System Library [JavaSE-21] | Exécution |
| `src` | Code source |
| `tests` | Tests |
| JUnit 5 | Tests JUnit |
| `resources/libs/*.jar` | ai-engine, Fest, ArchUnit, Log4j… |

Si **JUnit 5** affiche une erreur (point d’exclamation) :

1. **Add Library… → JUnit → JUnit 5** → **Finish**
2. Supprimer l’ancienne entrée JUnit cassée si doublon

Si un **jar** dans `resources/libs/` est marqué introuvable :

- Vérifier que le dossier `resources/libs/` est bien présent (notamment `ai-engine-v1.2.0.jar`)
- **Refresh** (F5) sur le projet

---

## Étape 4 — Lancer le jeu

1. Ouvrir `src/helmosdeep/HelmosDeep.java`
2. Clic droit → **Run As → Java Application**
3. Le **working directory** doit être la racine du projet `ai-2026` (pour trouver `resources/`)

Si les images ou la carte ne se chargent pas :

1. **Run → Run Configurations…**
2. Sélectionner la config **HelmosDeep**
3. Onglet **Arguments** → **Working directory** → **Other…** → dossier `ai-2026`
4. **Apply → Run**

---

## Étape 5 — Lancer les tests

| Tests | Comment |
|-------|---------|
| Un test | Clic droit sur la classe → **Run As → JUnit Test** |
| Tous les tests du package | Clic droit sur `tests/helmosdeep` → **Run As → JUnit Test** |
| Tests d’acceptation AI-1/2/3 | `AI1Test.java`, etc. (interface graphique — ne pas toucher la souris pendant l’exécution) |

Tests unitaires sans Eclipse (terminal) :

```bash
cd ai-2026
./run-move-tests.sh
./run-combat-tests.sh
```

---

## Problèmes fréquents

### Bouton Terminer / Finish grisé

→ Voir la section [Le bouton « Terminer » reste grisé](#le-bouton-terminer--finish-reste-grisé) en haut de ce guide.

### « Invalid project description » / import impossible

- Importer **`ai-2026`**, pas le parent.
- Le fichier `.project` doit être présent dans le dossier sélectionné.

### Erreurs « Plug-in PMD » / builders manquants

**Corrigé** : le `.project` ne référence plus les plugins PMD (optionnels).  
Si l’erreur persiste avec une **ancienne copie** du projet :

1. Fermer Eclipse
2. Remplacer `.project` par la version à jour du dépôt
3. Rouvrir et réimporter

### `UnsupportedClassVersionError` (version 65.0)

→ Eclipse utilise Java 17 ou moins. Passer le projet et le JRE en **Java 21** (étape 3).

### `ClassNotFoundException: ai.engine.SwingGameLoop`

→ `ai-engine-v1.2.0.jar` absent du classpath. Vérifier **Java Build Path → Libraries**.

### Erreurs de compilation sur les tests Fest / ArchUnit

→ Les jars dans `resources/libs/` doivent tous être présents. **Refresh** le projet.

### Espace / clavier inactif en jeu

→ Cliquer une fois sur la carte pour donner le focus à `GameScene`.

### Projet grisé avec un « x »

→ **Clic droit → Close Project**, puis **Open Project**.

---

## Checklist rapide Tom

- [ ] JDK 21 installé et sélectionné dans Eclipse  
- [ ] Import depuis le dossier **`ai-2026`**  
- [ ] Projet **`helmosdeep.dister.tom`** visible sans erreur rouge sur `ai-engine-v1.2.0.jar`  
- [ ] JUnit 5 OK dans Build Path  
- [ ] `HelmosDeep.java` se lance (Run As → Java Application)  
- [ ] `MoveManagerTest` passe (Run As → JUnit Test)  

---

## Besoin d’aide ?

Indiquer le **message d’erreur exact** (capture ou copier-coller depuis la vue **Problems** d’Eclipse) — les causes les plus courantes sont Java ≠ 21, mauvais dossier importé, ou jar manquant.
