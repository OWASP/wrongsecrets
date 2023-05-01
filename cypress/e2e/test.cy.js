/// <reference types="cypress" />

describe('example to-do app', () => {
  beforeEach(() => {
    cy.visit('localhost:8080')
  })

  it("Check all challenges load correctly", () => {
    cy.wrap([0,1,2,3,4,8,12,13,14,15,16,17,18,19]).each((challengeNum) => {
      cy.visit('localhost:8080')
      cy.get(`[data-cy="challenge ${challengeNum}-link"]`).click()
      cy.url().should('contain', `challenge/${challengeNum}`)
      cy.get(`[data-cy="challenge ${challengeNum}-title"]`).should('be.visible')
    })
  })

  it("Check all hints load correctly", () => {
    cy.wrap([0,1,2,3,4,8,12,13,14,15,16,17,18,19]).each((challengeNum) => {
      cy.visit(`localhost:8080/challenge/${challengeNum}`)
      cy.get(`[data-cy="show-hints-btn"]`).click()
      cy.get(`[data-cy="hint-paragraph"]`).should('be.visible')
      // Not currently possible due to the layout of an adoc being different to how it gets shown on the frontend, is it really needed?
      // cy.readFile(`src/main/resources/explanations/challenge${challengeNum}_hint.adoc`).then((content) => {
      //   cy.get(`[data-cy="hint-paragraph"]`).should('contain', content)
      // })
    })
  })

  it("Check whats wrong section load correctly", () => {
    cy.wrap([0,1,2,3,4,8,12,13,14,15,16,17,18,19]).each((challengeNum) => {
      cy.visit(`localhost:8080/challenge/${challengeNum}`)
      cy.get(`[data-cy="whats-wrong-btn"]`).click()
      cy.get(`[data-cy="whats-wrong-paragraph"]`).should('be.visible')
    })
  })
})
