Feature: GenerateToken
  Description: New token generation

  Scenario: New token generation
    Given a customer with CPR-number "051272-2514"
    When the client request new token
    Then a new token is created whose customer-ID is "051272-2514" 