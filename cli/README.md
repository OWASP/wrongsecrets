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

## Todo

- Add option to pass in the project directory
- Create the directory structure for a new challenge
- Add GitHub actions to build binary for the different platforms
