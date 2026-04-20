# Revise contributor rankings

## About
This script is used to generate the contributor-list for home.html and for the various markdown pages on Github where we list the contributors: [www-wrongsecrets](https://github.com/OWASP/www-project-wrongsecrets/blob/main/index.md#contributors) [wrongsecrets](https://github.com/OWASP/wrongsecrets/blob/master/README.md#special-thanks--contributors).

## Go version

The script has been converted to a Go CLI that generates:
- `contributors_file.html`
- `contributors_file.md`

## Installation and run

1. Make sure you get a fine grained access token from github to read the repositories (https://github.com/settings/tokens?type=beta):
- go to https://github.com/settings/tokens?type=beta
- create a token and make sure it has "repository access" only
- store the token in a safe secrets manager and export it before you run it (`export USER_TOKEN=github_pat<redacted>`)

2. Build and run:

```sh
make build
./bin/sort-contributors
```

3. You can also pass the token via CLI flag:

```sh
./bin/sort-contributors -token github_pat<redacted>
```

4. You can choose a custom output directory:

```sh
./bin/sort-contributors -output-dir ./generated
```

5. The tool supports standard Go help output:

```sh
./bin/sort-contributors -h
```

6. You can run with Makefile (pass optional args through `ARGS`):

```sh
make run ARGS="-output-dir ./generated"
```

## Make targets

- `make build`: compile binary to `bin/sort-contributors`
- `make run`: build and run the binary (supports `ARGS="..."`)
- `make lint`: run `golangci-lint`
- `make test`: run unit tests
- `make vet`: run `go vet`
- `make security`: run `gosec`
