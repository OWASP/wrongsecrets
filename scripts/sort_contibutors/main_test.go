package main

import (
	"os"
	"strings"
	"testing"
)

func TestResolveTokenPrecedence(t *testing.T) {
	t.Parallel()

	env := func(key string) string {
		switch key {
		case "USER_TOKEN":
			return "env-user"
		case "GITHUB_TOKEN":
			return "env-github"
		default:
			return ""
		}
	}

	token, err := resolveToken("flag-token", env)
	if err != nil {
		t.Fatalf("expected no error, got %v", err)
	}
	if token != "flag-token" {
		t.Fatalf("expected flag token, got %s", token)
	}

	token, err = resolveToken("", env)
	if err != nil {
		t.Fatalf("expected no error, got %v", err)
	}
	if token != "env-user" {
		t.Fatalf("expected USER_TOKEN value, got %s", token)
	}
}

func TestResolveTokenMissing(t *testing.T) {
	t.Parallel()

	token, err := resolveToken("", func(string) string { return "" })
	if err == nil {
		t.Fatalf("expected an error, got nil and token=%s", token)
	}
}

func TestResolveOutputDir(t *testing.T) {
	t.Parallel()

	outputDir, err := resolveOutputDir(".")
	if err != nil {
		t.Fatalf("expected no error, got %v", err)
	}
	if outputDir == "" {
		t.Fatal("expected output directory path, got empty string")
	}
}

func TestResolveOutputDirMissing(t *testing.T) {
	t.Parallel()

	outputDir, err := resolveOutputDir("   ")
	if err == nil {
		t.Fatalf("expected an error, got nil and outputDir=%s", outputDir)
	}
}

func TestMergeUsers(t *testing.T) {
	t.Parallel()

	input := []Contributor{
		{Username: "alice", Name: "Alice", Ranking: 40},
		{Username: "bob", Name: "Bob", Ranking: 120},
		{Username: "alice", Name: "Alice", Ranking: 80},
		{Username: "charlie", Name: "Charlie", Ranking: 20},
	}

	top, normal := mergeUsers(input)
	if len(top) != 2 {
		t.Fatalf("expected 2 top contributors, got %d", len(top))
	}
	if len(normal) != 1 {
		t.Fatalf("expected 1 normal contributor, got %d", len(normal))
	}
	if top[0].Username != "alice" || top[0].Ranking != 120 {
		t.Fatalf("unexpected top[0]: %+v", top[0])
	}
	if top[1].Username != "bob" || top[1].Ranking != 120 {
		t.Fatalf("unexpected top[1]: %+v", top[1])
	}
	if normal[0].Username != "charlie" {
		t.Fatalf("unexpected normal contributor: %+v", normal[0])
	}
}

func TestRenderMarkdown(t *testing.T) {
	t.Parallel()

	payload := renderMarkdown([]Contributor{
		{Username: "LittlePrimate", Name: "LittlePrimate"},
		{Username: "S3DFX-CYBER", Name: "S3DFX-CYBER"},
		{Username: "bhushan4work", Name: "Bhushan Agrawal"},
	}, "Translators")
	if !strings.Contains(payload, "Translators:\n\n") {
		t.Fatalf("payload missing section header: %s", payload)
	}
	if !strings.Contains(payload, "[LittlePrimate @LittlePrimate](https://www.github.com/LittlePrimate)") {
		t.Fatalf("payload missing first translator: %s", payload)
	}
	if !strings.Contains(payload, "[S3DFX-CYBER @S3DFX-CYBER](https://www.github.com/S3DFX-CYBER)") {
		t.Fatalf("payload missing second translator: %s", payload)
	}
	if !strings.Contains(payload, "[Bhushan Agrawal @bhushan4work](https://www.github.com/bhushan4work)") {
		t.Fatalf("payload missing github url: %s", payload)
	}
}

func TestRenderHTMLIncludesTranslators(t *testing.T) {
	t.Parallel()

	translators := []Contributor{{Username: "octocat", Name: "The Cat"}}
	payload := renderHTML("2026-10-11", nil, nil, nil, nil, translators, nil)
	if !strings.Contains(payload, "Translators:") {
		t.Fatalf("payload missing translators section: %s", payload)
	}
	if !strings.Contains(payload, "<li><a href='https://www.github.com/octocat'>The Cat @octocat</a></li>") {
		t.Fatalf("payload missing translator: %s", payload)
	}
}

func TestWriteFile(t *testing.T) {
	t.Parallel()

	tmp, err := os.CreateTemp(t.TempDir(), "contributors-*.txt")
	if err != nil {
		t.Fatalf("create temp file: %v", err)
	}
	path := tmp.Name()
	_ = tmp.Close()

	if err := writeFile(path, "hello"); err != nil {
		t.Fatalf("writeFile returned error: %v", err)
	}

	b, err := os.ReadFile(path)
	if err != nil {
		t.Fatalf("read output file: %v", err)
	}
	if string(b) != "hello" {
		t.Fatalf("unexpected file content: %s", string(b))
	}
}
