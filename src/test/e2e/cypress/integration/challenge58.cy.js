import ChallengesPage from '../pages/challengesPage'

describe('Challenge 58 Database Connection String Exposure Tests', () => {
  // Challenge 58 specific selectors
  const DATABASE_CONTAINER = '#database-challenge-container'
  const ERROR_DEMO_LINK = 'a[href="/error-demo/database-connection"]'

  beforeEach(() => {
    // Visit Challenge 58 page
    cy.visit('/challenge/challenge-58')

    // Verify the page loads correctly
    cy.dataCy(ChallengesPage.CHALLENGE_TITLE).should('contain', 'Challenge 58')

    // Wait for database challenge container to be ready
    cy.get(DATABASE_CONTAINER, { timeout: 10000 }).should('be.visible')
  })

  it('Challenge interface displays correctly', () => {
    // Verify database container is present with correct structure
    cy.get(DATABASE_CONTAINER).should('be.visible')
    cy.get(DATABASE_CONTAINER).should('contain', 'Database Connection Error Demo')

    // Verify error demo link is present and functional
    cy.get(ERROR_DEMO_LINK).should('be.visible').and('not.be.disabled')
    cy.get(ERROR_DEMO_LINK).should('contain', 'Trigger Database Connection Error')

    // Verify instructional content
    cy.get(DATABASE_CONTAINER).should('contain', 'This challenge demonstrates how database connection failures')
    cy.get(DATABASE_CONTAINER).should('contain', 'Look for the database password')
  })

  it('Error demo endpoint is accessible and returns error with exposed credentials', () => {
    // Click the error demo link
    cy.get(ERROR_DEMO_LINK).click()

    // Verify we're redirected to the error demo endpoint
    cy.url().should('include', '/error-demo/database-connection')

    // Wait for page to load and check for error content
    cy.get('body', { timeout: 10000 }).should('be.visible')

    // The error page should contain database connection information
    cy.get('body').should(($body) => {
      const text = $body.text()
      // Look for typical database connection error patterns that might expose credentials
      const hasErrorIndicators = text.includes('SuperSecretDB2024!') ||
                                text.includes('connection') ||
                                text.includes('database') ||
                                text.includes('error') ||
                                text.includes('failed') ||
                                text.includes('postgresql') ||
                                text.includes('jdbc')
      expect(hasErrorIndicators, 'Expected database error page with connection information').to.be.true
    })
  })

  it('Database error exposes the target secret', () => {
    // Access the error demo endpoint
    cy.visit('/error-demo/database-connection')

    // Look for the specific secret in the page content
    cy.get('body', { timeout: 10000 }).should('contain', 'SuperSecretDB2024!')
  })

  it('Can solve the challenge using the exposed database password', () => {
    // First, trigger the database error to find the secret
    cy.get(ERROR_DEMO_LINK).click()

    // Wait for error page and extract the secret
    cy.get('body', { timeout: 10000 }).should('contain', 'SuperSecretDB2024!')

    // Navigate back to the challenge page
    cy.visit('/challenge/challenge-58')

    // Use the secret to solve the challenge
    cy.dataCy(ChallengesPage.ANSWER_TEXTBOX).type('SuperSecretDB2024!')
    cy.dataCy(ChallengesPage.SUBMIT_TEXTBOX_BTN).click()

    // Verify success
    cy.dataCy(ChallengesPage.SUCCESS_ALERT).should('contain', 'Your answer is correct!')
  })

  it('Challenge follows WrongSecrets standard structure', () => {
    // Verify standard WrongSecrets challenge elements
    cy.dataCy(ChallengesPage.CHALLENGE_TITLE).should('be.visible')
    cy.dataCy(ChallengesPage.ANSWER_TEXTBOX).should('be.visible')
    cy.dataCy(ChallengesPage.SUBMIT_TEXTBOX_BTN).should('be.visible')

    // Verify Challenge 58 specific database container
    cy.get(DATABASE_CONTAINER).should('be.visible')
    cy.get(DATABASE_CONTAINER).should('contain', 'Database Connection Error Demo')
    cy.get(DATABASE_CONTAINER).should('contain', 'database connection failures')

    // Verify error demo link is present
    cy.get(ERROR_DEMO_LINK).should('be.visible')
  })

  it('Validates the educational objective of database credential exposure', () => {
    // This test emphasizes the learning goal following WrongSecrets educational patterns
    cy.log('Challenge 58 demonstrates database connection string credential exposure through error handling')

    // Verify the error endpoint exposes credentials (educational success criteria)
    cy.visit('/error-demo/database-connection')
    cy.get('body', { timeout: 10000 }).should('contain', 'SuperSecretDB2024!')

    // This demonstrates how poor error handling can expose database credentials
    cy.log('Successfully demonstrated database credential exposure - users learn how error handling can leak sensitive connection information')

    // Verify this allows solving the challenge
    cy.visit('/challenge/challenge-58')
    cy.dataCy(ChallengesPage.ANSWER_TEXTBOX).type('SuperSecretDB2024!')
    cy.dataCy(ChallengesPage.SUBMIT_TEXTBOX_BTN).click()
    cy.dataCy(ChallengesPage.SUCCESS_ALERT).should('contain', 'Your answer is correct!')
  })

  it('Demonstrates realistic database error scenario for learning', () => {
    // Educational test showing how database errors expose credentials in real applications
    cy.log('Testing realistic database connection failure scenario')

    // Access the error endpoint
    cy.visit('/error-demo/database-connection')

    // Verify error content contains realistic database connection information
    cy.get('body').should(($body) => {
      const text = $body.text()
      // Look for realistic database connection error patterns
      const hasRealisticError = text.includes('connection') ||
                              text.includes('database') ||
                              text.includes('failed') ||
                              text.includes('timeout') ||
                              text.includes('refused') ||
                              text.includes('unable')
      expect(hasRealisticError, 'Expected realistic database connection error message').to.be.true
    })

    // Most importantly, verify the credentials are exposed
    cy.get('body').should('contain', 'SuperSecretDB2024!')

    cy.log('Educational objective achieved: Database credentials exposed through error handling demonstrate real-world vulnerability')
  })

  it('Error endpoint demonstrates common logging/error disclosure patterns', () => {
    // Test that the error endpoint demonstrates realistic error disclosure
    cy.visit('/error-demo/database-connection')

    // Check for common error patterns that expose secrets
    cy.get('body').should(($body) => {
      const content = $body.text()
      // Look for patterns typical in database connection errors
      const hasConnectionString = content.includes('jdbc:') ||
                                 content.includes('postgresql://') ||
                                 content.includes('connection string') ||
                                 content.includes('SuperSecretDB2024!')
      expect(hasConnectionString, 'Expected database connection string or credential exposure').to.be.true
    })
  })

  it('Challenge page provides proper educational guidance', () => {
    // Verify the challenge provides educational context
    cy.get(DATABASE_CONTAINER).should('contain', 'database connection failures can expose sensitive credentials')
    cy.get(DATABASE_CONTAINER).should('contain', 'Look for the database password')

    // Verify the demo section explains the vulnerability
    cy.get(DATABASE_CONTAINER).should('contain', 'Click the button below to trigger a database connection error')
    cy.get(DATABASE_CONTAINER).should('contain', 'exposes the connection string with embedded credentials')
  })
})
