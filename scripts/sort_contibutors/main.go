package main

import (
	"encoding/json"
	"errors"
	"flag"
	"fmt"
	"html"
	"io"
	"net/http"
	"net/url"
	"os"
	"path/filepath"
	"sort"
	"strings"
	"time"
)

const (
	githubAPIBase      = "https://api.github.com"
	outputHTMLFile     = "contributors_file.html"
	outputMarkdownFile = "contributors_file.md"
)

var errInvalidToken = errors.New("invalid token")

type Contributor struct {
	Username string
	Name     string
	Ranking  int
}

type contributorAPI struct {
	Login         string `json:"login"`
	Contributions int    `json:"contributions"`
}

type app struct {
	client    *http.Client
	token     string
	outputDir string
	nameCache map[string]string
}

var excludedContributors = map[string]struct{}{
	"DerGut":             {},
	"bkimminich":         {},
	"MichaelEischer":     {},
	"rseedorff":          {},
	"jonasbg":            {},
	"scornelissen85":     {},
	"zadjadr":            {},
	"stuebingerb":        {},
	"sydseter":           {},
	"troygerber":         {},
	"skandix":            {},
	"saymolet":           {},
	"adrianeriksen":      {},
	"pseudobeard":        {},
	"coffemakingtoaster": {},
	"wurstbrot":          {},
	"blucas-accela":      {},
	"fwijnholds":         {},
	"stefan-schaermeli":  {},
	"nickmalcolm":        {},
	"orangecola":         {},
	"commjoen":           {},
	"bendehaan":          {},
	"benno001":           {},
	"copilot":            {},
	"copilot-swe-agent":  {},
}

var knownNames = map[string]string{
	"puneeth072003": "Puneeth Y",
	"f3rn0s":        "Fern",
	"Novice-expert": "Divyanshu Dev",
	"neatzsche":     "Chris Elbring Jr.",
}

func main() {
	tokenFlag := flag.String("token", "", "GitHub fine-grained token. If omitted, USER_TOKEN or GITHUB_TOKEN env vars are used")
	outputDirFlag := flag.String("output-dir", ".", "Directory where contributors_file.html and contributors_file.md will be written")
	flag.Parse()

	token, err := resolveToken(*tokenFlag, os.Getenv)
	if err != nil {
		fmt.Println(err)
		flag.Usage()
		os.Exit(1)
	}

	outputDir, err := resolveOutputDir(*outputDirFlag)
	if err != nil {
		fmt.Println(err)
		os.Exit(1)
	}

	application := app{
		client:    &http.Client{Timeout: 20 * time.Second},
		token:     token,
		outputDir: outputDir,
		nameCache: map[string]string{},
	}

	if err := application.run(); err != nil {
		fmt.Printf("[!] %v\n", err)
		os.Exit(1)
	}
}

func resolveToken(tokenFlag string, env func(string) string) (string, error) {
	if strings.TrimSpace(tokenFlag) != "" {
		return tokenFlag, nil
	}

	if token := strings.TrimSpace(env("USER_TOKEN")); token != "" {
		return token, nil
	}

	if token := strings.TrimSpace(env("GITHUB_TOKEN")); token != "" {
		return token, nil
	}

	return "", errors.New("missing token: provide -token or set USER_TOKEN/GITHUB_TOKEN")
}

func resolveOutputDir(outputDirFlag string) (string, error) {
	outputDir := strings.TrimSpace(outputDirFlag)
	if outputDir == "" {
		return "", errors.New("missing output directory")
	}

	absPath, err := filepath.Abs(outputDir)
	if err != nil {
		return "", fmt.Errorf("resolve output directory: %w", err)
	}

	return absPath, nil
}

func (a *app) run() error {
	if err := os.MkdirAll(a.outputDir, 0o750); err != nil {
		return fmt.Errorf("create output directory: %w", err)
	}

	createdDate := time.Now().UTC().Format("2006-01-02")

	leaders := []Contributor{
		{Username: "bendehaan", Name: "Ben de Haan"},
		{Username: "commjoen", Name: "Jeroen Willemsen"},
	}
	testers := []Contributor{
		{Username: "davevs", Name: "Dave van Stein"},
		{Username: "drnow4u", Name: "Marcin Nowak"},
		{Username: "mchangsp", Name: "Marc Chang Sing Pang"},
		{Username: "djvinnie", Name: "Vineeth Jagadeesh"},
	}
	specialThanks := []Contributor{
		{Username: "madhuakula", Name: "Madhu Akula @madhuakula"},
		{Username: "nbaars", Name: "Nanne Baars @nbaars"},
		{Username: "bkimminich", Name: "Bjorn Kimminich"},
		{Username: "devsecops", Name: "Dan Gora"},
		{Username: "saragluna", Name: "Xiaolu Dai"},
		{Username: "jonathanGiles", Name: "Jonathan Giles"},
        {Username: "MayanK23YadaV", Name: "Mayank Yadav"},
	}

	topContributors, contributors, err := a.getContributorsList()
	if err != nil {
		return err
	}

	htmlOutputPath := filepath.Join(a.outputDir, outputHTMLFile)
	fmt.Printf("[+] Print to HTML file: %s\n", htmlOutputPath)
	htmlPayload := renderHTML(createdDate, leaders, topContributors, contributors, testers, specialThanks)
	if err := writeFile(htmlOutputPath, htmlPayload); err != nil {
		return fmt.Errorf("write html output: %w", err)
	}

	markdownOutputPath := filepath.Join(a.outputDir, outputMarkdownFile)
	fmt.Printf("[+] Print to MD file: %s\n", markdownOutputPath)
	mdPayload := renderCreatedComment(createdDate)
	mdPayload += renderMarkdown(leaders, "Leaders")
	mdPayload += renderMarkdown(topContributors, "Top contributors")
	mdPayload += renderMarkdown(contributors, "Contributors")
	mdPayload += renderMarkdown(testers, "Testers")
	mdPayload += renderMarkdown(specialThanks, "Special thanks")
	if err := writeFile(markdownOutputPath, mdPayload); err != nil {
		return fmt.Errorf("write markdown output: %w", err)
	}

	fmt.Println("[+] Done")
	return nil
}

func writeFile(path, payload string) error {
	return os.WriteFile(path, []byte(payload), 0o600)
}

func renderMarkdown(users []Contributor, label string) string {
	var b strings.Builder
	b.WriteString(label)
	b.WriteString(":\n\n")
	for _, user := range users {
		fmt.Fprintf(&b, "- [%s @%s](https://www.github.com/%s)\n", user.Name, user.Username, user.Username)
	}
	b.WriteString("\n")
	return b.String()
}

func renderCreatedComment(date string) string {
	return fmt.Sprintf("<!-- Generated on %s -->\n\n", date)
}

func renderHTML(createdDate string, leaders, topContributors, contributors, testers, specialThanks []Contributor) string {
	var b strings.Builder
	b.WriteString(renderCreatedComment(createdDate))
	b.WriteString("<html><head></head><body>\n")
	writeHTMLSection(&b, "OWASP Project Leaders", leaders)
	writeHTMLSection(&b, "Top Contributors", topContributors)
	writeHTMLSection(&b, "Contributors", contributors)
	writeHTMLSection(&b, "Testers", testers)
	writeHTMLSection(&b, "Special mentions for helping out", specialThanks)
	b.WriteString("</body><html>\n")
	return b.String()
}

func writeHTMLSection(b *strings.Builder, title string, users []Contributor) {
	b.WriteString(title)
	b.WriteString(":\n")
	b.WriteString("<ul>\n")
	for _, user := range users {
		escUser := html.EscapeString(user.Username)
		escName := html.EscapeString(user.Name)
		fmt.Fprintf(b, "<li><a href='https://www.github.com/%s'>%s @%s</a></li>\n", escUser, escName, escUser)
	}
	b.WriteString("</ul>\n")
}

func (a *app) getContributorsList() ([]Contributor, []Contributor, error) {
	fmt.Println("[+] Fetching the Wrong Secrets CTF party contributors list ...")
	ctfList, err := a.fetchRepository("wrongsecrets-ctf-party")
	if err != nil {
		return nil, nil, err
	}

	fmt.Println("[+] Fetching the Wrong Secrets Binaries contributors list ...")
	binariesList, err := a.fetchRepository("wrongsecrets-binaries")
	if err != nil {
		return nil, nil, err
	}

	fmt.Println("[+] Fetching the Wrong Secrets contributors list ...")
	wrongsecretsList, err := a.fetchRepository("wrongsecrets")
	if err != nil {
		return nil, nil, err
	}

	merged := append(append(binariesList, ctfList...), wrongsecretsList...)
	fmt.Println("[+] Sorting the list ..")
	top, normal := mergeUsers(merged)
	return top, normal, nil
}

func (a *app) fetchRepository(project string) ([]Contributor, error) {
	var contributors []Contributor

	for page := 1; ; page++ {
		endpoint := fmt.Sprintf("%s/repos/OWASP/%s/contributors?per_page=100&page=%d", githubAPIBase, project, page)
		statusCode, body, err := a.githubGet(endpoint)
		if err != nil {
			return nil, err
		}

		if statusCode == http.StatusUnauthorized {
			return nil, errInvalidToken
		}
		if statusCode == http.StatusForbidden {
			return nil, fmt.Errorf("access denied while fetching %s; token may be missing repository access or is rate-limited", project)
		}
		if statusCode >= http.StatusBadRequest {
			return nil, fmt.Errorf("failed to fetch %s contributors, status code %d", project, statusCode)
		}

		var payload []contributorAPI
		if err := json.Unmarshal(body, &payload); err != nil {
			return nil, fmt.Errorf("decode contributors for %s: %w", project, err)
		}
		if len(payload) == 0 {
			break
		}

		contributors = append(contributors, a.parseContributorList(payload)...)
		if len(payload) < 100 {
			break
		}
	}

	return contributors, nil
}

func (a *app) parseContributorList(items []contributorAPI) []Contributor {
	contributors := make([]Contributor, 0, len(items))

	for _, item := range items {
		if item.Login == "" {
			continue
		}
		if strings.Contains(item.Login, "[bot]") {
			continue
		}
		if _, excluded := excludedContributors[item.Login]; excluded {
			continue
		}

		name, err := a.getFullName(item.Login)
		if err != nil {
			if errors.Is(err, errInvalidToken) {
				fmt.Println("[!] Invalid token")
				continue
			}
			fmt.Printf("[!] Failed to fetch user %s: %v\n", item.Login, err)
			name = item.Login
		}
		if strings.TrimSpace(name) == "" {
			name = item.Login
		}

		contributors = append(contributors, Contributor{
			Username: item.Login,
			Name:     name,
			Ranking:  item.Contributions,
		})
	}

	return contributors
}

func (a *app) getFullName(username string) (string, error) {
	if name, ok := knownNames[username]; ok {
		return name, nil
	}
	if name, ok := a.nameCache[username]; ok {
		return name, nil
	}

	endpoint := fmt.Sprintf("%s/users/%s", githubAPIBase, url.PathEscape(username))
	statusCode, body, err := a.githubGet(endpoint)
	if err != nil {
		return username, err
	}

	if statusCode == http.StatusUnauthorized {
		return username, errInvalidToken
	}
	if statusCode == http.StatusNotFound {
		return username, errors.New("not found")
	}
	if statusCode >= http.StatusBadRequest {
		return username, fmt.Errorf("status code %d", statusCode)
	}

	var payload struct {
		Name string `json:"name"`
	}
	if err := json.Unmarshal(body, &payload); err != nil {
		return username, err
	}
	if strings.TrimSpace(payload.Name) == "" {
		a.nameCache[username] = username
		return username, nil
	}

	a.nameCache[username] = payload.Name
	return payload.Name, nil
}

func (a *app) githubGet(endpoint string) (int, []byte, error) {
	if err := validateGitHubEndpoint(endpoint); err != nil {
		return 0, nil, err
	}

	req, err := http.NewRequest(http.MethodGet, endpoint, nil)
	if err != nil {
		return 0, nil, err
	}
	req.Header.Set("X-GitHub-Api-Version", "2022-11-28")
	req.Header.Set("Accept", "application/vnd.github+json")
	req.Header.Set("Authorization", "Bearer "+a.token)

	// #nosec G704 -- endpoint is validated in validateGitHubEndpoint before request execution.
	resp, err := a.client.Do(req)
	if err != nil {
		return 0, nil, err
	}
	defer func() {
		_ = resp.Body.Close()
	}()

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return resp.StatusCode, nil, err
	}

	return resp.StatusCode, body, nil
}

func validateGitHubEndpoint(endpoint string) error {
	u, err := url.Parse(endpoint)
	if err != nil {
		return fmt.Errorf("invalid endpoint: %w", err)
	}

	if u.Scheme != "https" {
		return errors.New("invalid endpoint scheme")
	}

	if !strings.EqualFold(u.Hostname(), "api.github.com") {
		return errors.New("invalid endpoint host")
	}

	return nil
}

func mergeUsers(items []Contributor) ([]Contributor, []Contributor) {
	byUser := map[string]Contributor{}
	for _, item := range items {
		current, ok := byUser[item.Username]
		if !ok {
			byUser[item.Username] = item
			continue
		}
		current.Ranking += item.Ranking
		if strings.TrimSpace(current.Name) == "" {
			current.Name = item.Name
		}
		byUser[item.Username] = current
	}

	merged := make([]Contributor, 0, len(byUser))
	for _, item := range byUser {
		merged = append(merged, item)
	}

	sort.SliceStable(merged, func(i, j int) bool {
		if merged[i].Ranking == merged[j].Ranking {
			return strings.ToLower(merged[i].Username) < strings.ToLower(merged[j].Username)
		}
		return merged[i].Ranking > merged[j].Ranking
	})

	top := make([]Contributor, 0, len(merged))
	normal := make([]Contributor, 0, len(merged))
	for _, item := range merged {
		if item.Ranking >= 100 {
			top = append(top, item)
			continue
		}
		normal = append(normal, item)
	}
	return top, normal
}
