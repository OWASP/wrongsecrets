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

	payload := renderMarkdown([]Contributor{{Username: "octocat", Name: "The Cat"}}, "Contributors")
	if !strings.Contains(payload, "Contributors:\n\n") {
		t.Fatalf("payload missing section header: %s", payload)
	}
	if !strings.Contains(payload, "https://www.github.com/octocat") {
		t.Fatalf("payload missing github url: %s", payload)
	}
}

func TestRenderCreatedComment(t *testing.T) {
	t.Parallel()

	comment := renderCreatedComment("2026-04-20")
	if comment != "<!-- Generated on 2026-04-20 -->\n\n" {
		t.Fatalf("unexpected comment: %q", comment)
	}
}

func TestRenderHTMLIncludesCreatedComment(t *testing.T) {
	t.Parallel()

	payload := renderHTML("2026-04-20", nil, nil, nil, nil, nil)
	if !strings.HasPrefix(payload, "<!-- Generated on 2026-04-20 -->") {
		t.Fatalf("html missing created comment prefix: %s", payload)
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

func TestParseContributorListExcludesBotsAndCopilot(t *testing.T) {
	t.Parallel()

	a := &app{nameCache: map[string]string{}}
	input := []contributorAPI{
		{Login: "some[bot]", Contributions: 10},
		{Login: "copilot", Contributions: 10},
		{Login: "copilot-swe-agent", Contributions: 10},
		{Login: "f3rn0s", Contributions: 5},
	}

	got := a.parseContributorList(input)
	if len(got) != 1 {
		t.Fatalf("expected 1 contributor after filtering, got %d", len(got))
	}
	if got[0].Username != "f3rn0s" {
		t.Fatalf("expected f3rn0s to remain, got %+v", got[0])
	}
}
