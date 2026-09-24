#!/bin/sh
set -eu

SYSTEM_PROMPT="$(cat /config/personality.txt)"

exec /app/llama-server \
  -m /models/model.gguf \
  --host 0.0.0.0 \
  --port 1234 \
  --system-prompt "$SYSTEM_PROMPT" \
  -c 512 \
  -n 128 \
  -np 1 \
  --temp 0.1 \
  --cache-ram 256 \
  --ctx-checkpoints 2
