Feature: Token Management Feature

	Scenario: Handling token generation request
		When I receive TOKEN_GENERATION_REQUEST
		Then I have sent TOKEN_GENERATION_RESPONSE


	Scenario: Handling token validation request
		When I receive TOKEN_VALIDATION_REQUEST
		Then I have sent TOKEN_VALIDATION_RESPONSE

#	Scenario: New customer request token
#		Given a new customer who doesn't have any tokens
#		When he request a token
#		Then a token is generated