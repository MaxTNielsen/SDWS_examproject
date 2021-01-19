Feature: AccountManagement

    # Scenario: Register customer
    #     Given the customer with CPR "123456-1234" has a bank account
    #     Given the customer with ID "111" has a bank account
    #     When the customer register in DTUPay
    #     Then the customer is registered in DTUPay

    # Scenario: Register merchant
    #     Given the merchant with ID "111" has a bank account
    #     When the merchant register in DTUPay
    #     Then the merchant is registered in DTUPay

    Scenario: Register a Customer in the Bank and in DTUPay
        Given the customer with CPR "423643-12312312312323" and with the name "Pharles" "Sontana" and the balance 1000 kr
        When the customer register in the bank
        When the customer register in DTUPay with his CPR as ID
        Then the customer has gotten an account in DTUPay

    Scenario: Register a Customer in the Bank and in DTUPay
        Given the customer with CPR "783434-2342112312321312312323123" and with the name "Pharles" "Sontana" and the balance 1000 kr
        When the customer register in the bank
        When the customer register in DTUPay with his CPR as ID
        Then the customer has gotten an account in DTUPay

    Scenario:  Register a Merchant in the Bank and in DTUPay
        Given the merchant with CPR "190512-335012311231232123123123123" and with the name "Hubert" "Claus" and the balance 2000 kr
        When the merchant register in the bank
        When the mechant register in DTUPay with his CPR as ID
        Then the merchant has gotten an account in DTUPay