Feature: TokenServiceRequestMessage and TokenServiceResponseMessage serialization and deserialization
  Description: TokenServiceRequestMessage and TokenServiceResponseMessage class instances created. Serialize and deserialize it

  Scenario: TokenServiceRequestMessage serialization and deserialization
    Given A TokenServiceRequestMessage class instance
    When Serialize TokenServiceRequestMessage to byte[] and deserialize it
    Then The original TokenServiceRequestMessage class and the newly created one should be equal

  Scenario: TokenServiceResponseMessage serialization and deserialization
    Given A TokenServiceResponseMessage class instance
    And 5 newly created tokenID put into TokenServiceResponseMessage instance
    When Serialize TokenServiceResponseMessage to byte[] and deserialize it
    Then The original TokenServiceResponseMessage class and the newly created one should be equal
