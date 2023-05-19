export default class ChallengesPage {
  static CHALLENGE_TITLE = 'challenge-title'
  static CHALLENGE_DESCRIPTION = 'challenge-description'
  static SHOW_HINTS_BTN = 'show-hints-btn'
  static HINT_PARAGRAPH = 'hint-paragraph'
  static WHATS_WRONG_BTN = 'whats-wrong-btn'
  static WHATS_WRONG_PARAGRAPH = 'whats-wrong-paragraph'
  static RESET_BTN = 'reset-btn'
  static ANSWER_TEXTBOX = 'answer-textbox'
  static CLEAR_TEXTBOX_BTN = 'clear-textbox-btn'
  static SUBMIT_TEXTBOX_BTN = 'submit-textbox-btn'
  static INCORRECT_ALERT = 'incorrect-alert'
  static SUCCESS_ALERT = 'success-alert'
  static DISABLED_alert = 'disabled-alert'
  static PROGRESS_BAR = 'progress-bar'

  assertEnabledChallengePage (challengeNum) {
    cy.url().should('contain', `challenge/${challengeNum}`)
    cy.dataCy(ChallengesPage.CHALLENGE_TITLE).should('be.visible')
    cy.dataCy(ChallengesPage.CHALLENGE_DESCRIPTION).should('be.visible')
    cy.dataCy(ChallengesPage.SHOW_HINTS_BTN).should('be.visible')
    cy.dataCy(ChallengesPage.WHATS_WRONG_BTN).should('be.visible')
    cy.dataCy(ChallengesPage.RESET_BTN).should('be.visible')
    cy.dataCy(ChallengesPage.SUBMIT_TEXTBOX_BTN).should('be.visible')
    cy.dataCy(ChallengesPage.CLEAR_TEXTBOX_BTN).should('be.visible')
  }

  assertDisabledChallengePage (challengeNum) {
    cy.url().should('contain', `challenge/${challengeNum}`)
    cy.dataCy(ChallengesPage.CHALLENGE_TITLE).should('be.visible')
    cy.dataCy(ChallengesPage.CHALLENGE_DESCRIPTION).should('be.visible')
    cy.dataCy(ChallengesPage.SHOW_HINTS_BTN).should('be.visible')
    cy.dataCy(ChallengesPage.WHATS_WRONG_BTN).should('be.visible')
    cy.dataCy(ChallengesPage.RESET_BTN).should('be.visible')
    cy.dataCy(ChallengesPage.SUBMIT_TEXTBOX_BTN).should('be.visible')
    cy.dataCy(ChallengesPage.CLEAR_TEXTBOX_BTN).should('be.visible')
    cy.dataCy(ChallengesPage.INCORRECT_ALERT).should('be.visible')
    cy.dataCy(ChallengesPage.DISABLED_alert).should('be.visible')
  }

  selectChallenge (challengeNum) {
    cy.get('.form-select').select('All')
    cy.dataCy(`"challenge ${challengeNum}-link"`).click()
  }
}
