Feature: Payment
  Scenario: Successful Payment
    Given a customer with id "cid1"
    And a merchant with id "mid1"
    When the merchant initiates a payment for 10 kr by the customer
    Then the payment is successful

  Scenario: List of transactions
    Given a successful payment of 10 kr from customer "cid1" to merchant "mid1"
    When the manager asks for a list of transactions
    Then the list contains a transaction where customer "cid1" paid 10 kr to merchant "mid1"

  Scenario: Customer is not know
    Given a new customer with id "cid2"
    And the same merchant with id "mid1"
    When the merchant initiates a new payment for 10 kr by the customer
    Then the payment is not successful
    And a error message is returned saying customer not know

  Scenario: Successful Payment with SOAP
    Given the customer "yezzzzzzz" "man" with CPR "953282-7131" has a bank account
    And the balance of that account is 1000
    And the customer is registered with DTUPay
    And the merchant "yo" "Kucklez" with CPR number "557267-2291" has a bank account
    And the balance of his account is 2000
    And the merchant is registered with DTUPay
    When the merchant initiates a payment for "10" kr by the customer
    Then the payment is successful
    And the balance of the customer at the bank is 990 kr
    And the balance of the merchant at the bank is 2010 kr