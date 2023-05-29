const { defineConfig } = require('cypress')

module.exports = defineConfig({
  video: false,
  env: {
    challengedockermtpath: 'src/test/resources/',
    keepasspath: 'src/test/resources/alibabacreds.kdbx'
  },
  e2e: {
    baseUrl: 'http://localhost:8080',
    setupNodeEvents (on, config) {
      // implement node event listeners here
    }
  }
})
