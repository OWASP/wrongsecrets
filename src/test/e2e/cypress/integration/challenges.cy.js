import ChallengesPage from '../pages/challengesPage'
import HomePage from '../pages/homePage'
const challengesPage = new ChallengesPage()

describe('Challenge Tests', () => {
  beforeEach(() => {
    cy.getEnabledChallenges()
    cy.getDisabledChallenges()
  })

  it('Check all enabled challenges display correctly', () => {
    cy.get('@enabledChallengeNames').then((enabledChallengeNames) => {
      cy.wrap(enabledChallengeNames).each((challengeName) => {
        cy.visit('/')
        challengesPage.selectChallenge(challengeName)
        challengesPage.assertEnabledChallengePage(challengeName)
      })
    })
  })

  it('Check all disabled challenges display correctly', () => {
    cy.get('@disabledChallengeNames').then((disabledChallengeNames) => {
      cy.wrap(disabledChallengeNames).each((challengeName) => {
        cy.visit(`/challenge/${challengeName}`)
        challengesPage.assertDisabledChallengePage(challengeName)
      })
    })
  })

  it('Check all hints display correctly', () => {
    cy.get('@enabledChallengeNames').then((enabledChallengeNames) => {
      cy.wrap(enabledChallengeNames).each((challengeName) => {
        cy.visit(`/challenge/${challengeName}`)
        cy.dataCy(ChallengesPage.SHOW_HINTS_BTN).click()
        cy.dataCy(ChallengesPage.HINT_PARAGRAPH).should('be.visible')
      })
    })
  })

  it('Check whats wrong section display correctly', () => {
    cy.get('@enabledChallengeNames').then((enabledChallengeNames) => {
      cy.wrap(enabledChallengeNames).each((challengeName) => {
        cy.visit(`/challenge/${challengeName}`)
        cy.dataCy(ChallengesPage.WHATS_WRONG_BTN).click()
        cy.dataCy(ChallengesPage.WHATS_WRONG_PARAGRAPH).should('be.visible')
      })
    })
  })

  it('Check reset button clears page', () => {
    cy.get('@enabledChallengeNames').then((enabledChallengeNames) => {
      cy.wrap(enabledChallengeNames).each((challengeName) => {
        cy.visit(`/challenge/${challengeName}`)
        cy.dataCy(ChallengesPage.WHATS_WRONG_BTN).click()
        cy.dataCy(ChallengesPage.SHOW_HINTS_BTN).click()
        cy.dataCy(ChallengesPage.RESET_BTN).click()
        cy.dataCy(ChallengesPage.WHATS_WRONG_PARAGRAPH).should('not.be.visible')
        cy.dataCy(ChallengesPage.HINT_PARAGRAPH).should('not.be.visible')
      })
    })
  })

  it('Clear button clears answer box', () => {
    cy.get('@enabledChallengeNames').then((enabledChallengeNames) => {
      cy.wrap(enabledChallengeNames).each((challengeName) => {
        cy.visit(`/challenge/${challengeName}`)
        cy.dataCy(ChallengesPage.ANSWER_TEXTBOX).type('Tst')
        cy.dataCy(ChallengesPage.CLEAR_TEXTBOX_BTN).click()
        cy.dataCy(ChallengesPage.ANSWER_TEXTBOX).should('be.empty')
      })
    })
  })

  it('Submitting wrong answer gives warning', () => {
    cy.get('@enabledChallengeNames').then((enabledChallengeNames) => {
      cy.wrap(enabledChallengeNames).each((challengeName) => {
        cy.visit(`/challenge/${challengeName}`)
        cy.dataCy(ChallengesPage.ANSWER_TEXTBOX).type('X')
        cy.dataCy(ChallengesPage.SUBMIT_TEXTBOX_BTN).click()
        cy.dataCy(ChallengesPage.INCORRECT_ALERT).should('contain', 'Your answer is incorrect, try harder ;-)')
      })
    })
  })

  it('Submitting right answer gives success notification and progress bar', () => {
    cy.visit('/challenge/challenge-0')
    cy.dataCy(ChallengesPage.ANSWER_TEXTBOX).type('The first answer')
    cy.dataCy(ChallengesPage.SUBMIT_TEXTBOX_BTN).click()
    cy.dataCy(ChallengesPage.SUCCESS_ALERT).should('contain', 'Your answer is correct!')
    cy.dataCy(ChallengesPage.PROGRESS_BAR).should('be.visible').should('not.have.attr', 'aria-valuenow', '0')
  })

  it('Submitting right answer gives visual cue on homepage that the challenge is successfully solved', () => {
    cy.visit('/challenge/challenge-0')
    cy.dataCy(ChallengesPage.ANSWER_TEXTBOX).type('The first answer')
    cy.dataCy(ChallengesPage.SUBMIT_TEXTBOX_BTN).click()
    cy.visit('/')
    cy.dataCy(HomePage.CHALLENGE_TABLE_ROW).first().should('have.class', 'solved')
    cy.dataCy(HomePage.CHALLENGE_0_COMPLETED).should('exist')
    cy.dataCy(HomePage.TOTAL_SCORE).scrollIntoView()
    cy.dataCy(HomePage.TOTAL_SCORE).should('contain', '100')
  })
})
