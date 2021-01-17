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
	String response;
	public TokenManagerSteps() {
		tokenManager = new TokenManager();
	}

	@When("I receive TOKEN_GENERATION_REQUEST")
	public void i_receive_token_generation_request() throws Exception {
		TokenGenerationRequest request = new TokenGenerationRequest("customerid",1);
		Event event = new Event("TOKEN_GENERATION_REQUEST", new Object[] { request });

		//convert to string
		Gson gson = new Gson();
		String request_string = gson.toJson(event);
		response = tokenManager.receiveEvent(request_string);

	}

	@Then("I have sent TOKEN_GENERATION_RESPONSE")
	public void i_have_sent_token_generation_response() {

		//convert to Event
		Gson gson = new Gson();
		Event event = gson.fromJson(response,Event.class);
		assertTrue("TOKEN_GENERATION_RESPONSE".equals(event.getEventType()));
	}

	@When("I recieve TOKEN_VALIDATION_REQUEST")
	public void i_recieve_token_validation_request() throws Exception {
		TokenValidationRequest request = new TokenValidationRequest("123");
		Event event = new Event("TOKEN_VALIDATION_REQUEST", new Object[] { request });
		//convert to string
		Gson gson = new Gson();
		String request_string = gson.toJson(event);
		response = tokenManager.receiveEvent(request_string);
	}

	@Then("I have sent TOKEN_VALIDATION_RESPONSE")
	public void i_have_sent_token_validation_response() {
		//convert to Event
		Gson gson = new Gson();
		Event event = gson.fromJson(response,Event.class);

		assertTrue("TOKEN_VALIDATION_RESPONSE".equals(event.getEventType()));
	}



}
