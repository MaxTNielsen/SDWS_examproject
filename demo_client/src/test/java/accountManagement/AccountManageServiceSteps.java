package accountManagement;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountManageServiceSteps {
    String customerCPR;
    String customerfname;
    String customerlname;
    String customerBankAccountID;
    String customerID;
    String merchantID;

    boolean success = false;

    BigDecimal balance;

    List<String> bankAccounts = new ArrayList<>();

    AccountManageService service = new AccountManageService();


    @Given("the customer with ID {string} has a bank account")
    public void customerHasID(String customerID) {
        this.customerID = customerID;
    }

    @Given("the merchant with ID {string} has a bank account")
    public void merchantHasID(String ID) {
        this.merchantID = ID;
    }

    @When("the customer register in DTUPay")
    public void registerCustomer() {
        service.registerCustomer(customerID);
    }

    @When("the merchant register in DTUPay")
    public void registerMerchant() {
        service.registerMerchant(merchantID);
    }

    @Then("the customer is registered in DTUPay")
    public void theCustomerIsRegistered() {
        assertTrue(service.isCustomerRegistered(customerID));
    }

    @Then("the merchant is registered in DTUPay")
    public void theMerchantIsRegistered() {
        assertTrue(service.isMerchantRegistered(merchantID));
    }

    /*NEW SCENARIO !!!!!!!!!!!!!!!!!!!!!!!!*/

    @Given("the customer with ID {string} and with the name {string} {string} and the balance {int} kr")
    public void theCustomerWithIDAndWithTheNameAndTheBalanceKr(String customerfname, String customerlname, String customerCPR, Integer int1) {
        balance = balance.valueOf(int1);
        this.customerfname = customerfname;
        this.customerlname = customerlname;
        this.customerCPR = customerCPR;
    }

    @When("the customer register in the bank")
    public void theCustomerRegisterInTheBank() {
        customerBankAccountID = service.registerClientAtBank(customerCPR, customerfname, customerlname, balance);
        bankAccounts.add(customerBankAccountID);
        service.deleteBankAccounts(bankAccounts);
    }

    @When("the customer register in DTUPay with his CPR as ID")
    public void theCustomerRegisterInDTUPayWithHisCPRAsID() {
        success = service.registerCustomer(customerCPR);
    }

    @Then("the customer has gotten an account in DTUPay")
    public void theCustomerHasGottenAnAccountInDTUPay() {
        assertTrue(success);
    }
}
