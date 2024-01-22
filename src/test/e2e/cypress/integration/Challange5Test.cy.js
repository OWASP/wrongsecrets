describe('Challenge5 Tests', () => {
    it('Submitting a Correct Answer', () => {
        cy.visit('/challenge/challenge-5');

        cy.get('#answerfield').type('thisIsK8SConfigMap');
        cy.get('[data-cy=submit-textbox-btn]').click();

        cy.get('[data-cy=success-alert]').should('contain', 'correct');
    });

    it('Submitting an Incorrect Answer', () => {
        cy.visit('/challenge/challenge-5'); // Replace with the actual path

        cy.get('#answerfield').type('wrong_answer');
        cy.get('[data-cy=submit-textbox-btn]').click();

        cy.get('[data-cy=incorrect-alert]').should('contain', 'incorrect');
    });
});
