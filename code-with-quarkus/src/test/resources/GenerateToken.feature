Feature: GenerateToken
  Description: New token generation

  Scenario: New token generation
    Given a customer with CPR-number "051272-2514"
    When the client request new token
    Then a new token is created whose customer-ID is "051272-2514"

  Scenario: New token generation success
    Given a customer with 1 unused token
    When the customer requests a new token
    Then the customer will have 2 unused tokens

  Scenario: New token generation success
    Given a customer with 2 unused token
    When the customer requests a new token
    Then the customer will have 2 unused tokens