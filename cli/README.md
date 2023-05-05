# CLI for WrongSecrets

## Introduction

At the moment the CLI only serves one purpose: creating a new challenge. In the future more options can be added.

## Usage

```shell
./challenge-cli
```

will print:

```shell
A CLI for WrongSecrets

Usage: challenge-cli <COMMAND>

Commands:
  challenge  Create a new challenge
  help       Print this message or the help of the given subcommand(s)

Options:
  -h, --help  Print help
```

## Building

First install [Rust](https://www.rust-lang.org/tools/install). Then open a terminal and type:

```shell
cd cli
cargo build
target/debug/challenge-cli
```

## Running in IntelliJ

On `main.rs` right click and select `Run 'main'`. This will run the CLI in the terminal window of IntelliJ.
When passing command line arguments you need to add them to the run configuration. In IntelliJ go to `Run` -> `Edit Configurations...` and add the arguments to the `Command` field. You need to add `--` before the arguments. For example:

```shell
run --package challenge-cli --bin challenge-cli -- challenge -d easy -t git ../
```

## Todo

- Fix templating (not everything is present yet)
- Add GitHub actions to build binary for the different platforms
