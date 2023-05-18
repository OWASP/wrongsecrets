import SpoilersPage from '../pages/spoilersPage'

describe('Spoiler Tests', () => {
  beforeEach(() => {
    cy.getAllChallenges()
  })

  it('Check all hints load correctly', () => {
    cy.get('@allChallengeIds').then((allChallengeIds) => {
      cy.wrap(allChallengeIds).each((challengeNum) => {
        cy.visit(`/spoil-${challengeNum}`)
        cy.dataCy(SpoilersPage.SPOILER_TITLE).should('be.visible')
        cy.dataCy(SpoilersPage.SPOILER_TITLE).should('not.be.empty')
        cy.dataCy(SpoilersPage.SPOILER_ANSWER).should('be.visible')
        cy.dataCy(SpoilersPage.SPOILER_ANSWER).should('not.be.empty')
      })
    })
  })
})
