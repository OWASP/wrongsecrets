# Cypress UI Tests

This project uses [Cypress](https://www.cypress.io/) to run UI tests for the project.

## How to run the tests

- Clone this repository and navigate to the project folder.
- Run `npm install` to install the dependencies.
- Run `npm run test:open` to launch the Cypress Test Runner.

## How to interact with elements

- To select an element in the UI, add a `data-cy` attribute to the HTML element and give it a unique value.
- To interact with the element in the test, use the `cy.dataCy()` command and pass the value of the `data-cy` attribute as an argument. For example:

```javascript
// HTML element
<h1 th:attr="data-cy='spoiler-title'">Spoiling secret</h1>

// Cypress test
cy.dataCy("spoiler-title").click();
```

## How the tests work
- The tests are located in the cypress/e2e folder.
- The tests loop through all the enabled challenges and check if they meet the expected criteria.

## When to create new tests
- A new UI test(s) only needs creating when the UI changes, not with each PR.
- If a new challenge is added or an existing challenge is modified no changes are needed.
- If a new UI element is added or an existing element is changed, update the data-cy attributes and the tests accordingly.
