#!/bin/bash
# Lance le jeu graphique HelmosDeep (Java 21 requis).
set -e
cd "$(dirname "$0")"

if ! java -version 2>&1 | grep -q 'version "21'; then
  echo "Java 21 est requis (ai-engine-v1.2.0.jar). Installez JDK 21 ou lancez depuis Eclipse."
  exit 1
fi

mkdir -p bin
find src -name "*.java" > sources.txt
javac -d bin -cp "resources/libs/*:src" @sources.txt
rm sources.txt

echo "Lancement de HelmosDeep..."
java -cp "bin:resources/libs/*" helmosdeep.HelmosDeep
