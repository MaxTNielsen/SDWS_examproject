package org.tokenManagement;

import com.google.gson.Gson;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.tokenManagement.messaging.model.Event;

import org.tokenManagement.messaging.model.TokenGenerationRequest;
import org.tokenManagement.messaging.model.TokenValidationRequest;
import org.tokenManagement.service.TokenManager;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class TokenManagerSteps {
	TokenManager tokenManager;
	Event event;
	Event response;
	public TokenManagerSteps() {
		tokenManager = new TokenManager();
	}

	@When("I receive TOKEN_GENERATION_REQUEST")
	public void i_receive_token_generation_request() throws Exception {
		TokenGenerationRequest request = new TokenGenerationRequest("customerid",1);
		Event event = new Event("TOKEN_GENERATION_REQUEST", new Object[] { request });

		response = tokenManager.receiveEvent(event);

	}

	@Then("I have sent TOKEN_GENERATION_RESPONSE")
	public void i_have_sent_token_generation_response() {

		assertTrue("TOKEN_GENERATION_RESPONSE".equals(response.getEventType()));
	}

	@When("I receive TOKEN_VALIDATION_REQUEST")
	public void i_recieve_token_validation_request() throws Exception {
		TokenValidationRequest request = new TokenValidationRequest("123");
		Event event = new Event("TOKEN_VALIDATION_REQUEST", new Object[] { request });
		response = tokenManager.receiveEvent(event);
	}

	@Then("I have sent TOKEN_VALIDATION_RESPONSE")
	public void i_have_sent_token_validation_response() {
		assertTrue("TOKEN_VALIDATION_RESPONSE".equals(response.getEventType()));
	}



}
