describe('Challenge6 Tests', () => {
    it('Submitting a Correct Answer', () => {
        cy.visit('/challenge/challenge-6');

        cy.get('#answerfield').type('this is apassword');
        cy.get('[data-cy=submit-textbox-btn]').click();

        cy.get('[data-cy=success-alert]').should('contain', 'correct');
    });

    it('Submitting an Incorrect Answer', () => {
        cy.visit('/challenge/challenge-6');

        cy.get('#answerfield').type('wrong_answer');
        cy.get('[data-cy=submit-textbox-btn]').click();

        cy.get('[data-cy=incorrect-alert]').should('contain', 'incorrect');
    });
});
