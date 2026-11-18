package main

import (
	"context"
	"flag"
	"fmt"
	"os"
	"strings"
	"time"

	"github.com/owasp/wrongsecrets/scripts/do_translate/internal/translator"
)

func main() {
	if err := run(); err != nil {
		fmt.Fprintln(os.Stderr, "Error:", err)
		os.Exit(1)
	}
}

func run() error {
	inputPath := flag.String(
		"input", "", "Path to the input file",
	)
	outputPath := flag.String(
		"output", "", "Path to the translated output file",
	)
	target := flag.String(
		"target", "", "Target language code, e.g. nl, de, fr",
	)
	source := flag.String(
		"source", "", "Source language code (optional)",
	)
	projectID := flag.String(
		"project", os.Getenv("GOOGLE_CLOUD_PROJECT"),
		"Google Cloud project ID",
	)

	constructFlag := flag.Bool(
		"construct", false,
		"File mode: combine all explanation adoc files into one bundle (-bundle)",
	)
	destructFlag := flag.Bool(
		"destruct", false,
		"File mode: recreate files from the bundle as <name>_<language>.adoc",
	)
	language := flag.String(
		"language", "", "Language suffix for -destruct, e.g. nl",
	)
	dir := flag.String(
		"dir", "../../src/main/resources/explanations",
		"Explanations directory for -construct/-destruct",
	)
	bundle := flag.String(
		"bundle", "explanations_bundle.txt",
		"Bundle file for -construct/-destruct",
	)

	flag.Parse()

	if *constructFlag && *destructFlag {
		return fmt.Errorf("use only one of -construct or -destruct")
	}
	if *constructFlag {
		return construct(*dir, *bundle)
	}
	if *destructFlag {
		if *language == "" {
			return fmt.Errorf("-destruct requires -language")
		}
		return destruct(*dir, *bundle, *language)
	}

	if *inputPath == "" || *outputPath == "" || *target == "" {
		flag.Usage()
		return fmt.Errorf("input, output, and target are required")
	}

	if strings.TrimSpace(*projectID) == "" {
		return fmt.Errorf(
			"set GOOGLE_CLOUD_PROJECT or pass -project",
		)
	}

	input, err := os.ReadFile(*inputPath)
	if err != nil {
		return fmt.Errorf("read input file: %w", err)
	}

	ctx, cancel := context.WithTimeout(
		context.Background(), 10*time.Minute,
	)
	defer cancel()

	tr, err := translator.New(
		ctx, *projectID, *source, *target,
	)
	if err != nil {
		return err
	}
	defer tr.Close()

	fmt.Printf(
		"Translating %s to %s...\n",
		*inputPath, *target,
	)

	result, err := tr.Translate(ctx, string(input))
	if err != nil {
		return err
	}

	if err := os.WriteFile(
		*outputPath, []byte(result), 0644,
	); err != nil {
		return fmt.Errorf("write output file: %w", err)
	}

	fmt.Printf("Translation saved to %s\n", *outputPath)
	return nil
}
