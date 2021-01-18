Feature: GenerateToken
  Description: New token generation

  Scenario: Generate 1 token
    Given A customer with customerId "0aa0383e-df5a-4afb-85f7-0dd5d33dfb77"
    When the customer requests to generate 1 new token
    Then token is created

  Scenario: Generate 5 tokens
    Given A customer with customerId "cfb38983-1e05-4200-a7dc-86948f405de6"
    When the customer requests to generate 5 new token
    Then token is created

  Scenario: Generate 6 tokens
    Given A customer with customerId "cfb38983-1e05-4200-a7dc-86948f405de6"
    When the customer requests to generate 6 new token
    Then token request fails

  Scenario: Generate tokens when already have 2 tokens
    Given A customer with customerId "cfb38983-1e05-4200-a7dc-86948f405de6"
    And the customer already generated 2 new tokens
    When the customer requests to generate 1 new token
    Then token request fails
