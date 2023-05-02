use clap::{arg, Command};

use crate::enums::{Difficulty, Technology};

mod enums;

fn cli() -> Command {
    Command::new("cli")
        .about("A CLI for WrongSecrets")
        .subcommand_required(true)
        .arg_required_else_help(true)
        .allow_external_subcommands(true)
        .subcommand(
            Command::new("challenge")
                .about("Create a new challenge")
                .arg_required_else_help(true)
                .arg(
                    arg!(--"difficulty" <DIFFICULTY>)
                        .short('d')
                        .num_args(0..=1)
                        .value_parser(clap::builder::EnumValueParser::<Difficulty>::new())
                        .num_args(0..=1)
                        .default_value("easy")
                )
                .arg(
                    arg!(--"technology" <TECHNOLOGY>)
                        .short('t')
                        .value_parser(clap::builder::EnumValueParser::<Technology>::new())
                        .num_args(0..=1)
                        .require_equals(true)
                        .default_value("git")
                )
        )
}

fn main() {
    let matches = cli().get_matches();

    match matches.subcommand() {
        Some(("challenge", sub_matches)) => {
            println!(
                "Create new challenge with difficulty: {}",
                sub_matches.get_one::<Difficulty>("difficulty").expect("")
            );
        }
        _ => unreachable!()
    }
}
