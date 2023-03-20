(function () {
  function updateToggle (darkMode) {
    document.querySelector(".theme-toggle input[type=radio][value='dark']").checked = darkMode
    document.querySelector(".theme-toggle input[type=radio][value='light']").checked = !darkMode

    document.body.classList.toggle('dark-mode', darkMode)
    localStorage.setItem('theme', darkMode ? 'dark' : 'light')
  }

  window.addEventListener('load', function () {
    const radios = document.querySelectorAll('.theme-toggle input[type=radio]')
    radios.forEach((radio) => {
      radio.addEventListener('change', function (e) {
        updateToggle(e.target.value === 'dark')
      })
    })

    const storedTheme = localStorage.getItem('theme')
    if (storedTheme === 'dark') {
      updateToggle(true)
    } else {
      updateToggle(false)
    }
  })
})()
