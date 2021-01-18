@tag
Feature: Payment

  @tag1
  Scenario: Successful payment
    Given the customer "Ryan" "Anderson" with CPR "1212121213" has a bank account with balance 1000
    And the merchant "Yo" "Cockles" with CPR "1312121213" has a bank account with balance 2000
    And the token "123123" is valid
    When the merchant initiates a transaction for 10
	 	Then the transaction is successful
    And the balance of the customer is 990
    And the balance of the merchant is 2010
    
  Scenario: insuficient amount in customeraccount
  	Given the customer "Ryan" "Anderson" with CPR "1212121213" has a bank account with balance 5
    And the merchant "Yo" "Cockles" with CPR "1312121213" has a bank account with balance 2000
    And the token "123123" is valid
    When the merchant initiates a transaction for 10
		Then the transaction is unsuccessful
    And the balance of the customer is 5
    And the balance of the merchant is 2000
  
	Scenario: transaction with negative amount
  	Given the customer "Ryan" "Anderson" with CPR "1212121213" has a bank account with balance 1000
    And the merchant "Yo" "Cockles" with CPR "1312121213" has a bank account with balance 2000
    And the token "123123" is valid
    When the merchant initiates a transaction for -5
		Then the transaction is unsuccessful
    And the balance of the customer is 1000
    And the balance of the merchant is 2000
    
  #Scenario: Recieving message in queue
  #	Given the customer "Ryan" "Anderson" with CPR "1212121213" has a bank account with balance 1000
  #  And the merchant "Yo" "Cockles" with CPR "1312121213" has a bank account with balance 2000
  #  And the token "123123" is valid
  #  When the merchant initiates a transaction from the queue for 10
	# 	Then the transaction is successful
  #  And the balance of the customer is 990
  #  And the balance of the merchant is 2010