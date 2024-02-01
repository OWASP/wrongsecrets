const { defineConfig } = require('cypress')

module.exports = defineConfig({
    video: false,
    e2e: {
        baseUrl: 'http://localhost:8080/',
        specPattern: 'cypress/e2e/*.cy.js',
        supportFile: false,
        setupNodeEvents(on, config) {
            // implement node event listeners here
        },
    },
});
