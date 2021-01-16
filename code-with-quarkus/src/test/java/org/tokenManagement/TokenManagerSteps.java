package org.tokenManagement;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.tokenManagement.messaging.Event;
import org.tokenManagement.messaging.EventSender;
import org.tokenManagement.messaging.TokenGenerationRequest;
import org.tokenManagement.messaging.TokenValidationRequest;
import org.tokenManagement.service.TokenManager;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class TokenManagerSteps {
	TokenManager tokenManager;
	Event event;

	public TokenManagerSteps() {
		tokenManager = new TokenManager(new EventSender() {

			@Override
			public void sendEvent(Event ev) throws Exception {
				event = ev;
			}

		});
	}

	@When("I receive TOKEN_GENERATION_REQUEST")
	public void i_receive_token_generation_request() throws Exception {
		TokenGenerationRequest request = new TokenGenerationRequest();
		Event event = new Event("TOKEN_GENERATION_REQUEST", new Object[] { request });
		tokenManager.receiveEvent(event);
	}

	@Then("I have sent TOKEN_GENERATION_RESPONSE")
	public void i_have_sent_token_generation_response() {
		assertTrue("TOKEN_GENERATION_RESPONSE".equals(event.getEventType()));
	}

	@When("I recieve TOKEN_VALIDATION_REQUEST")
	public void i_recieve_token_validation_request() throws Exception {
		TokenValidationRequest request = new TokenValidationRequest();
		Event event = new Event("TOKEN_GENERATION_REQUEST", new Object[] { request });
		tokenManager.receiveEvent(event);
	}

	@Then("I have sent TOKEN_VALIDATION_RESPONSE")
	public void i_have_sent_token_validation_response() {
		assertTrue("TOKEN_GENERATION_RESPONSE".equals(event.getEventType()));
	}



}
