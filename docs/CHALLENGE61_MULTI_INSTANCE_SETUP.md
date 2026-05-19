# Challenge61 Multi-Instance Setup Guide

This guide explains how to configure and run Challenge61, which demonstrates how hardcoded Telegram bot credentials can be discovered and exploited. The bot token is double-encoded in base64 to make it slightly more challenging but still discoverable through code inspection.

## Overview

This challenge supports running on multiple app instances (e.g., Arcane and WrongSecrets Heroku apps) using either polling (getUpdates) or webhooks.

## Option 1: Polling with getUpdates (Default - Works Out of Box)

The code uses update offsets to minimize conflicts between multiple app instances:
- No configuration needed
- Uses update offsets to minimize conflicts between instances
- Multiple instances can run simultaneously
- Less efficient but simpler setup
- `timeout=0` - No long polling, quick responses
- `limit=1` - Process one update at a time
- Offset acknowledgment - Marks updates as processed

**Status**: ✅ Code updated and tested

## Option 2: Webhook Solution (Recommended for Production)

### Step 1: Configure Each Heroku App

For **WrongSecrets Heroku app**:
```bash
heroku config:set CHALLENGE61_WEBHOOK_ENABLED=true -a wrongsecrets-app
heroku config:set CHALLENGE61_WEBHOOK_TOKEN=$(openssl rand -hex 32) -a wrongsecrets-app
```

For **Arcane Heroku app**:
```bash
heroku config:set CHALLENGE61_WEBHOOK_ENABLED=true -a arcane-app
heroku config:set CHALLENGE61_WEBHOOK_TOKEN=$(openssl rand -hex 32) -a arcane-app
```

### Step 2: Choose ONE App for the Webhook

You can only set ONE webhook URL per bot. Choose either WrongSecrets or Arcane:

**Option A: Use WrongSecrets app**
```bash
# Get your webhook token
WEBHOOK_TOKEN=$(heroku config:get CHALLENGE61_WEBHOOK_TOKEN -a wrongsecrets-app)

# Set the webhook
curl -X POST "https://api.telegram.org/bot8132866643:AAHJmvZqvvM9dI2rtBOu--WMZyMFTfHNo9I/setWebhook?url=https://your-wrongsecrets-app.herokuapp.com/telegram/webhook/challenge61&secret_token=$WEBHOOK_TOKEN"
```

**Option B: Use Arcane app**
```bash
# Get your webhook token
WEBHOOK_TOKEN=$(heroku config:get CHALLENGE61_WEBHOOK_TOKEN -a arcane-app)

# Set the webhook
curl -X POST "https://api.telegram.org/bot8132866643:AAHJmvZqvvM9dI2rtBOu--WMZyMFTfHNo9I/setWebhook?url=https://your-arcane-app.herokuapp.com/telegram/webhook/challenge61&secret_token=$WEBHOOK_TOKEN"
```

### Step 3: Verify Webhook

```bash
curl "https://api.telegram.org/bot8132866643:AAHJmvZqvvM9dI2rtBOu--WMZyMFTfHNo9I/getWebhookInfo"
```

### Step 4: Test

1. Open @WrongsecretsBot in Telegram
2. Send `/start`
3. Bot should respond: "Welcome! Your secret is: telegram_secret_found_in_channel"

## Alternative: Use Both Apps with getUpdates (Current Setup)

If you want both apps to be able to respond (not recommended but possible):

1. **Keep webhook disabled** (default)
2. **Accept that responses may be inconsistent** - whichever app polls first will respond
3. **The improved getUpdates code** minimizes conflicts with offset handling

## Troubleshooting

### Check if webhook is active
```bash
curl "https://api.telegram.org/bot8132866643:AAHJmvZqvvM9dI2rtBOu--WMZyMFTfHNo9I/getWebhookInfo"
```

### Remove webhook (to go back to getUpdates)
```bash
curl -X POST "https://api.telegram.org/bot8132866643:AAHJmvZqvvM9dI2rtBOu--WMZyMFTfHNo9I/deleteWebhook"
```

### View Heroku logs
```bash
heroku logs --tail -a wrongsecrets-app | grep Challenge61
heroku logs --tail -a arcane-app | grep Challenge61
```

## Recommendation

For **production with multiple apps**: Use webhook on ONE primary app (WrongSecrets).

For **development/testing**: The current getUpdates approach with offsets works fine.

## BotFather Configuration (Optional but Recommended)

### 1. Configure Commands

- Send `/setcommands` to @BotFather
- Select your bot
- Add: `start - Get the secret message`

### 2. Set Description

- Send `/setdescription` to @BotFather
- Select your bot
- Add: "OWASP WrongSecrets Challenge 61 - Demonstrates hardcoded bot credentials. Send /start to receive the secret!"

### 3. Set About Text

- Send `/setabouttext` to @BotFather
- Add: "Educational security challenge from OWASP WrongSecrets project"

## Testing the Bot

1. Find the bot: Search for @WrongsecretsBot in Telegram (or your bot username)
2. Send: `/start`
3. Receive: "Welcome! Your secret is: telegram_secret_found_in_channel"

## Creating a New Bot

If you need to create your own bot for testing:

1. Message @BotFather in Telegram
2. Send `/newbot`
3. Follow prompts to choose name and username
4. BotFather will provide a token like: `1234567890:ABCdefGHIjklMNOpqrsTUVwxyz`
5. Double-encode the token for use in this challenge:
   ```bash
   echo -n "YOUR_TOKEN" | base64 | base64
   ```
6. Replace the `encodedToken` value in the `getBotToken()` method in Challenge61.java
