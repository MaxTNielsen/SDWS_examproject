Feature: GenerateToken
  Description: New token generation

  Scenario: Generate 1 token
    Given A customer with customerId "ef77f688-bd56-414d-9288-d610a12341de"
    When the customer requests to generate 1 new token
    Then token is created

  Scenario: Generate 5 tokens
    Given A customer with customerId "733fa7b5-ae3d-48e3-a781-6d62a4017034"
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
