Feature:
   Scenario: Register customer
     Given the customer with bank ID account "fba0ae26-00f5-4ea9-a734-863807c035b5" has a bank account
     When the customer register in DTUPay
     Then the customer is registered in DTUPay

    Scenario: Register merchant
      Given the merchant with bank account ID "7a95b604-2054-43d4-b696-f29fb9e33466" has a bank account
      When the merchant register in DTUPay
      Then the merchant is registered in DTUPay
