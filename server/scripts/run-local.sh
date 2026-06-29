#!/bin/bash
set -euo pipefail

export JAVA_HOME="${JAVA_HOME:-/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home}"
export PATH="$JAVA_HOME/bin:$PATH"

cd "$(dirname "$0")/.."

if lsof -ti :8080 >/dev/null 2>&1; then
  echo "Stopping existing process on port 8080..."
  lsof -ti :8080 | xargs kill -9
fi

echo "Starting LearnForge backend (profile: local)..."
./mvnw spring-boot:run -DskipTests -Dspring-boot.run.profiles=local
