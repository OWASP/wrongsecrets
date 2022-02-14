(function () {
    const darkModeMediaQuery = window.matchMedia("(prefers-color-scheme: dark)");
    let darkSheets = null;

    function updateToggle(darkMode) {
        document.querySelector(".theme-toggle input[type=radio][value='dark']").checked = darkMode;
        document.querySelector(".theme-toggle input[type=radio][value='light']").checked = !darkMode;

        if (darkSheets) {
            darkSheets.forEach((element) => {
                element.media = darkMode ? "(prefers-color-scheme: dark)" : "none";
            });
        }
    }

    darkModeMediaQuery.addEventListener("change", (e) => {
        const darkModeOn = e.matches;
        updateToggle(darkModeOn);
    });

    window.addEventListener("load", function () {
        darkSheets = document.querySelectorAll("link[rel=stylesheet][media='(prefers-color-scheme: dark)']");
        const radios = document.querySelectorAll(".theme-toggle input[type=radio]");
        radios.forEach((radio) => {
            radio.addEventListener("change", function (e) {
                updateToggle(e.target.value === "dark");
            });
        });

        updateToggle(darkModeMediaQuery.matches);
    });
})();
