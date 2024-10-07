const { defineConfig } = require('cypress')

module.exports = defineConfig({
  video: false,
  e2e: {
    baseUrl: 'http://localhost:8080',
    specPattern: 'cypress/integration/*.cy.js',
    reporter: 'cypress-multi-reporters',
    pageLoadTimeout: 60000,
    reporterOptions: {
      configFile: 'reporter-config.json'
    },
    setupNodeEvents (on, config) {
      // implement node event listeners here
    }
  }
})
