(function () {
  const label = document.getElementById('theme-toggle-label')
  const toggle = document.getElementById('theme-toggle')

  function applyDarkMode (darkMode) {
    document.body.classList.toggle('dark-mode', darkMode)
    label.textContent = darkMode ? '🌙' : '☀️'
    localStorage.setItem('darkMode', darkMode ? 'true' : 'false')
    localStorage.setItem('darkmode-pref-set', 'true')
  }

  function toggleTheme () {
    const darkMode = !document.body.classList.contains('dark-mode')
    applyDarkMode(darkMode)
    toggle.checked = darkMode
  }

  window.addEventListener('DOMContentLoaded', () => {
    const darkModePref = localStorage.getItem('darkMode') === 'true'
    applyDarkMode(darkModePref)
    toggle.checked = darkModePref
  })

  label.onclick = toggleTheme
})()
