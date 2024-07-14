describe('Challenge45 Tests', () => {
  it('Submitting a Correct Answer', () => {
    // Visit the spoiler page and extract the spoiler
    cy.visit('/spoil/challenge-45')
    cy.get('[data-cy=spoiler-answer]').invoke('text').then(spoilerAnswer => {
      // Asserting that the spoiler is not a default value
      expect(spoilerAnswer.trim()).to.not.equal('if_you_see_this_please_use_K8S_and_Vault')
      expect(spoilerAnswer.trim()).to.not.equal('ACTUAL_ANSWER_CHALLENGE7')
      expect(spoilerAnswer.trim()).to.not.be.empty

      // Visit the challenge page and submit the spoiler as the answer
      cy.visit('/challenge/challenge-45')
      cy.get('#answerfield').type(spoilerAnswer.trim())
      cy.get('[data-cy=submit-textbox-btn]').click()
      cy.get('[data-cy=success-alert]').should('contain', 'correct')
    })
  })

  it('Submitting an Incorrect Answer', () => {
    cy.visit('/challenge/challenge-45')

    // Use a known incorrect answer
    cy.get('#answerfield').type('definitely_wrong_answer')
    cy.get('[data-cy=submit-textbox-btn]').click()

    // Check for incorrect alert
    cy.get('[data-cy=incorrect-alert]').should('contain', 'incorrect')
  })
})
