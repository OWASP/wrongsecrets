// Later on we can read this from the Github repository to make it more flexible
// cache the values locally and add a flag `--force` to force reading the values again
// Other option is to include a text file attached in a zip file. This makes it a bit more
// error prone as we need to have that file in the same directory.
// Other option is to have these files as part of the source code of wrongsecrets as you need
// to pass the project folder anyway. Otherwise generating a new challenge makes no sense ;-)

use std::fmt;

#[derive(clap::ValueEnum, Clone, Debug)]
pub enum Technology {
    Git,
    Docker,
    ConfigMaps,
    Secrets,
    Vault,
    Logging,
    Terraform,
    CSI,
    CICD,
    PasswordManager,
    Cryptography,
    Binary,
    Frontend,
    IAM,
    Web3,
    Documentation,
}

#[derive(clap::ValueEnum, Copy, Clone, Debug, PartialEq, Eq)]
pub enum Difficulty {
    Easy,
    Normal,
    Hard,
    Expert,
    Master,
}

#[derive(clap::ValueEnum, Copy, Clone, Debug, PartialEq, Eq)]
pub enum Platform {
    Cloud,
    Docker,
    Kubernetes,
}

impl fmt::Display for Difficulty {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{:?}", self)
    }
}

impl fmt::Display for Technology {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{:?}", self)
    }
}

impl fmt::Display for Platform {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{:?}", self)
    }
}
