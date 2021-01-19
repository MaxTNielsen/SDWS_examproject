Feature: Reporting
  I want to use this template for my feature file

  #Scenario: something something
  	#Given the customer "Ryan" "Anderson" with CPR "1212121213" has a bank account with balance 1000
  	#And the customer is registered with DTUPay
  	#And the merchant "Yo" "Cockles" with CPR "1312121213" has a bank account with balance 2000
  	#And the merchant is registered with DTUPay
  	#When the merchant initiates a payment for 10 kr by the customer
  	#Then the payment is successful
  	#And the balance of the customer in the bank is 990
  	#And the balance of the merchant in the bank is 2010
  	
  #Scenario: SOAP transaction with negative amount
  	#Given the customer "Ryan" "Anderson" with CPR "1212121213" has a bank account with balance 1000
  	#And the customer is registered with DTUPay
  	#And the merchant "Yo" "Cockles" with CPR "1312121213" has a bank account with balance 2000
  	#And the merchant is registered with DTUPay
  	#When the merchant initiates a payment for 0 kr by the customer
  	#Then the payment is unsuccessful
  	
 	Scenario Outline: The payment scenario
		Given the customer "Ryan" "Anderson" with CPR "111145613" has a bank account with balance 1000
 		And the customer is registered with DTUPay
		And the merchant "Yo" "Cockles" with CPR "0111456213" has a bank account with balance 2000
		And the merchant is registered with DTUPay
		And the customer has a valid tokens
		When the merchant initiates a payment for <amount> kr by the customer
		Then the payment is successful
		And the balance of the customer in the bank is <customer>
  		And the balance of the merchant in the bank is <merchant>
		Examples:
			| amount | customer|merchant|
			|10      |990      |2010    |
			|20      |980      |2020    |