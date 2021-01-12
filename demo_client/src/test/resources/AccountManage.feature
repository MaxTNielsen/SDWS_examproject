Feature: AccountManagement
	

Scenario: Register customer
# Given the customer with CPR "123456-1234" has a bank account
Given the customer with ID "061100-7172" has a bank account
When the customer register in DTUPay
Then the customer is registered in DTUPay
