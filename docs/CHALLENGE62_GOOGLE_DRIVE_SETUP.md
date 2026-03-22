# Challenge 62: Google Service Account Setup Guide

This guide explains how to configure Challenge 62, which demonstrates privilege escalation via an MCP (Model Context Protocol) server using a Google Service Account to access restricted Google Drive documents.

## Overview

Challenge 62 shows how an MCP server configured with an overly-privileged Google Service Account allows callers to read Google Drive documents they are not directly authorized to access. The service account acts as a privilege escalation proxy.

## Prerequisites

- A Google Cloud project
- Owner or Editor role on the Google Cloud project (to create service accounts)
- A Google Drive document containing a secret

## Step 1: Create a Google Cloud Project (if needed)

If you don't have a Google Cloud project:

```bash
gcloud projects create YOUR_PROJECT_ID --name="WrongSecrets Challenge 62"
gcloud config set project YOUR_PROJECT_ID
```

## Step 2: Enable the Google Drive API

```bash
gcloud services enable drive.googleapis.com
```

## Step 3: Create a Service Account

```bash
gcloud iam service-accounts create wrongsecrets-challenge62 \
    --display-name="WrongSecrets Challenge 62 Drive Reader" \
    --description="Service account for WrongSecrets Challenge 62 - demonstrates MCP privilege escalation"
```

## Step 4: Create and Download a Service Account Key

```bash
gcloud iam service-accounts keys create challenge62-key.json \
    --iam-account=wrongsecrets-challenge62@YOUR_PROJECT_ID.iam.gserviceaccount.com
```

**⚠️ Security Warning**: Service account key files are sensitive credentials. Handle them carefully:
- Do not commit key files to version control
- Delete the key file after encoding it
- Rotate keys regularly

## Step 5: Create a Google Drive Document with the Secret

1. Go to [Google Drive](https://drive.google.com) and create a new Google Doc
2. Add your challenge secret as the document content (e.g., `my_wrongsecrets_challenge62_answer`)
3. Note the document ID from the URL:
   - URL format: `https://docs.google.com/document/d/DOCUMENT_ID/edit`
   - Copy the `DOCUMENT_ID` part

## Step 6: Share the Document with the Service Account

Share the Google Drive document with the service account's email address:

1. Open the document in Google Drive
2. Click **Share**
3. Add the service account email: `wrongsecrets-challenge62@YOUR_PROJECT_ID.iam.gserviceaccount.com`
4. Set the permission to **Viewer**
5. Click **Send**

Alternatively, use the Drive API via the CLI:
```bash
# Get the document ID from the URL
DOCUMENT_ID="your_document_id_here"
SA_EMAIL="wrongsecrets-challenge62@YOUR_PROJECT_ID.iam.gserviceaccount.com"

# Share using the Drive API (requires OAuth2 token)
curl -X POST "https://www.googleapis.com/drive/v3/files/${DOCUMENT_ID}/permissions" \
  -H "Authorization: Bearer $(gcloud auth print-access-token)" \
  -H "Content-Type: application/json" \
  -d "{\"role\": \"reader\", \"type\": \"user\", \"emailAddress\": \"${SA_EMAIL}\"}"
```

## Step 7: Encode the Service Account Key

Base64-encode the service account key file:

```bash
# On Linux/macOS:
SERVICE_ACCOUNT_KEY_B64=$(base64 -w 0 challenge62-key.json)

# On macOS (if the above doesn't work):
SERVICE_ACCOUNT_KEY_B64=$(base64 -i challenge62-key.json | tr -d '\n')

echo "Your base64-encoded key (use this as GOOGLE_SERVICE_ACCOUNT_KEY):"
echo "${SERVICE_ACCOUNT_KEY_B64}"
```

## Step 8: Configure WrongSecrets

Set the following environment variables when running WrongSecrets:

| Variable | Description | Example |
|----------|-------------|---------|
| `GOOGLE_SERVICE_ACCOUNT_KEY` | Base64-encoded service account JSON key | `eyJ0eXBlIjoic2VydmljZV9hY2...` |
| `GOOGLE_DRIVE_DOCUMENT_ID` | Google Drive document ID | `1vfHmi5lGoHogcjD0wxClZAjDy_qml_i2BtVrjVaklHc` |
| `WRONGSECRETS_MCP_GOOGLEDRIVE_SECRET` | The secret stored in the document | `my_wrongsecrets_challenge62_answer` |

### Running with Docker

```bash
docker run -p 8080:8080 -p 8090:8090 \
  -e GOOGLE_SERVICE_ACCOUNT_KEY="${SERVICE_ACCOUNT_KEY_B64}" \
  -e GOOGLE_DRIVE_DOCUMENT_ID="your_document_id" \
  -e WRONGSECRETS_MCP_GOOGLEDRIVE_SECRET="your_secret_here" \
  ghcr.io/owasp/wrongsecrets/wrongsecrets:latest-no-vault
```

### Running with Spring Boot (local development)

Add the following to your `application-local.properties` or set environment variables:

```properties
GOOGLE_SERVICE_ACCOUNT_KEY=<base64_encoded_key>
GOOGLE_DRIVE_DOCUMENT_ID=<document_id>
WRONGSECRETS_MCP_GOOGLEDRIVE_SECRET=<secret_in_document>
```

Or set environment variables directly:
```bash
export GOOGLE_SERVICE_ACCOUNT_KEY="${SERVICE_ACCOUNT_KEY_B64}"
export GOOGLE_DRIVE_DOCUMENT_ID="your_document_id"
export WRONGSECRETS_MCP_GOOGLEDRIVE_SECRET="your_secret_here"
./mvnw spring-boot:run
```

## Step 9: Clean Up the Key File

After encoding the key, delete the local key file:

```bash
rm challenge62-key.json
```

## Using the Default OWASP Document (for testing)

The default document ID configured in the application is the OWASP WrongSecrets Google Drive document:
- Document: https://docs.google.com/document/d/1vfHmi5lGoHogcjD0wxClZAjDy_qml_i2BtVrjVaklHc/edit

To use this document, your service account must have been granted read access to it by the OWASP WrongSecrets maintainers. For your own deployment, we recommend creating your own document as described above.

## Security Notes

1. **This is intentionally insecure for educational purposes**: In a real system, you should always authenticate and authorize MCP callers before granting access to external resources.

2. **Least Privilege**: The service account used in this challenge demonstrates what happens when you violate least privilege. In production, ensure service accounts only have the minimum permissions necessary.

3. **Never use production credentials**: Do not use service accounts that have access to production data for this challenge.

4. **Key rotation**: Regularly rotate service account keys to limit the window of exposure if a key is compromised.

## Verification

After configuration, verify the challenge works by calling the MCP endpoint:

```bash
curl -s -X POST http://localhost:8080/mcp62 \
  -H 'Content-Type: application/json' \
  -d '{"jsonrpc":"2.0","id":2,"method":"tools/call","params":{"name":"read_google_drive_document","arguments":{}}}'
```

The response should contain the document content with your secret.
