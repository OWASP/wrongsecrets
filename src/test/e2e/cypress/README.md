# Cypress UI Tests

This project uses [Cypress](https://www.cypress.io/) to run UI tests for the project.

## How to run the tests

Go to `CypressIntegrationTest` and run it as a normal unit test in IntelliJ. This will start the application as
a `SpringBootTest` which uses Testcontainers to start running the Cypress tests.
The test then gathers all the output from the Cypress tests and displays them as normal unit test output in IntelliJ.

The reporting of the tests themselves will be located in `target/test-classes/e2e/cypress/reports/mochawesome`.

Please note that cypress requires the templates to have been build first.

### Run outside of maven
Want to run them outside of maven? Make sure you have node20 installed and the application running and listening to `http://localhost:8080` . Then do:

```shell
cd src/test/e2e/cypress
npx cypress run
```

## How to interact with elements

- To select an element in the UI, add a `data-cy` attribute to the HTML element and give it a unique value.
- To interact with the element in the test, use the `cy.dataCy()` command and pass the value of the `data-cy` attribute
  as an argument. For example:

```javascript
// HTML element
<h1 th:attr="data-cy='spoiler-title'">Spoiling secret</h1>

// Cypress test
cy.dataCy("spoiler-title").click();
```

## How the tests work

- The tests are located in the src/test/e2e folder.
- The tests loop through all the enabled challenges and check if they meet the expected criteria.

## When to create new tests

- A new UI test(s) only needs creating when the UI changes, not with each PR.
- If a new challenge is added or an existing challenge is modified no changes are needed.
- If a new UI element is added or an existing element is changed, update the data-cy attributes and the tests
  accordingly.
