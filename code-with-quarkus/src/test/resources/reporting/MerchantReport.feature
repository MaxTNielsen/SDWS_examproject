Feature: "Merchant1" asks the system to create reports after some predefined transactions

  Scenario: "Customer1" pays 123 Kr to "M1", 444 Kr to "M2" and "Customer2" 147 to "M1"
    Given an empty transaction history
    When "Customer1" pays 123 Kr to "M1"
    And "Customer1" pays 444 Kr to "M2"
    And "Customer2" pays 147 Kr to "M1"
    Then The merchant report of "M1" from the last 15 secs should contain 2 transactions

  Scenario: "Customer1" pays 25 Kr to "M1" and 444 Kr to "M2" and the second transaction is being refunded
    Given an empty transaction history
    When "Customer1" pays 25 Kr to "M1"
    And "Customer1" pays 444 Kr to "M2"
    And 2 st/rd/nd Transaction is refunded
    Then The merchant report of "M2" from the last 15 secs should contain 1 transactions

  Scenario: "Customer1" pays 25 Kr to "M1" and 444 Kr to "M2", waits 3 secs, pays 123 Kr to "M3" and ask a report from the last 2 secs
    Given an empty transaction history
    When "Customer1" pays 25 Kr to "M1"
    And "Customer1" pays 444 Kr to "M2"
    And waits 3 secs
    And "Customer1" pays 123 Kr to "M3"
    Then The merchant report of "M1" from the last 2 secs should contain 0 transactions
    And The merchant report of "M3" from the last 2 secs should contain 1 transactions

