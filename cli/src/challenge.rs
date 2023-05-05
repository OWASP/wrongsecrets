use std::collections::BTreeMap;
use std::fs::File;
use std::io::Write;
use std::path::PathBuf;

use handlebars::Handlebars;
use walkdir::WalkDir;

use crate::{Difficulty, Platform, Technology};

pub struct Challenge {
    pub number: u8,
    pub technology: Technology,
    pub difficulty: Difficulty,
    pub project_directory: PathBuf,
    pub platform: Platform,
}

impl std::fmt::Display for Challenge {
    fn fmt(&self, f: &mut std::fmt::Formatter) -> std::fmt::Result {
        write!(f, "Technology: {}, Difficulty: {}", self.technology, self.difficulty)
    }
}

pub fn create_challenge(challenge: &Challenge) {
    let challenge_number = &challenge.number.to_string();
    let challenge_name = String::from("Challenge") + challenge_number + ".java";
    let challenge_exists = check_challenge_exists(challenge);

    if challenge_exists {
        panic!("{:?} already exists", challenge_name);
    }

    println!("Creating challenge {} in {}", challenge_number, challenge.project_directory.display());
    create_challenge_class_file(challenge, challenge_number, challenge_name);
    create_documentation_files(challenge, challenge_number);
}

fn create_documentation_files(challenge: &Challenge, challenge_number: &String) {
    let challenge_documentation_path = challenge.project_directory.join("src/main/resources/explanations/");
    create_documentation_file(challenge_documentation_path.join(format!("challenge{}.adoc", challenge_number)));
    create_documentation_file(challenge_documentation_path.join(format!("challenge{}_hint.adoc", challenge_number)));
    create_documentation_file(challenge_documentation_path.join(format!("challenge{}_explanation.adoc", challenge_number)));
}

fn create_documentation_file(filename: PathBuf) {
    File::create(filename).expect("Unable to create challenge documentation file");
}

fn create_challenge_class_file(challenge: &Challenge, challenge_number: &String, challenge_name: String) {
    const CHALLENGE_TEMPLATE: &str = "src/main/resources/challenge.hbs";
    let challenge_source_path = challenge.project_directory.join("src/main/java/org/owasp/wrongsecrets/challenges");

    let mut handlebars = Handlebars::new();
    handlebars.register_template_file("challenge", challenge.project_directory.join(CHALLENGE_TEMPLATE)).unwrap();
    let mut data = BTreeMap::new();
    data.insert("challenge_number".to_string(), challenge_number);
    let challenge_source_content = handlebars.render("challenge", &data).expect("Unable to render challenge template");
    let mut class_file = File::create(challenge_source_path.join(challenge.platform.to_string()).join(challenge_name)).expect("Unable to create challenge source file");
    class_file.write(challenge_source_content.as_bytes()).expect("Unable to write challenge source file");
}

//File API has `create_new` but it is still experimental in the nightly build, let loop and check if it exists for now
fn check_challenge_exists(challenge: &Challenge) -> bool {
    let challenges_directory = challenge.project_directory.join("src/main/java/org/owasp/wrongsecrets/challenges");
    let challenge_name = String::from("Challenge") + &challenge.number.to_string() + ".java";

    let challenge_exists = WalkDir::new(challenges_directory)
        .into_iter()
        .filter_map(|e| e.ok())
        .any(|e| {
            match e.file_name().to_str() {
                None => { false }
                Some(name) => {
                    name.eq(challenge_name.as_str())
                }
            }
        });
    challenge_exists
}
