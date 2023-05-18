Cypress.Commands.add('dataCy', (value) => {
  return cy.get(`[data-cy=${value}]`)
})

Cypress.Commands.add('getEnabledChallenges', () => {
  cy.request('/api/challenges').then((response) => {
    const numChallenges = response.body.data.length
    const enabledChallengeIds = []
    for (let i = 0; i < numChallenges; i++) {
      if (response.body.data[i].disabledEnv != null) {
        enabledChallengeIds.push(response.body.data[i].id - 1)
      }
    }
    cy.wrap(enabledChallengeIds).as('enabledChallengeIds')
  })
})

Cypress.Commands.add('getDisabledChallenges', () => {
  cy.request('/api/challenges').then((response) => {
    const numChallenges = response.body.data.length
    const disabledChallengeIds = []
    for (let i = 0; i < numChallenges; i++) {
      if (response.body.data[i].disabledEnv === null) {
        disabledChallengeIds.push(response.body.data[i].id - 1)
      }
    }
    cy.wrap(disabledChallengeIds).as('disabledChallengeIds')
  })
})

Cypress.Commands.add('getAllChallenges', () => {
  cy.request('/api/challenges').then((response) => {
    const numChallenges = response.body.data.length
    const allChallengeIds = Array.from({ length: numChallenges }, (v, i) => i)
    cy.wrap(allChallengeIds).as('allChallengeIds')
  })
})
