#!/bin/bash
# Lance la démo console des déplacements (depuis le dossier ai-2026).
set -e
cd "$(dirname "$0")"

mkdir -p bin

echo "Compilation..."
find src/helmosdeep/domains src/helmosdeep/util -name "*.java" > sources.txt
javac -d bin -cp src @sources.txt
rm sources.txt

echo ""
echo "Exécution (carte level-0 par défaut)..."
java -cp bin helmosdeep.domains.MoveDemo "$@"
