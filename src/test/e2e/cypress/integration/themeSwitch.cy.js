import ThemeSwitchPage from '../pages/themeSwitchPage'

describe('Theme Switching Tests', () => {
  it('A user can switch the theme to dark on each page', () => {
    cy.wrap(['', 'challenge/challenge-0', 'stats', 'about']).each((endpoint) => {
      cy.visit(`/${endpoint}`)
      cy.get('body').then(($body) => {
        if (!$body.hasClass('dark-mode')) { // Adjust this condition to check for light mode
          cy.dataCy(ThemeSwitchPage.DARK_MODE_RADIO).click()
        }
      })
      cy.get(ThemeSwitchPage.DARK_MODE).should('exist')
      cy.dataCy(ThemeSwitchPage.DARK_MODE_RADIO).click()
    })
  })

  it('Dark mode persists on each page', () => {
    cy.wrap(['', 'challenge/challenge-0', 'stats', 'about']).each((endpoint) => {
      cy.visit(`/${endpoint}`)
      cy.get('body').then(($body) => {
        if (!$body.hasClass('dark-mode')) {
          cy.dataCy(ThemeSwitchPage.DARK_MODE_RADIO).click()
        }
      })
      cy.get(ThemeSwitchPage.DARK_MODE).should('exist')
    })
  })

  it('A user can switch theme to dark and back to light on each page', () => {
    cy.wrap(['', 'challenge/challenge-0', 'stats', 'about']).each((endpoint) => {
      cy.visit(`/${endpoint}`)
      cy.get('body').then(($body) => {
        if (!$body.hasClass('dark-mode')) { // Adjust this condition to check for light mode
          cy.dataCy(ThemeSwitchPage.DARK_MODE_RADIO).click()
        }
      })
      cy.get(ThemeSwitchPage.DARK_MODE).should('exist')
      cy.dataCy(ThemeSwitchPage.DARK_MODE_RADIO).click()
      cy.get(ThemeSwitchPage.DARK_MODE).should('not.exist')
    })
  })
})
