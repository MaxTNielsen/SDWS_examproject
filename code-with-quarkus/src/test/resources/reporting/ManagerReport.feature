Feature: The manager asks the system to create reports after some predefined transactions

  Scenario: Manager ask the list of transaction after "C1" pays 15 Kr to "M1", "C2" pays 123 Kr to "M2", "C3" pays 18 Kr to "M1" and "C1" pays 999 Kr to "M3"
    Given an empty transaction history
    When "C1" pays 15 Kr to "M1"
    And "C2" pays 123 Kr to "M2"
    And "C3" pays 18 Kr to "M1"
    And "C1" pays 999 Kr to "M3"
    Then The manager report should contain 4 transactions

  Scenario: Manager ask the money flow after "C1" pays 25 Kr to "M1", "C1" pays 541 Kr to "M2", "C3" pays 128 Kr to "M1" and "C1" pays 125 Kr to "M3"
    Given an empty transaction history
    When "C1" pays 25 Kr to "M1"
    And "C1" pays 541 Kr to "M2"
    And "C3" pays 128 Kr to "M1"
    And "C1" pays 125 Kr to "M3"
    Then The money flow should be 819 Kr

  Scenario: Manager ask the money flow after "C1" pays 25 Kr to "M1", "C1" pays 541 Kr to "M2", "C3" pays 128 Kr to "M1" and first transaction is being refunded
    Given an empty transaction history
    When "C1" pays 25 Kr to "M1"
    And "C1" pays 541 Kr to "M2"
    And "C3" pays 128 Kr to "M1"
    And 1 st/rd/nd Transaction is refunded
    Then The 1 st/rd/nd Transaction should be refunded



