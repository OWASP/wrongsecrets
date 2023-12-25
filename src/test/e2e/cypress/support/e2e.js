// ***********************************************************
// This example support/e2e.js is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// Import commands.js using ES2015 syntax:
import './commands'
import addContext from 'mochawesome/addContext'
import fs from 'fs'

Cypress.on('test:after:run', (test, runnable) => {
  const screenshot = `../screenshots/${Cypress.spec.name}/${runnable.parent.title} -- ${test.title}.png`
  if (fs.existsSync(screenshot)) {
    addContext({ test }, screenshot)
  } else {
    console.log(screenshot + 'not found')
  }
})

// Alternatively you can use CommonJS syntax:
// require('./commands')
