package TokenManagement;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.assertEquals;
import javax.ws.rs.core.Response;


public class TokenManagementServiceSteps {
    String cprNumber;
    Response response;
    TokenManagementService service = new TokenManagementService();
    @Given("A customer with cprNumber {string}")
    public void a_customer_with_cpr_number(String string) {
        this.cprNumber=string;
    }

    @When("the customer requests to generate a new token")
    public void the_customer_requests_to_generate_a_new_token() {
        response = service.generateToken(cprNumber);
    }

    @Then("a token is created")
    public void a_token_is_created() {
        assertEquals(200,response.getStatus());
    }


}
