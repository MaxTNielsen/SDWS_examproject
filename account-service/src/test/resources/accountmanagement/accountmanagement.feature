Feature: Account Management
  Scenario: Register customer
    Given A random bank account with CPR "4567812311" and name "H" "P"
    Given the customer has a bank account
    When the customer register in DTUPay
    Then the customer is registered in DTUPay

  Scenario: Register merchant
    Given A random bank account with CPR "4256791242" and name "H" "P"
    Given the merchant has a bank account
    When the merchant register in DTUPay
    Then the merchant is registered in DTUPay