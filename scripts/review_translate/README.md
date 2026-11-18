# WrongSecrets Translation Side-by-Side Viewer

A static GitHub Pages app to compare Dutch translation files (`*_nl.*`) with their matching original files in [OWASP/WrongSecrets](https://github.com/OWASP/WrongSecrets).

## Features

- Automatically scans the selected repository ref for files ending in `_nl` before the extension (or at the end of an extensionless filename).
- Pairs `README_nl.md` with `README.md`, for example.
- Shows original and translation side by side, with copy buttons.
- Filters the Dutch file list.
- Accepts a branch, tag, or commit SHA.
- No server, API key, or write access; uses the public GitHub REST API.

## Shareable URLs

State is kept in the query string: `?repo=OWASP/WrongSecrets&ref=master&lang=de&file=path/to/README_de.md`. Opening such a link loads that repo, ref (branch, tag or commit), language and selected file.

The right-hand side is always compared against English: the `_en` variant if present, otherwise the unsuffixed base file. Any language suffix works.

## Run locally

Because the app calls the public GitHub API, serve this directory over HTTP rather than opening `index.html` as a `file://` URL. For example:

```bash
python3 -m http.server 8000
```

Or, with Node.js, from this directory:

```bash
npm run serve
```

Then open `http://localhost:8000`.

## Publish with GitHub Pages

1. Create a repository and add `index.html`, `style.css`, and `app.js` to its root.
2. Open **Settings → Pages**.
3. Under **Build and deployment**, select **Deploy from a branch**, choose your branch and `/ (root)`, then save.
4. Open the Pages URL when deployment completes.

To use this project directly inside a fork of WrongSecrets, add these files in a folder and configure Pages to serve that folder using your preferred static-site workflow.

## Usage

- **Repository:** `OWASP/WrongSecrets` (default); any public `owner/repository` can be entered.
- **Branch, tag, or commit:** defaults to `master`; enter another ref and select **Load files**.
- Type the exact suffix used in filenames in **Filename extension / language suffix** (for example `nl`, `es`, or `_de`) and select **Load files**. Select a matching translated file from the left list to load the pair.
- Use the filter field to narrow the list.

## Notes and limitations

- Uses unauthenticated GitHub API requests, which have rate limits. If requests fail with a rate-limit message, wait and retry.
- Only files with a matching original are listed. The suffix is expected immediately before the extension, e.g. `README_es.md` maps to `README.md`.
- GitHub's recursive tree endpoint can truncate very large trees; the app displays a notice if that happens.
- The viewer displays source text as text (not rendered Markdown or HTML) to keep the comparison predictable and avoid executing repository content.
- For binary/non-UTF-8 files, the text view may not be meaningful.
- This is an independent community utility, not an official OWASP project.
