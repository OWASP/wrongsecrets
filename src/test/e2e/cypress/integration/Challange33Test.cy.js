describe('Challenge7 Tests', () => {
    it('Submitting a Correct Answer', () => {
        cy.visit('/challenge/challenge-33');

        cy.get('#answerfield').type('This was a standardValue as SecureSecret');
        cy.get('[data-cy=submit-textbox-btn]').click();

        cy.get('[data-cy=success-alert]').should('contain', 'correct');
    });

    it('Submitting an Incorrect Answer', () => {
        cy.visit('/challenge/challenge-33');

        cy.get('#answerfield').type('wrong_answer');
        cy.get('[data-cy=submit-textbox-btn]').click();

        cy.get('[data-cy=incorrect-alert]').should('contain', 'incorrect');
    });
});
