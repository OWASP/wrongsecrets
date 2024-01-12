(function () {
    const darkModeMediaQuery = window.matchMedia('(prefers-color-scheme: dark)');

    function updateToggle(darkMode) {
        const checkbox = document.querySelector(".theme-toggle input[type='checkbox']");
        if (checkbox) {
            checkbox.checked = darkMode;
        }

        document.body.classList.toggle('dark-mode', darkMode);
        localStorage.setItem('darkMode', darkMode);
        localStorage.setItem('darkmode-pref-set', 'true');
    }

    // Listen for system theme changes
    darkModeMediaQuery.addEventListener('change', (e) => {
        updateToggle(e.matches);
    });

    // Set up event listener and initial theme on page load
    window.addEventListener('load', function () {
        const checkbox = document.querySelector(".theme-toggle input[type='checkbox']");
        if (checkbox) {
            checkbox.addEventListener('change', function () {
                updateToggle(checkbox.checked);
            });

            // Initialize theme based on user preference or system preference
            let initialTheme;
            if (localStorage.getItem('darkmode-pref-set') === 'true') {
                initialTheme = localStorage.getItem('darkMode') === 'true';
            } else {
                initialTheme = darkModeMediaQuery.matches;
            }
            updateToggle(initialTheme);
        }
    });
})();
