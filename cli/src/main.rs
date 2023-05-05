use std::path::PathBuf;

use clap::arg;
use clap::{Parser, Subcommand};

use crate::challenge::Challenge;
use crate::enums::{Difficulty, Platform, Technology};

mod enums;
mod challenge;

#[derive(Debug, Parser)]
#[command(name = "cli")]
#[command(about = "A CLI for WrongSecrets", long_about = None)]
struct Cli {
    #[command(subcommand)]
    command: Commands,
}

#[derive(Debug, Subcommand)]
enum Commands {
    #[command(arg_required_else_help = true, name = "challenge", about = "Create a new challenge")]
    ChallengeCommand {
        //We could infer this from the directory structure but another PR could already have added the challenge with this number
        #[arg(
        long,
        short,
        value_name = "NUMBER")]
        number: u8,
        #[arg(
        long,
        short,
        value_name = "DIFFICULTY",
        num_args = 0..=1,
        default_value_t = Difficulty::Easy,
        default_missing_value = "easy",
        value_enum
        )]
        difficulty: Difficulty,
        #[arg(
        long,
        short,
        value_name = "TECHNOLOGY",
        num_args = 0..=1,
        default_value_t = Technology::Git,
        default_missing_value = "git",
        value_enum
        )]
        technology: Technology,
        #[arg(
        long,
        short,
        value_name = "PLATFORM",
        num_args = 0..=1,
        value_enum
        )]
        platform: Platform,
        #[arg(required = true)]
        project_directory: PathBuf,
    }
}

fn main() {
    let args = Cli::parse();
    match args.command {
        Commands::ChallengeCommand {
            number,
            difficulty,
            technology,
            platform,
            project_directory
        } => {
            project_directory.try_exists().expect("Unable to find project directory");
            let challenge = Challenge { number, difficulty, technology, platform, project_directory };
            challenge::create_challenge(&challenge);
        }
    }
}
