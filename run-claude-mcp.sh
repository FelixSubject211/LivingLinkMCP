#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(grep '^projectDir=' "$SCRIPT_DIR/local.properties" | cut -d'=' -f2-)"

cd "$PROJECT_DIR" || exit 1

export LIVINGLINK_MCP_TRANSPORT=stdio
export LIVINGLINK_STDIO_USER_ID=felix
export LIVINGLINK_STDIO_USERNAME=Felix
export LIVINGLINK_MONGO_CONNECTION_STRING=mongodb://localhost:27017
export LIVINGLINK_MONGO_DATABASE=livinglink

./build/install/livinglink/bin/livinglink \
    | sed -u '/^kotlin-logging: initializing/d'