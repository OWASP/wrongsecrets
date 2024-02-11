(function () {
    const label = document.getElementById('theme-toggle-label');
    const toggle = document.getElementById('theme-toggle');

    function applyDarkMode(darkMode) {
        document.body.classList.toggle('dark-mode', darkMode);
        label.textContent = darkMode ? '🌙' : '☀️';
        localStorage.setItem('darkMode', darkMode ? 'true' : 'false');
        localStorage.setItem('darkmode-pref-set', 'true');

        if (darkMode) {
            label.classList.add('rotate');
        } else {
            label.classList.remove('rotate');
        }
    }

    function toggleTheme() {
        const darkMode = !document.body.classList.contains('dark-mode');
        applyDarkMode(darkMode);
        toggle.checked = darkMode;
    }

    window.addEventListener('load', () => {
        const darkModePref = localStorage.getItem('darkMode') === 'true';
        applyDarkMode(darkModePref);
        toggle.checked = darkModePref;

        // Prevent the rotate animation on load
        label.classList.add('notransition'); // Temporarily disable transition
        label.offsetHeight; // Trigger reflow to apply the transition disable
        label.classList.remove('notransition'); // Re-enable transitions
    });

    label.onclick = toggleTheme;
})();
