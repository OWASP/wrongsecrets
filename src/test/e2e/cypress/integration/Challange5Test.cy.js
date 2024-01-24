describe('Challenge5 Tests', () => {
    it('Submitting a Correct Answer', () => {
        cy.visit('/spoil/challenge-5');
        cy.get('[data-cy=spoiler-answer]').invoke('text').then(spoilerAnswer => {
            cy.visit('/challenge/challenge-5');

            cy.get('#answerfield').type(spoilerAnswer.trim());
            cy.get('[data-cy=submit-textbox-btn]').click();

            cy.get('[data-cy=success-alert]').should('contain', 'correct');
        });
    });

    it('Submitting an Incorrect Answer', () => {
        cy.visit('/challenge/challenge-5');

        cy.get('#answerfield').type('wrong_answer');
        cy.get('[data-cy=submit-textbox-btn]').click();

        cy.get('[data-cy=incorrect-alert]').should('contain', 'incorrect');
    });
});
