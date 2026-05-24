#!/bin/bash
# Lance les tests unitaires de combat (sans JUnit, depuis ai-2026).
set -e
cd "$(dirname "$0")"
mkdir -p bin
find src/helmosdeep/domains src/helmosdeep/util -name "*.java" > sources.txt
javac -d bin -cp "resources/libs/ai-engine-v1.2.0.jar:src" @sources.txt
rm sources.txt
java -cp bin helmosdeep.domains.CombatSelfTest
