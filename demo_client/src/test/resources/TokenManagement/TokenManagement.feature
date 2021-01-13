Feature: GenerateToken
  Description: New token generation

  Scenario: New token generation
    Given A customer with cprNumber "00000-1234"
    When the customer requests to generate a new token
    Then a token is created
