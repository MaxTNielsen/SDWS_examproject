Feature:
   Scenario: Register customer
     Given the customer with CPR "123456-1234" has a bank account
     Given the customer with ID "111" has a bank account
     When the customer register in DTUPay
     Then the customer is registered in DTUPay

    Scenario: Register merchant
      Given the merchant with ID "111" has a bank account
      When the merchant register in DTUPay
      Then the merchant is registered in DTUPay
