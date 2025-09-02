const { defineConfig } = require('cypress')

module.exports = defineConfig({
  video: false,
  e2e: {
    baseUrl: 'https://arcane-scrubland-42646.herokuapp.com/',
    specPattern: 'cypress/integration/*.cy.js',
    reporter: 'cypress-multi-reporters',
    pageLoadTimeout: 60000,
    reporterOptions: {
      configFile: 'reporter-config.json'
    },
    userAgent: 'Cypress WrongSecrets E2E Tests (Arcane)',
    setupNodeEvents (on, config) {
      // implement node event listeners here
    }
  }
})
