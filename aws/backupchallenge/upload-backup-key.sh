#!/usr/bin/env bash
set -euo pipefail

BUCKET_NAME="${1:-backupchallenge}"
OBJECT_KEY="${2:-backups/id_ed25519}"
SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
KEY_FILE="${SCRIPT_DIR}/ed25519_backup_key"

if [[ ! -f "${KEY_FILE}" ]]; then
  echo "Missing fixture: ${KEY_FILE}" >&2
  exit 1
fi

aws s3 cp --only-show-errors "${KEY_FILE}" "s3://${BUCKET_NAME}/${OBJECT_KEY}"

ETAG="$(aws s3api head-object \
  --bucket "${BUCKET_NAME}" \
  --key "${OBJECT_KEY}" \
  --query ETag \
  --output text)"

echo "Uploaded ${KEY_FILE##*/} to s3://${BUCKET_NAME}/${OBJECT_KEY} (ETag: ${ETAG})"
echo "Participants can fetch it without credentials via https://${BUCKET_NAME}.s3.amazonaws.com/${OBJECT_KEY}"
