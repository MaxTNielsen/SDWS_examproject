Feature:
   Scenario: Register customer
     Given the customer with bank ID account "7196e1f5-7b76-42c5-aa22-03ef613db5d1" has a bank account
     When the customer register in DTUPay
     Then the customer is registered in DTUPay

    Scenario: Register merchant
      Given the merchant with bank account ID "a0aad743-602b-40ef-989e-22af11a39d51" has a bank account
      When the merchant register in DTUPay
      Then the merchant is registered in DTUPay
