package main

import (
	"fmt"
	"os"
	"path/filepath"
	"regexp"
	"sort"
	"strings"
)

var (
	translatedName = regexp.MustCompile(`_[a-z]{2}(-[A-Za-z]{2})?\.adoc$`)
	markerLine     = regexp.MustCompile(
		`^\W*FILE:\s*([\w.\-]+\.adoc)\W*$`,
	)
	langCode = regexp.MustCompile(`^[a-zA-Z]{2,3}([-_][A-Za-z]{2})?$`)
)

// construct merges all explanation adoc files into one bundle file.
func construct(dir, bundle string) error {
	entries, err := os.ReadDir(dir)
	if err != nil {
		return fmt.Errorf("read dir: %w", err)
	}
	var names []string
	for _, e := range entries {
		n := e.Name()
		if e.IsDir() || !strings.HasSuffix(n, ".adoc") ||
			translatedName.MatchString(n) {
			continue
		}
		names = append(names, n)
	}
	sort.Strings(names)

	var sb strings.Builder
	for _, n := range names {
		content, err := os.ReadFile(filepath.Join(dir, n))
		if err != nil {
			return fmt.Errorf("read %s: %w", n, err)
		}
		fmt.Fprintf(&sb, "@@@@ FILE: %s @@@@\n", n)
		sb.WriteString(strings.TrimRight(string(content), "\n"))
		sb.WriteString("\n\n")
	}
	if err := os.WriteFile(bundle, []byte(sb.String()), 0644); err != nil {
		return fmt.Errorf("write bundle: %w", err)
	}
	fmt.Printf("Combined %d files into %s\n", len(names), bundle)
	return nil
}

// destruct splits a (translated) bundle back into <name>_<lang>.adoc files.
func destruct(dir, bundle, lang string) error {
	if !langCode.MatchString(lang) {
		return fmt.Errorf("invalid language code %q", lang)
	}
	data, err := os.ReadFile(bundle)
	if err != nil {
		return fmt.Errorf("read bundle: %w", err)
	}

	var (
		current string
		buf     []string
		count   int
	)
	flush := func() error {
		if current == "" {
			return nil
		}
		out := strings.TrimRight(strings.Join(buf, "\n"), "\n") + "\n"
		name := strings.TrimSuffix(current, ".adoc") +
			"_" + lang + ".adoc"
		if err := os.WriteFile(
			filepath.Join(dir, name), []byte(out), 0644,
		); err != nil {
			return fmt.Errorf("write %s: %w", name, err)
		}
		count++
		return nil
	}

	for _, line := range strings.Split(string(data), "\n") {
		line = strings.TrimRight(line, "\r")
		if m := markerLine.FindStringSubmatch(
			strings.TrimSpace(line),
		); m != nil {
			if err := flush(); err != nil {
				return err
			}
			current, buf = filepath.Base(m[1]), nil
			continue
		}
		buf = append(buf, line)
	}
	if err := flush(); err != nil {
		return err
	}
	if count == 0 {
		return fmt.Errorf("no FILE markers found in %s", bundle)
	}
	fmt.Printf("Recreated %d files in %s\n", count, dir)
	return nil
}
