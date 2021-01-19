package tokenmanagement;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import tokenManagement.messaging.model.*;
import tokenManagement.model.Token;
import tokenManagement.service.TokenManager;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class TokenManagerSteps {
	TokenManager tokenManager;
	Event event;
	Event response;
	String customerId;
	ArrayList<String> result = null;
	String testToken;
	TokenValidationResponse tokenValidationResponse;
	public TokenManagerSteps() {
		tokenManager = new TokenManager();
	}

	@When("I receive TOKEN_GENERATION_REQUEST")
	public void i_receive_token_generation_request() throws Exception {
		TokenGenerationRequest request = new TokenGenerationRequest("customerid",1);
		Object[] objects = new Object[3];
		objects[0] = "c";
		objects[1] = request.getCustomerId();
		objects[2] = request;
		Event event = new Event("TOKEN_GENERATION_REQUEST", objects);
		response = tokenManager.receiveEvent(event);

	}

	@Then("I have sent TOKEN_GENERATION_RESPONSE")
	public void i_have_sent_token_generation_response() {

		assertTrue("TOKEN_GENERATION_RESPONSE".equals(response.getEventType()));
	}

	@When("I receive TOKEN_VALIDATION_REQUEST")
	public void i_recieve_token_validation_request() throws Exception {
		TokenValidationRequest request = new TokenValidationRequest();
		Object[] objects = new Object[3];
		objects[0] = "c";
		objects[1] = "something";
		objects[2] = request;
		Event event = new Event("TOKEN_VALIDATION_REQUEST", objects);
		//Event event = new Event("TOKEN_VALIDATION_REQUEST", new Object[] { request });
		response = tokenManager.receiveEvent(event);
	}

	@Then("I have sent TOKEN_VALIDATION_RESPONSE")
	public void i_have_sent_token_validation_response() {
		assertTrue("TOKEN_VALIDATION_RESPONSE".equals(response.getEventType()));
	}


	@Given("a customer who has {int} tokens")
	public void a_customer_who_has_tokens(Integer int1) {
		tokenManager.getNewTokens("000000-0000", int1);
	}


	@When("he requests {int} tokens")
	public void he_requests_tokens(Integer int1) {
		result = tokenManager.getNewTokens("000000-0000", int1);
	}

	@Then("{int} tokens are generated")
	public void tokens_are_generated(Integer int1) {
		if (int1>0)
			assertEquals(int1,result.size());
		else
			assertNull(result);
	}
	@Given("a token is valid")
	public void a_token_is_valid() {
		customerId="000000-1111";
		testToken = "new-token-testing";
		tokenManager.addToken(new Token(testToken,customerId));

	}

	@When("someone requests to valid this token")
	public void someone_requests_to_valid_this_token() {
		tokenValidationResponse = tokenManager.validateToken(testToken);
	}

	@Then("the token is validated")
	public void the_token_is_validated() {
		assertTrue(tokenValidationResponse.isValid());
	}

	@Then("a customer id has been returned")
	public void a_customer_id_has_been_returned() {
		assertEquals(customerId,tokenValidationResponse.getCustomerId());
	}

	@Given("a customer has used a token already")
	public void a_customer_has_used_a_token_already() {
		customerId="000000-1111";
		testToken = "new-token-testing";
		Token token = new Token(testToken,customerId);
		tokenManager.addToken(token);
		tokenManager.validateToken(token.getId());
	}

	@Then("the token manager returns that the token is not valid")
	public void the_token_manager_returns_that_the_token_is_not_valid() {
		assertFalse(tokenValidationResponse.isValid());
	}

	@Given("a token which does not exist")
	public void a_token_which_does_not_exist() {
		testToken = "some-token-that-not-exist";

	}

	@Then("no customer id will be returned")
	public void no_customer_id_will_be_returned() {
		assertEquals("",tokenValidationResponse.getCustomerId());
	}


}
