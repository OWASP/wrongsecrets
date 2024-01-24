describe('Challenge7 Tests', () => {
    it('Submitting a Correct Answer', () => {
        cy.visit('/challenge/challenge-7');

        cy.get('#answerfield').type('N9EXxGxsm2j7N88t7/tL5A==');
        cy.get('[data-cy=submit-textbox-btn]').click();

        cy.get('[data-cy=success-alert]').should('contain', 'correct');
    });

    it('Submitting an Incorrect Answer', () => {
        cy.visit('/challenge/challenge-7');

        cy.get('#answerfield').type('wrong_answer');
        cy.get('[data-cy=submit-textbox-btn]').click();

        cy.get('[data-cy=incorrect-alert]').should('contain', 'incorrect');
    });
});
