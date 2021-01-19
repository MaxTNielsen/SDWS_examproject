package behaviourtests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class Steps {
	Service service = new Service();
	private boolean result;
	Customer customer;
	Customer newCustomer;
	
	@Given("a customer with an empty id")
	public void aCustomerWithAnEmptyId() {
		customer = new Customer();
		customer.setName("James");
		assertTrue(customer.getId().isEmpty());
	}

	@When("I do something with the customer")
	public void iDoSomethingWithTheCustomer() {
		newCustomer = service.doSomething(customer);
	}
	
	@Then("the customer is assigned an id")
	public void theCustomerIsAssignedAnId() {
		assertNotNull(newCustomer.getId());
	}
}

