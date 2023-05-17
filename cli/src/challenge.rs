use std::collections::BTreeMap;
use std::fs::File;
use std::io::Write;
use std::path::PathBuf;

use handlebars::Handlebars;
use walkdir::WalkDir;

use crate::{Difficulty, Platform, Technology};

#[derive(Debug)]
pub struct Challenge {
    pub number: u8,
    pub technology: Technology,
    pub difficulty: Difficulty,
    pub platform: Platform,
}

impl Challenge {
    fn create_java_sources(&self, project_directory: &PathBuf) {
        let challenge_source_path =
            project_directory
                .join("src/main/java/org/owasp/wrongsecrets/challenges");

        let (handlebars, data) = self.init_handlebars(project_directory);
        let challenge_source_content = handlebars
            .render("challenge", &data)
            .expect("Unable to render challenge template");
        let mut class_file = File::create(
            challenge_source_path
                .join(self.platform.to_string())
                .join(format!("Challenge{}.java", &self.number.to_string()))
        )
            .expect("Unable to create challenge source file");
        class_file
            .write(challenge_source_content.as_bytes())
            .expect("Unable to write challenge source file");
    }

    fn init_handlebars(&self, project_directory: &PathBuf) -> (Handlebars, BTreeMap<String, String>) {
        const CHALLENGE_TEMPLATE: &str = "src/main/resources/challenge.hbs";
        let mut handlebars = Handlebars::new();
        handlebars
            .register_template_file(
                "challenge",
                project_directory.join(CHALLENGE_TEMPLATE),
            )
            .unwrap();
        let mut data: BTreeMap<String, String> = BTreeMap::new();
        data.insert("challenge_number".to_string(), self.number.to_string());
        data.insert("platform".to_string(), self.platform.to_string().to_lowercase());
        data.insert("difficulty".to_string(), self.difficulty.to_string().to_uppercase());
        data.insert("technology".to_string(), self.technology.to_string().to_uppercase());

        (handlebars, data)
    }

    fn create_documentation(&self, project_directory: &PathBuf) {
        let challenge_documentation_path =
            project_directory
                .join("src/main/resources/explanations/");
        create_documentation_file(
            challenge_documentation_path.join(format!("challenge{}.adoc", self.number.to_string())),
        );
        create_documentation_file(
            challenge_documentation_path.join(format!("challenge{}_hint.adoc", self.number.to_string())),
        );
        create_documentation_file(
            challenge_documentation_path
                .join(format!("challenge{}_explanation.adoc", self.number.to_string())),
        );
    }
}

fn create_documentation_file(filename: PathBuf) {
    File::create(filename).expect("Unable to create challenge documentation file");
}

pub fn create_challenge(challenge: &Challenge, project_directory: &PathBuf) {
    let challenge_exists = check_challenge_exists(challenge, &project_directory);

    if challenge_exists {
        panic!("{:?} already exists", &challenge);
    }

    println!(
        "Creating {:?}",
        &challenge
    );
    challenge.create_java_sources(project_directory);
    challenge.create_documentation(project_directory);
}


//File API has `create_new` but it is still experimental in the nightly build, let loop and check if it exists for now
fn check_challenge_exists(challenge: &Challenge, project_directory: &PathBuf) -> bool {
    let challenges_directory =
        project_directory
            .join("src/main/java/org/owasp/wrongsecrets/challenges");
    let challenge_name = String::from("Challenge") + &challenge.number.to_string() + ".java";

    let challenge_exists = WalkDir::new(challenges_directory)
        .into_iter()
        .filter_map(|e| e.ok())
        .any(|e| match e.file_name().to_str() {
            None => false,
            Some(name) => name.eq(challenge_name.as_str()),
        });
    challenge_exists
}
