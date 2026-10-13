(() => {
  const $ = id => document.getElementById(id);
  const state = { files: [], selected: null, contents: new Map() };
  const apiRoot = "https://api.github.com";

  function setStatus(message) { $("status").textContent = message; }
  function repoName() { return $("repo").value.trim().replace(/^https?:\/\/github\.com\//, "").replace(/\/+$/, ""); }
  function encPath(path) { return path.split("/").map(encodeURIComponent).join("/"); }
  async function api(url) {
    const response = await fetch(url, { headers: { Accept: "application/vnd.github+json" } });
    if (!response.ok) {
      let detail = `${response.status} ${response.statusText}`;
      try { const body = await response.json(); if (body.message) detail += ` — ${body.message}`; } catch {}
      throw new Error(detail);
    }
    return response.json();
  }
  function languageSuffix() {
    // The user enters the literal suffix used in filenames, e.g. nl, es, de, or _pt.
    return $("language").value.trim().replace(/^[_.]+|[.]+$/g, "").toLowerCase();
  }
  const escapeRe = v => v.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
  function stripSuffix(path, suffix) {
    // Remove the _<suffix> before the final extension, or at filename end.
    return path.replace(new RegExp(`_${escapeRe(suffix)}(?=(\\.[^/]+)?$)`, "i"), "");
  }
  function originalPath(translatedPath, suffix = languageSuffix()) {
    // The English counterpart: an explicit _en file if present in the tree, else the unsuffixed base file.
    const base = stripSuffix(translatedPath, suffix);
    const en = base.replace(/(\.[^/.]+)?$/, m => `_en${m}`);
    return state.paths?.has(en) && suffix !== "en" ? en : base;
  }
  function syncUrl() {
    const p = new URLSearchParams();
    p.set("repo", repoName()); p.set("ref", $("ref").value.trim()); p.set("lang", languageSuffix());
    if (state.selected) p.set("file", state.selected.path);
    history.replaceState(null, "", `${location.pathname}?${p}`);
  }
  function langLabel() {
    const code = languageSuffix();
    try { return new Intl.DisplayNames(["en"], { type: "language" }).of(code) || code; } catch { return code; }
  }
  function applyLabels() {
    const name = langLabel();
    $("lang-title").textContent = `${name} files`;
    $("file-list").setAttribute("aria-label", `${name} translation files`);
    $("lang-heading").textContent = `${name} translation`;
    $("lang-hint").innerHTML = `Select a <code>*_${languageSuffix()}.*</code> file from the list. Its English original will appear beside it.`;
  }

  function renderList() {
    const filter = $("filter").value.trim().toLowerCase();
    const visible = state.files.filter(f => f.path.toLowerCase().includes(filter));
    $("count").textContent = `${visible.length}`;
    const list = $("file-list");
    list.replaceChildren();
    if (!visible.length) {
      const li = document.createElement("li"); li.textContent = "No matching files."; li.className = "file-meta"; list.append(li); return;
    }
    for (const file of visible) {
      const li = document.createElement("li");
      const button = document.createElement("button");
      button.textContent = file.path;
      button.title = `Compare ${file.path} with ${originalPath(file.path)}`;
      button.setAttribute("aria-current", state.selected?.path === file.path ? "true" : "false");
      button.addEventListener("click", () => selectFile(file));
      li.append(button); list.append(li);
    }
  }
  async function getText(repo, ref, path) {
    const key = `${repo}@${ref}:${path}`;
    if (state.contents.has(key)) return state.contents.get(key);
    const url = `${apiRoot}/repos/${repo}/contents/${encPath(path)}?ref=${encodeURIComponent(ref)}`;
    const data = await api(url);
    if (data.type !== "file") throw new Error("GitHub returned a non-file object.");
    let text;
    if (data.encoding === "base64" && data.content) {
      const binary = atob(data.content.replace(/\s/g, ""));
      const bytes = Uint8Array.from(binary, c => c.charCodeAt(0));
      text = new TextDecoder("utf-8").decode(bytes);
    } else {
      // Large files may omit inline content; use the download URL returned by GitHub.
      const response = await fetch(data.download_url);
      if (!response.ok) throw new Error(`Could not download ${path} (${response.status}).`);
      text = await response.text();
    }
    state.contents.set(key, text);
    return text;
  }
  async function selectFile(file) {
    state.selected = file; renderList(); syncUrl();
    $("empty").hidden = true; $("comparison").hidden = false;
    $("selected-title").textContent = file.path.split("/").pop();
    $("path-label").textContent = `${originalPath(file.path)}  ↔  ${file.path}`;
    $("github-link").href = `https://github.com/${repoName()}/blob/${encodeURIComponent($("ref").value.trim())}/${encPath(file.path)}`;
    $("original").textContent = "Loading original…"; $("translation").textContent = "Loading translation…";
    $("original-meta").textContent = originalPath(file.path);
    $("translation-meta").textContent = file.path;
    setStatus("Loading selected files…");
    const repo = repoName(), ref = $("ref").value.trim();
    try {
      const [original, translation] = await Promise.all([
        getText(repo, ref, originalPath(file.path)),
        getText(repo, ref, file.path)
      ]);
      // Avoid a stale request replacing a more recently selected file.
      if (state.selected?.path !== file.path) return;
      $("original").textContent = original;
      $("translation").textContent = translation;
      setStatus("Comparison loaded.");
    } catch (error) {
      if (state.selected?.path !== file.path) return;
      $("original").textContent = `Unable to load original file:\n${error.message}`;
      $("translation").textContent = `Unable to load translation:\n${error.message}`;
      setStatus(`Could not load comparison: ${error.message}`);
    }
  }
  async function loadFiles() {
    const repo = repoName(), ref = $("ref").value.trim();
    if (!/^[^/]+\/[^/]+$/.test(repo) || !ref || !languageSuffix()) {
      setStatus("Enter a repository as owner/name and a branch, tag, or commit."); return;
    }
    $("load").disabled = true; setStatus("Fetching repository file list…");
    state.files = []; state.selected = null; state.contents.clear();
    $("comparison").hidden = true; $("empty").hidden = false;
    try {
      const suffix = `_${languageSuffix()}`;
      applyLabels();
      // Resolve branch/tag/commit to a SHA, then use the recursive tree endpoint.
      const encodedRef = encodeURIComponent(ref);
      let commit;
      try {
        commit = await api(`${apiRoot}/repos/${repo}/commits/${encodedRef}`);
      } catch {
        // For branches/tags whose names contain slashes, resolve via Git refs endpoint.
        const refs = await api(`${apiRoot}/repos/${repo}/git/ref/heads/${encPath(ref)}`).catch(async () =>
          api(`${apiRoot}/repos/${repo}/git/ref/tags/${encPath(ref)}`));
        commit = { sha: refs.object.sha, commit: refs.object.type === "commit" ? undefined : undefined };
        if (refs.object.type === "tag") {
          const tag = await api(`${apiRoot}/repos/${repo}/git/tags/${refs.object.sha}`);
          commit.sha = tag.object.sha;
        }
      }
      const sha = commit.sha || commit.commit?.sha;
      if (!sha) throw new Error("Could not resolve that branch, tag, or commit.");
      const tree = await api(`${apiRoot}/repos/${repo}/git/trees/${sha}?recursive=1`);
      if (tree.truncated) setStatus("GitHub returned a truncated file tree; results may be incomplete.");
      state.paths = new Set(tree.tree.filter(i => i.type === "blob").map(i => i.path));
      const paths = tree.tree.filter(item => item.type === "blob" && new RegExp(`_${escapeRe(languageSuffix())}(?:\\.[^/]+)?$`, "i").test(item.path))
        .map(item => ({ path: item.path, original: originalPath(item.path) }))
        .filter(item => state.paths.has(item.original))
        .sort((a,b) => a.path.localeCompare(b.path));
      state.files = paths;
      renderList();
      const wanted = new URLSearchParams(location.search).get("file");
      const pre = wanted && paths.find(f => f.path === wanted);
      setStatus(`Found ${paths.length} ${suffix} translation file(s) with a matching original in ${repo} at ${ref}.`);
      syncUrl();
      if (pre) selectFile(pre);
      if (!paths.length) setStatus(`No *${suffix} files with a matching original were found. Check the repository, language suffix, and ref.`);
    } catch (error) {
      setStatus(`Could not load repository: ${error.message}`);
      $("file-list").replaceChildren();
    } finally { $("load").disabled = false; }
  }
  $("load").addEventListener("click", () => { history.replaceState(null, "", location.pathname); loadFiles(); });
  $("language").addEventListener("keydown", e => { if (e.key === "Enter") loadFiles(); });
  $("filter").addEventListener("input", renderList);
  $("ref").addEventListener("keydown", e => { if (e.key === "Enter") loadFiles(); });
  $("repo").addEventListener("keydown", e => { if (e.key === "Enter") loadFiles(); });
  document.querySelectorAll("[data-copy]").forEach(button => button.addEventListener("click", async () => {
    const target = button.dataset.copy === "original" ? $("original") : $("translation");
    try {
      await navigator.clipboard.writeText(target.textContent);
      const old = button.textContent; button.textContent = "Copied!";
      setTimeout(() => button.textContent = old, 1200);
    } catch { setStatus("Clipboard access failed. Select the text and copy it manually."); }
  }));
  const q = new URLSearchParams(location.search);
  if (q.get("repo")) $("repo").value = q.get("repo");
  if (q.get("ref")) $("ref").value = q.get("ref");
  if (q.get("lang")) $("language").value = q.get("lang");
  $("language").addEventListener("change", loadFiles);
  loadFiles();
})();
