#!/bin/sh
set -eu

SYSTEM_PROMPT="$(cat /config/personality.txt)"

exec /app/llama-server \
  -m /models/model.gguf \
  --host 0.0.0.0 \
  --port 1234 \
  -c 256 \
  -n 64 \
  -np 1 \
  --temp 0.1
