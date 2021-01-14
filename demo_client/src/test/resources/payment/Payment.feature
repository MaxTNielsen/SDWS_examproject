
@tag
Feature: Payment
  I want to use this template for my feature file
    
  Scenario: SOAP transaction
  	Given the customer "Ryan" "Anderson" with CPR "1212121213" has a bank account with balance 1000
  	And the customer is registered with DTUPay
  	And the merchant "Yo" "Cockles" with CPR "1312121213" has a bank account with balance 2000
  	And the merchant is registered with DTUPay
  	When the merchant initiates a payment for 10 kr by the customer
  	Then the payment is successful
  	And the balance of the customer in the bank is 990
  	And the balance of the merchant in the bank is 2010
  	
  Scenario: SOAP transaction with negative amount
  	Given the customer "Ryan" "Anderson" with CPR "1212121213" has a bank account with balance 1000
  	And the customer is registered with DTUPay
  	And the merchant "Yo" "Cockles" with CPR "1312121213" has a bank account with balance 2000
  	And the merchant is registered with DTUPay
  	When the merchant initiates a payment for 0 kr by the customer
  	Then the payment is unsuccessful
  	
 	Scenario: The payment
 		Given the customer "Ryan" "Anderson" with CPR "1212121213" has a bank account with balance 1000
		And the merchant "Yo" "Cockles" with CPR "1312121213" has a bank account with balance 2000