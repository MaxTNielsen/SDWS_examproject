package accountManagement;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AccountManageServiceSteps {
    AccountManageService service = new AccountManageService();
    String customerID;
    String merchantID;

    @Given("the customer with ID {string} has a bank account")
    public void customerHasID(String customerID)
    {
        this.customerID = customerID;
    }

    @Given("the merchant with ID {string} has a bank account")
    public void merchantHasID(String ID)
    {
        this.merchantID = ID;
    }

    @When("the customer register in DTUPay")
    public void registerCustomer()
    {
        service.registerCustomer(customerID);
    }

    @When("the merchant register in DTUPay")
    public void registerMerchant()
    {
        service.registerMerchant(merchantID);
    }

    @Then("the customer is registered in DTUPay")
    public void theCustomerIsRegistered()
    {
        assertTrue(service.isCustomerRegistered(customerID));
    }

    @Then("the merchant is registered in DTUPay")
    public void theMerchantIsRegistered()
    {
        assertTrue(service.isMerchantRegistered(merchantID));
    }
}
