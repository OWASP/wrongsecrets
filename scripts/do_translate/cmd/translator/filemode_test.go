package main

import (
	"os"
	"path/filepath"
	"strings"
	"testing"
)

func write(t *testing.T, path, content string) {
	t.Helper()
	if err := os.WriteFile(path, []byte(content), 0644); err != nil {
		t.Fatal(err)
	}
}

func read(t *testing.T, path string) string {
	t.Helper()
	b, err := os.ReadFile(path)
	if err != nil {
		t.Fatal(err)
	}
	return string(b)
}

func TestConstructSkipsTranslatedAndNonAdoc(t *testing.T) {
	dir := t.TempDir()
	write(t, filepath.Join(dir, "challenge1.adoc"), "one\n")
	write(t, filepath.Join(dir, "challenge1_hint.adoc"), "hint\n")
	write(t, filepath.Join(dir, "challenge1_nl.adoc"), "een\n")
	write(t, filepath.Join(dir, "notes.txt"), "x")
	bundle := filepath.Join(t.TempDir(), "b.txt")

	if err := construct(dir, bundle); err != nil {
		t.Fatal(err)
	}
	got := read(t, bundle)
	if !strings.Contains(got, "FILE: challenge1.adoc") ||
		!strings.Contains(got, "FILE: challenge1_hint.adoc") {
		t.Fatalf("missing files in bundle: %q", got)
	}
	if strings.Contains(got, "challenge1_nl") ||
		strings.Contains(got, "notes.txt") {
		t.Fatalf("unexpected files in bundle: %q", got)
	}
}

func TestRoundTrip(t *testing.T) {
	dir := t.TempDir()
	a := "= Title\n\nSome *text*\n\n\nmore\n"
	b := "hint text\n"
	write(t, filepath.Join(dir, "challenge1.adoc"), a)
	write(t, filepath.Join(dir, "challenge1_hint-gcp.adoc"), b)
	bundle := filepath.Join(t.TempDir(), "b.txt")

	if err := construct(dir, bundle); err != nil {
		t.Fatal(err)
	}
	if err := destruct(dir, bundle, "nl"); err != nil {
		t.Fatal(err)
	}
	if got := read(t, filepath.Join(dir, "challenge1_nl.adoc")); got != a {
		t.Fatalf("got %q want %q", got, a)
	}
	if got := read(
		t, filepath.Join(dir, "challenge1_hint-gcp_nl.adoc"),
	); got != b {
		t.Fatalf("got %q want %q", got, b)
	}
}

func TestDestructToleratesMangledMarkers(t *testing.T) {
	dir := t.TempDir()
	bundle := filepath.Join(t.TempDir(), "b.txt")
	write(t, bundle, "@@@@ FILE: a.adoc @@@@\r\ninhoud\r\n\r\n"+
		"@@@ FILE:  b.adoc @@@\nandere\n")

	if err := destruct(dir, bundle, "nl"); err != nil {
		t.Fatal(err)
	}
	if got := read(t, filepath.Join(dir, "a_nl.adoc")); got != "inhoud\n" {
		t.Fatalf("got %q", got)
	}
	if got := read(t, filepath.Join(dir, "b_nl.adoc")); got != "andere\n" {
		t.Fatalf("got %q", got)
	}
}

func TestDestructErrors(t *testing.T) {
	dir := t.TempDir()
	bundle := filepath.Join(t.TempDir(), "b.txt")
	write(t, bundle, "no markers here\n")

	if err := destruct(dir, bundle, "nl"); err == nil {
		t.Error("expected error for bundle without markers")
	}
	if err := destruct(dir, bundle, "../x"); err == nil {
		t.Error("expected error for invalid language")
	}
	if err := destruct(dir, filepath.Join(dir, "missing"), "nl"); err == nil {
		t.Error("expected error for missing bundle")
	}
}

func TestDestructIgnoresPathInMarker(t *testing.T) {
	dir := t.TempDir()
	bundle := filepath.Join(t.TempDir(), "b.txt")
	write(t, bundle, "@@@@ FILE: ../evil.adoc @@@@\nx\n")

	// the marker regex rejects path separators, so nothing is written
	if err := destruct(dir, bundle, "nl"); err == nil {
		t.Fatal("expected error")
	}
	if _, err := os.Stat(filepath.Join(dir, "..", "evil_nl.adoc")); err == nil {
		t.Fatal("file escaped target dir")
	}
}
