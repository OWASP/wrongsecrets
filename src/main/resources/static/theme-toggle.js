(function () {
  const darkModeMediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
  let initialTheme
  if (localStorage.getItem('darkmode-pref-set') === 'true') {
    initialTheme = localStorage.getItem('darkMode') === 'true'
  } else {
    initialTheme = darkModeMediaQuery.matches
  }

  function updateToggle (darkMode) {
    document.querySelector(".theme-toggle input[type=radio][value='dark']").checked = darkMode
    document.querySelector(".theme-toggle input[type=radio][value='light']").checked = !darkMode

    document.body.classList.toggle('dark-mode', darkMode)
    localStorage.setItem('darkMode', darkMode)
    localStorage.setItem('darkmode-pref-set', 'true')
  }

  darkModeMediaQuery.addEventListener('change', (e) => {
    const darkModeOn = e.matches
    updateToggle(darkModeOn)
  })

  window.addEventListener('load', function () {
    const radios = document.querySelectorAll('.theme-toggle input[type=radio]')
    radios.forEach((radio) => {
      radio.addEventListener('change', function (e) {
        updateToggle(e.target.value === 'dark')
      })
    })

    updateToggle(initialTheme)
  })
})()
