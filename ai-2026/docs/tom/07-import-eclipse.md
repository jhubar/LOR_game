# Importer le projet dans Eclipse

Guide pas à pas pour Tom. Le projet Eclipse se trouve dans le dossier **`ai-2026`** (pas à la racine `LOR_game`).

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

Ne pas importer `LOR_game` entier : Eclipse ne trouvera pas `.project` à la racine.

---

## Étape 2 — Import dans Eclipse

1. **File → Import…**
2. **General → Existing Projects into Workspace**
3. **Next**
4. **Select root directory** → **Browse…** → choisir le dossier `ai-2026`
5. Cocher le projet **`helmosdeep.dister.tom`**
6. **Finish**

Si Eclipse demande de **fermer un projet du même nom** déjà ouvert, accepter ou supprimer l’ancien import.

---

## Étape 3 — Configurer le JDK 21

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

## Étape 4 — Vérifier le classpath

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

## Étape 5 — Lancer le jeu

1. Ouvrir `src/helmosdeep/HelmosDeep.java`
2. Clic droit → **Run As → Java Application**
3. Le **working directory** doit être la racine du projet `ai-2026` (pour trouver `resources/`)

Si les images ou la carte ne se chargent pas :

1. **Run → Run Configurations…**
2. Sélectionner la config **HelmosDeep**
3. Onglet **Arguments** → **Working directory** → **Other…** → dossier `ai-2026`
4. **Apply → Run**

---

## Étape 6 — Lancer les tests

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
