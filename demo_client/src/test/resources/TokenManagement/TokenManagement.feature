Feature: GenerateToken
  Description: New token generation

  Scenario: Generate 1 token
    Given A customer with customerId "5fec3c97-91d0-4c33-b65d-c9a81f786a70"
    When the customer requests to generate 1 new token
    Then token is created

  Scenario: Generate 5 tokens
    Given A customer with customerId "2a58183a-91c5-400c-b154-bfaad37d58d0"
    When the customer requests to generate 5 new token
    Then token is created

  Scenario: Generate 6 tokens
    Given A customer with customerId "00000-3333"
    When the customer requests to generate 6 new token
    Then token request fails

  Scenario: Generate tokens when already have 2 tokens
    Given A customer with customerId "00000-4444"
    And the customer already generated 2 new tokens
    When the customer requests to generate 1 new token
    Then token request fails
