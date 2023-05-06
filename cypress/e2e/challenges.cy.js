import ChallengesPage from '../pages/challengesPage';
const challengesPage = new ChallengesPage();

describe('example to-do app', () => {
  beforeEach(() => {
    cy.visit('localhost:8080')
  })

  it("Check all challenges load correctly", () => {
    cy.wrap([0,1,2,3,4,8,12,13,14,15,16,17,18,19]).each((challengeNum) => {
      cy.visit('localhost:8080')
      cy.dataCy(`"challenge ${challengeNum}-link"`).click()
      cy.url().should('contain', `challenge/${challengeNum}`)
      challengesPage.assertChallengePage()
    })
  })

  it("Check all hints load correctly", () => {
    cy.wrap([0,1,2,3,4,8,12,13,14,15,16,17,18,19]).each((challengeNum) => {
      cy.visit(`localhost:8080/challenge/${challengeNum}`)
      cy.dataCy(ChallengesPage.SHOW_HINTS_BTN).click()
      cy.dataCy(ChallengesPage.HINT_PARAGRAPH).should('be.visible')
    })
  })

  it("Check whats wrong section load correctly", () => {
    cy.wrap([0,1,2,3,4,8,12,13,14,15,16,17,18,19]).each((challengeNum) => {
      cy.visit(`localhost:8080/challenge/${challengeNum}`)
      cy.dataCy(ChallengesPage.WHATS_WRONG_BTN).click()
      cy.dataCy(ChallengesPage.WHATS_WRONG_PARAGRAPH).should('be.visible')
    })
  })

  it("Check reset button clears page", () => {
    cy.wrap([0,1,2,3,4,8,12,13,14,15,16,17,18,19]).each((challengeNum) => {
      cy.visit(`localhost:8080/challenge/${challengeNum}`)
      cy.dataCy(ChallengesPage.WHATS_WRONG_BTN).click()
      cy.dataCy(ChallengesPage.SHOW_HINTS_BTN).click()
      cy.dataCy(ChallengesPage.RESET_BTN).click()
      cy.dataCy(ChallengesPage.WHATS_WRONG_PARAGRAPH).should('not.be.visible')
      cy.dataCy(ChallengesPage.HINT_PARAGRAPH).should('not.be.visible')
    })
  })

  it("Clear button clears answer box", () => {
    cy.wrap([0,1,2,3,4,8,12,13,14,15,16,17,18,19]).each((challengeNum) => {
      cy.visit(`localhost:8080/challenge/${challengeNum}`)
      cy.dataCy(ChallengesPage.ANSWER_TEXTBOX).type("Tst")
      cy.dataCy(ChallengesPage.CLEAR_TEXTBOX_BTN).click()
      cy.dataCy(ChallengesPage.ANSWER_TEXTBOX).should("be.empty")    
    })
  })

  it("Submitting wrong answer gives warning", () => {
    cy.wrap([0,1,2,3,4,8,12,13,14,15,16,17,18,19]).each((challengeNum) => {
      cy.visit(`localhost:8080/challenge/${challengeNum}`)
      cy.dataCy(ChallengesPage.ANSWER_TEXTBOX).type("X")
      cy.dataCy(ChallengesPage.SUBMIT_TEXTBOX_BTN).click()
      cy.dataCy(ChallengesPage.INCORRECT_ALERT).should("contain", "Your answer is incorrect, try harder ;-)")    
    })
  })

  it("Submitting right answer gives success notification and progress bar", () => {
    cy.visit(`localhost:8080/challenge/0`)
    cy.dataCy(ChallengesPage.ANSWER_TEXTBOX).type("The first answer")
    cy.dataCy(ChallengesPage.SUBMIT_TEXTBOX_BTN).click()
    cy.dataCy(ChallengesPage.SUCCESS_ALERT).should("contain", "Your answer is correct!")  
    cy.dataCy(ChallengesPage.PROGRESS_BAR).should('be.visible').should('not.have.attr', 'aria-valuenow', '0')  
  })
})
