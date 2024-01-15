import ThemeSwitchPage from '../pages/themeSwitchPage'

describe('Theme Switching Tests', () => {
    it('A user can switch the theme to dark on each page', () => {
        cy.visit(`/`);
        cy.dataCy(ThemeSwitchPage.DARK_MODE_CHECKBOX).then(($checkbox) => {
            cy.wrap(['', 'challenge/challenge-0', 'stats', 'about']).each((endpoint) => {
                cy.get(ThemeSwitchPage.CHECKBOX_SWITCH).check({force: true})
                if ($checkbox.is(":checked")) {
                    cy.get(ThemeSwitchPage.CHECKBOX_SWITCH).uncheck({force: true})
                }
                cy.get(ThemeSwitchPage.DARK_MODE).should('exist')
            });
        });
    });

    it('Dark mode persists on each page', () => {
        cy.visit('/')
        cy.dataCy(ThemeSwitchPage.DARK_MODE_CHECKBOX)
        cy.get(ThemeSwitchPage.CHECKBOX_SWITCH).check({force: true})
        cy.wrap(['', 'challenge/challenge-0', 'stats', 'about']).each((endpoint) => {
            cy.visit(`/${endpoint}`)
            cy.get(ThemeSwitchPage.CHECKBOX_SWITCH).should('be.checked')
        })
    })

    it('A user can switch the theme to light on each page', () => {
        cy.visit(`/`);
        cy.dataCy(ThemeSwitchPage.DARK_MODE_CHECKBOX).then(($checkbox) => {
            cy.wrap(['', 'challenge/challenge-0', 'stats', 'about']).each((endpoint) => {
                cy.get(ThemeSwitchPage.CHECKBOX_SWITCH).uncheck({ force: true })
                if (!$checkbox.is(":checked")) {
                    cy.get(ThemeSwitchPage.CHECKBOX_SWITCH).check({force: true})
                }
                cy.get(ThemeSwitchPage.DARK_MODE).should('not.exist')
            });
        });
    });

    it('Light mode persists on each page', () => {
        cy.visit('/')
        cy.get(ThemeSwitchPage.CHECKBOX_SWITCH).uncheck({force: true})
        cy.wrap(['', 'challenge/challenge-0', 'stats', 'about']).each((endpoint) => {
            cy.visit(`/${endpoint}`)
            cy.get(ThemeSwitchPage.CHECKBOX_SWITCH).should('not.be.checked');
        })
    })
      it('A user can switch theme to dark and back to light on each page', () => {
        cy.wrap(['', 'challenge/challenge-0', 'stats', 'about']).each((endpoint) => {
          cy.visit(`/${endpoint}`)
            cy.get(ThemeSwitchPage.CHECKBOX_SWITCH).check({force: true})
            cy.get(ThemeSwitchPage.CHECKBOX_SWITCH).uncheck({force: true})

          cy.get(ThemeSwitchPage.CHECKBOX_SWITCH).should('not.be.checked')
        })
    })
})
