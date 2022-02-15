(function () {
    const darkModeMediaQuery = window.matchMedia("(prefers-color-scheme: dark)");

    function updateToggle(darkMode) {
        document.querySelector(".theme-toggle input[type=radio][value='dark']").checked = darkMode;
        document.querySelector(".theme-toggle input[type=radio][value='light']").checked = !darkMode;
        document.body.classList.toggle("dark-mode", darkMode);
        document.cookie["darkmode"] = darkMode;
    }

    if (document.cookie["darkmode"]) {
        let darkModeOn;
        darkModeOn = document.cookie["darkmode"] === "true";
        updateToggle(darkModeOn);
    } else {
        darkModeMediaQuery.addEventListener("change", (e) => {
            const darkModeOn = e.matches;
            updateToggle(darkModeOn);
        });
    }


    window.addEventListener("load", function () {
        const radios = document.querySelectorAll(".theme-toggle input[type=radio]");
        radios.forEach((radio) => {
            radio.addEventListener("change", function (e) {
                updateToggle(e.target.value === "dark");
            });
        });

        updateToggle(darkModeMediaQuery.matches);
    });
})();
