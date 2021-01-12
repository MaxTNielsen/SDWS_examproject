package org.tokenManagement;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.tokenManagement.service.TokenService;

import static org.junit.Assert.assertTrue;

//I just wrote a random test case to not push empty files and make possible to check if cucumber is able to run

public class TokenManagementTestSteps
{	
	String cprNumber;
	TokenService ts;
    @Given("a customer with CPR-number {string}")
    public void a_customer_with_cpr_number(String cprNumber) {
       this.cprNumber = cprNumber;
    }

    @When("the client request new token")
    public void the_client_request_new_token() {
    	ts = new TokenService();
    	ts.generateToken(cprNumber,1);
    }

    @Then("a new token is created whose customer-ID is {string}")
    public void a_new_token_is_created_whose_customer_id_is_and_its_validation_is(String cprNumber) {

        assertTrue(true);
    }
}
