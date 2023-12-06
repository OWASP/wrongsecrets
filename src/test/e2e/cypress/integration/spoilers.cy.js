import SpoilersPage from '../pages/spoilersPage'

describe('Spoiler Tests', () => {
  beforeEach(() => {
    cy.getAllChallenges()
  })

  it('Check all spoiler pages display correctly (e.g. have a title and some data)', () => {
    cy.get('@allChallengeNames').then((allChallengeNames) => {
      cy.wrap(allChallengeNames).each((challengeName) => {
        cy.visit(`/spoil/${challengeName}`)
        cy.dataCy(SpoilersPage.SPOILER_TITLE).should('be.visible')
        cy.dataCy(SpoilersPage.SPOILER_TITLE).should('not.be.empty')
        cy.dataCy(SpoilersPage.SPOILER_ANSWER).should('be.visible')
        cy.dataCy(SpoilersPage.SPOILER_ANSWER).should('not.be.empty')
      })
    })
  })
})
