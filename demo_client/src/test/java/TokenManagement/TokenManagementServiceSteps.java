package TokenManagement;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.messages.internal.com.google.gson.Gson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.ws.rs.core.Response;


public class TokenManagementServiceSteps {
    String customerId;
    Response response;
    TokenManagementService service = new TokenManagementService();

    @Given("A customer with customerId {string}")
    public void a_customer_with_cpr_number(String customerId) {
        this.customerId = customerId;
    }

    @When("the customer requests to generate {int} new token")
    public void the_customer_requests_to_generate_new_token(Integer numberOfTokens) {
        TokenGenerationRequest request = new TokenGenerationRequest(customerId, numberOfTokens);
        Gson gson = new Gson();
        String request_string = gson.toJson(request);
        response = service.generateToken(request_string);
    }

    @Then("token is created")
    public void a_token_is_created() {
        assertEquals(200, response.getStatus());
    }

    @Then("token request fails")
    public void token_request_fails() {
        assertEquals(400, response.getStatus());
    }

    @Given("the customer already generated {int} new tokens")
    public void the_customer_already_generated_new_tokens(Integer numberOfTokens) {
        TokenGenerationRequest request = new TokenGenerationRequest(customerId, numberOfTokens);
        Gson gson = new Gson();
        String request_string = gson.toJson(request);
        response = service.generateToken(request_string);
    }

}
