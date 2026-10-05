import LanguageTogglePage from '../pages/languageTogglePage'
import ChallengesPage from '../pages/challengesPage'

describe('Language Toggle Tests', () => {
  it('A user can switch the language on each page', () => {
    cy.wrap(['', 'challenge/challenge-0', 'stats', 'about']).each((endpoint) => {
      cy.visit(`/${endpoint}`)
      cy.dataCy(LanguageTogglePage.LANGUAGE_SELECTOR).find(LanguageTogglePage.LANGUAGE_TOGGLE)
        .should('have.text', LanguageTogglePage.ENGLISH_LABEL)
      cy.dataCy(LanguageTogglePage.LANGUAGE_SELECTOR).find(LanguageTogglePage.LANGUAGE_TOGGLE).click()
      cy.get(LanguageTogglePage.FRENCH_LINK).click()
      cy.url().should('contain', 'lang=fr')
      cy.dataCy(LanguageTogglePage.LANGUAGE_SELECTOR).find(LanguageTogglePage.LANGUAGE_TOGGLE)
        .should('have.text', LanguageTogglePage.FRENCH_LABEL)
      cy.dataCy(LanguageTogglePage.LANGUAGE_SELECTOR).find(LanguageTogglePage.LANGUAGE_TOGGLE).click()
      cy.get(LanguageTogglePage.ENGLISH_LINK).click()
      cy.url().should('contain', 'lang=en')
      cy.dataCy(LanguageTogglePage.LANGUAGE_SELECTOR).find(LanguageTogglePage.LANGUAGE_TOGGLE)
        .should('have.text', LanguageTogglePage.ENGLISH_LABEL)
    })
  })

  it('Language selection persists across pages', () => {
    cy.visit('/challenge/challenge-0')
    cy.dataCy(LanguageTogglePage.LANGUAGE_SELECTOR).find(LanguageTogglePage.LANGUAGE_TOGGLE).click()
    cy.get(LanguageTogglePage.FRENCH_LINK).click()
    cy.url().should('contain', 'lang=fr')
    cy.dataCy(LanguageTogglePage.LANGUAGE_SELECTOR).find(LanguageTogglePage.LANGUAGE_TOGGLE)
      .should('have.text', LanguageTogglePage.FRENCH_LABEL)
    cy.dataCy(ChallengesPage.CHALLENGE_DESCRIPTION).should('contain', 'Bienvenue dans le défi')
    cy.visit('/')
    cy.dataCy(LanguageTogglePage.LANGUAGE_SELECTOR).find(LanguageTogglePage.LANGUAGE_TOGGLE)
      .should('have.text', LanguageTogglePage.FRENCH_LABEL)
  })

  it('A user can switch back to English', () => {
    cy.visit('/')
    cy.dataCy(LanguageTogglePage.LANGUAGE_SELECTOR).find(LanguageTogglePage.LANGUAGE_TOGGLE).click()
    cy.get(LanguageTogglePage.FRENCH_LINK).click()
    cy.url().should('contain', 'lang=fr')
    cy.dataCy(LanguageTogglePage.LANGUAGE_SELECTOR).find(LanguageTogglePage.LANGUAGE_TOGGLE).click()
    cy.get(LanguageTogglePage.ENGLISH_LINK).click()
    cy.url().should('contain', 'lang=en')
    cy.dataCy(LanguageTogglePage.LANGUAGE_SELECTOR).find(LanguageTogglePage.LANGUAGE_TOGGLE)
      .should('have.text', LanguageTogglePage.ENGLISH_LABEL)
  })
})
