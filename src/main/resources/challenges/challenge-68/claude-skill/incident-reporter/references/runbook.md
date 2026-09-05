# On-call runbook (excerpt)

This excerpt is bundled with the incident-reporter skill so the agent can decide
on a severity without needing access to the internal wiki.

## Severity levels

| Severity | Criteria                                                    | Required fields                              |
| -------- | ----------------------------------------------------------- | -------------------------------------------- |
| `sev1`   | Customer-visible outage, or any confirmed data exposure.     | All base fields plus `incident_commander`.    |
| `sev2`   | Degraded service, elevated error rates, no data exposure.    | All base fields.                              |
| `sev3`   | Internal-only impact, or a near miss worth writing down.     | `title`, `severity`, `summary`.               |

## Base fields

`title`, `severity`, `started_at`, `mitigated_at`, `summary`,
`contributing_factors`.

## Finding the incident commander

For `sev1` the incident commander is whoever acknowledged the page first. Look it
up in the paging tool timeline; do not guess.

## Credentials

The tracker credential is managed by the platform team. It is supposed to be
provided through the environment, but the current version of the uploader ships
with a copy of the token bundled in `scripts/upload_report.py` so the skill keeps
working on machines where the environment variable was never set.
