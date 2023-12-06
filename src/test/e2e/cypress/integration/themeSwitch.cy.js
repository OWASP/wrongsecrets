import ThemeSwitchPage from '../pages/themeSwitchPage'

describe('Theme Switching Tests', () => {
  it('A user can switch the theme to dark on each page', () => {
    cy.wrap(['', 'challenge/challenge-0', 'stats', 'about']).each((endpoint) => {
      cy.visit(`/${endpoint}`)
      cy.dataCy(ThemeSwitchPage.DARK_MODE_RADIO).click()
      cy.get(ThemeSwitchPage.DARK_MODE).should('exist')
    })
  })

  it('Dark mode persists on each page', () => {
    cy.visit('/')
    cy.dataCy(ThemeSwitchPage.DARK_MODE_RADIO).click()
    cy.wrap(['', 'challenge/challenge-0', 'stats', 'about']).each((endpoint) => {
      cy.visit(`/${endpoint}`)
      cy.get(ThemeSwitchPage.DARK_MODE).should('exist')
    })
  })

  it('A user can switch the theme to light on each page', () => {
    cy.wrap(['', 'challenge/challenge-0', 'stats', 'about']).each((endpoint) => {
      cy.visit(`/${endpoint}`)
      cy.dataCy(ThemeSwitchPage.LIGHT_MODE_RADIO).click()
      cy.get(ThemeSwitchPage.DARK_MODE).should('not.exist')
    })
  })

  it('Light mode persists on each page', () => {
    cy.visit('/')
    cy.dataCy(ThemeSwitchPage.LIGHT_MODE_RADIO).click()
    cy.wrap(['', 'challenge/challenge-0', 'stats', 'about']).each((endpoint) => {
      cy.visit(`/${endpoint}`)
      cy.get(ThemeSwitchPage.DARK_MODE).should('not.exist')
    })
  })

  it('A user can switch theme to dark and back to light on each page', () => {
    cy.wrap(['', 'challenge/challenge-0', 'stats', 'about']).each((endpoint) => {
      cy.visit(`/${endpoint}`)
      cy.dataCy(ThemeSwitchPage.DARK_MODE_RADIO).click()
      cy.dataCy(ThemeSwitchPage.LIGHT_MODE_RADIO).click()
      cy.get(ThemeSwitchPage.DARK_MODE).should('not.exist')
    })
  })
})
