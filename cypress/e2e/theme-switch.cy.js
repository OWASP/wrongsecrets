/// <reference types="cypress" />

describe('Theme switching', () => {
  beforeEach(() => {
  })

  it("A user can switch the theme to dark on each page", () => {
    cy.wrap(["", "challenge/0", "stats", "about"]).each((endpoint) => {
      cy.visit(`localhost:8080/${endpoint}`)
      cy.get('[data-cy=dark-mode-radio').click()
      cy.get('.dark-mode').should('exist')
    })
  })

  it("Dark mode persists on each page", () => {
    cy.visit('localhost:8080')
    cy.get('[data-cy=dark-mode-radio').click()
    cy.wrap(["", "challenge/0", "stats", "about"]).each((endpoint) => {
      cy.visit(`localhost:8080/${endpoint}`)
      cy.get('.dark-mode').should('exist')
    })
  })

  it("A user can switch the theme to light on each page", () => {
    cy.wrap(["", "challenge/0", "stats", "about"]).each((endpoint) => {
      cy.visit(`localhost:8080/${endpoint}`)
      cy.get('[data-cy=light-mode-radio').click() 
      cy.get('.dark-mode').should('not.exist')
    })
  })

  it("Light mode persists on each page", () => {
    cy.visit('localhost:8080')
    cy.get('[data-cy=light-mode-radio').click()
    cy.wrap(["", "challenge/0", "stats", "about"]).each((endpoint) => {
      cy.visit(`localhost:8080/${endpoint}`)
      cy.get('.dark-mode').should('not.exist')
    })
  })

  it("A user can switch theme to dark and back to light on each page", () => {
    cy.wrap(["", "challenge/0", "stats", "about"]).each((endpoint) => {
      cy.visit(`localhost:8080/${endpoint}`)
      cy.get('[data-cy=dark-mode-radio').click()
      cy.get('[data-cy=light-mode-radio').click()
      cy.get('.dark-mode').should('not.exist')
    })
  })
})
