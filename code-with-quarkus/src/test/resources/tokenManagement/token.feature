Feature: Token Management Feature

	Scenario: Handling token generation request
		When I receive TOKEN_GENERATION_REQUEST
		Then I have sent TOKEN_GENERATION_RESPONSE


	Scenario: Handling token validation request
		When I receive TOKEN_VALIDATION_REQUEST
		Then I have sent TOKEN_VALIDATION_RESPONSE

	Scenario Outline: Customer request token
		Given a customer who has <existingTokens> tokens
		When he requests <numberOfTokens> tokens
		Then <numberOfResult> tokens are generated

		Examples:
			| existingTokens |numberOfTokens|numberOfResult|
			| 0              |1             |1             |
			| 0              |2             |2             |
			| 0              |3             |3             |
			| 0              |4             |4             |
			| 0              |5             |5             |
			| 0              |6             |0             |
			| 1              |1             |1             |
			| 1              |2             |2             |
			| 1              |3             |3             |
			| 1              |4             |4             |
			| 1              |5             |5             |
			| 1              |6             |0             |
			| 2              |1             |0             |
			| 2              |2             |0             |
			| 3              |1             |0             |
			| 3              |2             |0             |
			| 4              |1             |0             |
			| 4              |2             |0             |
			| 5              |1             |0             |
			| 5              |2             |0             |

	Scenario: Token Validation - token not used
		Given a token is valid
		When someone requests to valid this token
		Then the token is validated
		And a customer id has been returned

	Scenario: Token Validation - token already used
		Given a customer has used a token already
		When someone requests to valid this token
		Then the token manager returns that the token is not valid
		And a customer id has been returned

	Scenario: Token Validation - no such token
		Given a token which does not exist
		When someone requests to valid this token
		Then the token manager returns that the token is not valid
		And no customer id will be returned