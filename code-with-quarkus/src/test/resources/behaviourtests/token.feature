Feature: Token Management Feature

  Scenario: Handling token generation request
  	When I receive TOKEN_GENERATION_REQUEST
  	Then I have sent TOKEN_GENERATION_RESPONSE
  	
	
  Scenario: Handling token validation request
  	When I recieve TOKEN_VALIDATION_REQUEST
  	Then I have sent TOKEN_VALIDATION_RESPONSE
  	
  	