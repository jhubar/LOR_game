@echo off
REM Ouvre Eclipse et importe automatiquement le projet HelmosDeep (ai-2026).
REM Double-clic ou : import-eclipse-windows.bat

setlocal EnableExtensions

REM --- A adapter si besoin ---
set "ECLIPSE=C:\Program Files\Eclipse\eclipse.exe"
set "WORKSPACE=%USERPROFILE%\eclipse-workspace"
REM Dossier du projet (= ai-2026, la ou se trouve ce script)
set "PROJECT_DIR=%~dp0"
set "PROJECT_DIR=%PROJECT_DIR:~0,-1%"

echo.
echo HelmosDeep - Import Eclipse (Windows)
echo ======================================
echo Projet : %PROJECT_DIR%
echo Workspace : %WORKSPACE%
echo.

if not exist "%PROJECT_DIR%\.project" (
    echo [ERREUR] Fichier .project introuvable.
    echo Vous devez lancer ce script depuis le dossier ai-2026.
    pause
    exit /b 1
)

if not exist "%ECLIPSE%" (
    echo [ERREUR] Eclipse introuvable :
    echo   %ECLIPSE%
    echo.
    echo Installez "Eclipse IDE for Java Developers" depuis https://www.eclipse.org/downloads/
    echo Puis editez la variable ECLIPSE en haut de ce fichier .bat
    pause
    exit /b 1
)

if not exist "%WORKSPACE%" mkdir "%WORKSPACE%"

echo Lancement d'Eclipse avec import automatique...
echo Fermez Eclipse s'il est deja ouvert, puis relancez ce script si besoin.
echo.

start "" "%ECLIPSE%" -data "%WORKSPACE%" -import "%PROJECT_DIR%"

endlocal
