Cypress.Commands.add('dataCy', (value) => {
  return cy.get(`[data-cy=${value}]`)
})

Cypress.Commands.add('getEnabledChallenges', () => {
  cy.request('/api/challenges').then((response) => {
    const numChallenges = response.body.data.length
    const enabledChallengeNames = []
    for (let i = 0; i < numChallenges; i++) {
      if (response.body.data[i].disabledEnv != null) {
        enabledChallengeNames.push(response.body.data[i].key)
      }
    }
    cy.wrap(enabledChallengeNames).as('enabledChallengeNames')
  })
})

Cypress.Commands.add('getDisabledChallenges', () => {
  cy.request('/api/challenges').then((response) => {
    const numChallenges = response.body.data.length
    const disabledChallengeNames = []
    for (let i = 0; i < numChallenges; i++) {
      if (response.body.data[i].disabledEnv === null) {
        disabledChallengeNames.push(response.body.data[i].key)
      }
    }
    cy.wrap(disabledChallengeNames).as('disabledChallengeNames')
  })
})

Cypress.Commands.add('getAllChallenges', () => {
  cy.request('/api/challenges').then((response) => {
    const numChallenges = response.body.data.length
    const allChallengeNames = []
    for (let i = 0; i < numChallenges; i++) {
      if (response.body.data[i].disabledEnv === null) {
        allChallengeNames.push(response.body.data[i].key)
      }
    }
    cy.wrap(allChallengeNames).as('allChallengeNames')
  })
})
