package translator

import (
	"context"
	"fmt"
	"strings"

	translate "cloud.google.com/go/translate/apiv3"
	"cloud.google.com/go/translate/apiv3/translatepb"
)

const maxChunkRunes = 20000

type Translator struct {
	client    *translate.TranslationClient
	projectID string
	source    string
	target    string
}

func New(
	ctx context.Context,
	projectID, source, target string,
) (*Translator, error) {
	if strings.TrimSpace(projectID) == "" {
		return nil, fmt.Errorf("Google Cloud project ID is required")
	}
	if strings.TrimSpace(target) == "" {
		return nil, fmt.Errorf("target language is required")
	}

	client, err := translate.NewTranslationClient(ctx)
	if err != nil {
		return nil, fmt.Errorf("create translation client: %w", err)
	}

	return &Translator{
		client:    client,
		projectID: projectID,
		source:    source,
		target:    target,
	}, nil
}

func (t *Translator) Close() error {
	return t.client.Close()
}

func (t *Translator) Translate(
	ctx context.Context,
	input string,
) (string, error) {
	if input == "" {
		return "", nil
	}

	chunks := splitText(input, maxChunkRunes)
	var output strings.Builder

	for i, chunk := range chunks {
		req := &translatepb.TranslateTextRequest{
			Parent: fmt.Sprintf(
				"projects/%s/locations/global",
				t.projectID,
			),
			TargetLanguageCode: t.target,
			MimeType:           "text/plain",
			Contents:           []string{chunk},
		}

		// An empty source language lets Google detect it.
		if t.source != "" {
			req.SourceLanguageCode = t.source
		}

		resp, err := t.client.TranslateText(ctx, req)
		if err != nil {
			return "", fmt.Errorf(
				"translate chunk %d/%d: %w",
				i+1, len(chunks), err,
			)
		}

		translations := resp.GetTranslations()
		if len(translations) != 1 {
			return "", fmt.Errorf(
				"chunk %d: expected one translation, got %d",
				i+1, len(translations),
			)
		}

		output.WriteString(translations[0].GetTranslatedText())
	}

	return output.String(), nil
}

// splitText limits each API request by Unicode code points.
// It prefers to split at a newline when possible.
func splitText(input string, limit int) []string {
	if input == "" {
		return nil
	}

	var chunks []string
	remaining := input

	for len([]rune(remaining)) > limit {
		runes := []rune(remaining)
		end := limit

		// Prefer a paragraph or line boundary.
		for i := limit - 1; i >= limit/2; i-- {
			if runes[i] == '\n' {
				end = i + 1
				break
			}
		}

		chunks = append(chunks, string(runes[:end]))
		remaining = string(runes[end:])
	}

	if remaining != "" {
		chunks = append(chunks, remaining)
	}

	return chunks
}