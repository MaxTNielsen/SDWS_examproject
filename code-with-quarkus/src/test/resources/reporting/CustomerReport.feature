#Feature: "Customer1" asks the system to create reports after some predefined transactions
#
#  Scenario: "Customer1" pays 25 Kr to "M1" and 444 Kr to "M2"
#    When "Customer1" pays 25 Kr to "M1"
#    And "Customer1" pays 444 Kr to "M2"
#    Then The customer report should contain 2 transactions
#
#  Scenario: "Customer1" pays 25 Kr to "M1" and 444 Kr to "M2" and the second transaction is being refunded
#    When "Customer1" pays 25 Kr to "M1"
#    And "Customer1" pays 444 Kr to "M2"
#    And "Second" Transaction is refunded
#    Then The state of the "Second" transaction should be refunded