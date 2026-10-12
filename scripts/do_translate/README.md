# File Translator

A Go CLI for translating text files using Google Cloud Translation API.

## Requirements

- Go 1.27 or newer
- A Google Cloud project
- Cloud Translation API enabled
- Credentials with permission to use Cloud Translation

## Install dependencies

    go mod tidy

## The cheap manual way

Spet 1: make the binaries:

`make`

Step 2: create the explanations_bundle.txt:

`./bin/translate --construct`

Step 3: save the contexts in a docx file

`cat explanations_bundle.txt | pbcopy`

Step 4: Open word and paste the contents, save it to the disk as a docx file

Step 5: open https://translate.google.com/?sl=en&tl=nl&op=docs select a target langauge, upload the doc and complete the translation process and download the file.

Step 6: open the translated doc and copy the contents to a txt file. e.g. `explanations_bundle_nl.txt` and make sure the `@@@@ FILE:` part is not translated (if it is, reconstruct it to FILE again).

step 7: now deconstruct the new file (e.g. `explanations_bundle_nl`) with 

## Configure authentication when doing cloud based translation

Using the Google Cloud CLI:

    gcloud auth application-default login
    gcloud config set project YOUR_PROJECT_ID

Enable the Translation API:

    gcloud services enable translate.googleapis.com

Set the project ID:

    export GOOGLE_CLOUD_PROJECT=YOUR_PROJECT_ID

## Usage

Translate English to Dutch:

    go run ./cmd/translator \
      -input input.txt \
      -output output.nl.txt \
      -source en \
      -target nl

Let Google detect the source language:

    go run ./cmd/translator \
      -input input.txt \
      -output output.de.txt \
      -target de

Specify a project explicitly:

    go run ./cmd/translator \
      -project YOUR_PROJECT_ID \
      -input input.txt \
      -output output.fr.txt \
      -target fr

## File mode (manual translation via Google Translate)

Combine all explanation `.adoc` files into one file (run from this directory;
use `-dir` to override the explanations path, `-bundle` for the bundle name):

    go run ./cmd/translator --construct

Paste the bundle into Google Translate (keep the `@@@@ FILE: ... @@@@` lines),
save the result over the bundle, then recreate the files with a language suffix
(e.g. `challenge1_nl.adoc`):

    go run ./cmd/translator --destruct --language nl

## Build

    go build -o bin/translator ./cmd/translator

Then run:

    ./bin/translator \
      -input input.txt \
      -output output.nl.txt \
      -target nl

## Supported language codes

Examples:

- en: English
- nl: Dutch
- de: German
- fr: French
- es: Spanish
- it: Italian
- pt: Portuguese
- ja: Japanese
- zh: Chinese