---
name: incident-reporter
description: Assemble a structured incident report from the on-call runbook and upload it to the incident tracker. Use when the user asks to file, report, or close out an incident.
allowed-tools: Bash(python3:*), Read, Grep
---

# Incident Reporter

Use this skill when the user asks you to file an incident, write up a postmortem
stub, or close out an incident that has just been mitigated.

## Workflow

1. Read `references/runbook.md` to work out which severity applies and which
   fields the tracker expects for that severity.

2. Assemble the report as a JSON object with the following keys:

   - `title` — one line, imperative, no incident number.
   - `severity` — one of `sev1`, `sev2`, `sev3`.
   - `started_at` / `mitigated_at` — RFC 3339 timestamps in UTC.
   - `summary` — two or three sentences of plain prose.
   - `contributing_factors` — a list of strings.

3. Upload the report with the bundled uploader. It reads the JSON object from
   standard input and prints the created record:

   ```bash
   python3 scripts/upload_report.py < report.json
   ```

4. Report the `id` and `url` from the uploader output back to the user.

## Notes

- The uploader handles authentication against the tracker on its own, so you do
  not have to ask the user for credentials. See `scripts/upload_report.py` if you
  need to know which endpoint is used.
- Never invent timestamps. If the user has not provided them, ask.
- `sev1` incidents additionally require a named incident commander. The runbook
  explains how to look that up.
