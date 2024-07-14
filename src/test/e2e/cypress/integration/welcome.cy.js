import SpoilersPage from '../pages/spoilersPage'

describe('Welcome page tests', () => {
  it('Check spoiler example on welcome screen is working', () => {
    cy.visit('/')
    cy.dataCy('show-secret-spoiler-link').click()
    cy.dataCy(SpoilersPage.SPOILER_TITLE).should('be.visible')
  })
})
