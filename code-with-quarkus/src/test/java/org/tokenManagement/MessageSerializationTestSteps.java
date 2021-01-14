package org.tokenManagement;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.tokenManagement.model.TokenServiceRequestMessage;
import org.tokenManagement.model.TokenServiceResponseMessage;
import org.tokenManagement.service.TokenManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MessageSerializationTestSteps {
    TokenManager manager = TokenManager.getInstance();
    TokenServiceRequestMessage requestMessage = null;
    TokenServiceResponseMessage responseMessage = null;

    byte[] serializedRequestMessage;
    byte[] serializedResponseMessage;

    @Given("A TokenServiceRequestMessage class instance")
    public void a_token_service_request_message_class_instance() {
        requestMessage = new TokenServiceRequestMessage(TokenServiceRequestMessage.tokenServiceRequestMessageType.GET_ALL_TOKENS);
        requestMessage.setToken("TOKENID-123456789");
        requestMessage.setUserId("USERCPR-+987654321");
    }

    @When("Serialize TokenServiceRequestMessage to byte[] and deserialize it")
    public void serialize_token_service_request_message_to_byte_and_deserialize_it() throws JsonProcessingException, UnsupportedEncodingException {
        serializedRequestMessage = requestMessage.toJson().getBytes( "UTF-8");
    }

    @Then("The original TokenServiceRequestMessage class and the newly created one should be equal")
    public void the_original_token_service_request_message_class_and_the_newly_created_one_should_be_equal() throws IOException {
        TokenServiceRequestMessage deserilaizedRequestMessage = new TokenServiceRequestMessage(new String(serializedRequestMessage, "UTF-8"));
        assertEquals(true,deserilaizedRequestMessage.equals(requestMessage));
        assertEquals(true,true);
    }

    @Given("A TokenServiceResponseMessage class instance")
    public void a_token_service_response_message_class_instance() {
        responseMessage = new TokenServiceResponseMessage(TokenServiceResponseMessage.tokenServiceResponseMessageType.RESPONSE_GET_ALL_TOKENS);
    }

    @Given("{int} newly created tokenID put into TokenServiceResponseMessage instance")
    public void newly_created_token_id_put_into_it(Integer int1) {
        ArrayList<String> tokens = new ArrayList<>();
        for(int i = 0; i < int1; i++)
        {
            tokens.add(manager.generateToken("USERCPR-123456789"));
        }
        responseMessage.addTokens(tokens);
    }

    @When("Serialize TokenServiceResponseMessage to byte[] and deserialize it")
    public void serialize_token_service_response_message_to_byte_and_deserialize_it() throws JsonProcessingException, UnsupportedEncodingException {
        serializedResponseMessage = responseMessage.toJson().getBytes( "UTF-8");
    }

    @Then("The original TokenServiceResponseMessage class and the newly created one should be equal")
    public void the_original_token_service_response_message_class_and_the_newly_created_one_should_be_equal() throws IOException {
        TokenServiceResponseMessage deserializedResponseMessage = new TokenServiceResponseMessage(new String(serializedResponseMessage, "UTF-8"));
        assertEquals(true,deserializedResponseMessage.equals(responseMessage));
    }

}
