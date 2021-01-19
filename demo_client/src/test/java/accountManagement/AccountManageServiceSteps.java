package accountManagement;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.After;
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
    String merchantCPR;
    String merchantfname;
    String merchantlname;
    String merchantBankAccountID;

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

    @Given("the customer with CPR {string} and with the name {string} {string} and the balance {int} kr")
    public void theCustomerWithIDAndWithTheNameAndTheBalanceKr(String customerCPR, String customerfname, String customerlname, Integer int1) {
        balance = BigDecimal.valueOf(int1);
        this.customerCPR = customerCPR;
        this.customerfname = customerfname;
        this.customerlname = customerlname;
    }

    @When("the customer register in the bank")
    public void theCustomerRegisterInTheBank() {
        customerBankAccountID = service.registerClientAtBank(customerCPR, customerfname, customerlname, balance);
        System.out.println(service.registerClientAtBank("1232133-121231233123", "Customer12", "A", BigDecimal.valueOf(1000)));
        System.out.println(service.registerClientAtBank("1234123123112312323123", "Customer22", "b", BigDecimal.valueOf(1000)));
        System.out.println(service.registerClientAtBank("121212312311231323123", "Merchanttt", "b", BigDecimal.valueOf(1000)));
        bankAccounts.add(customerBankAccountID);
    }

    @When("the customer register in DTUPay with his CPR as ID")
    public void theCustomerRegisterInDTUPayWithHisCPRAsID() {
        //System.out.printf("Send %s to register DTUPay account", customerBankAccountID);
        success = service.registerCustomer(customerBankAccountID);
        // update customer info: cpr name
    }

    @Then("the customer has gotten an account in DTUPay")
    public void theCustomerHasGottenAnAccountInDTUPay() {
        assertTrue(success);
    }

    /*NEW SCENARIO !!!!!!!!!!!!!!!!!!!!!!*/

    @Given("the merchant with CPR {string} and with the name {string} {string} and the balance {int} kr")
    public void theMerchantWithCPRAndWithTheNameAndTheBalanceKr(String merchantCPR, String merchantfname, String merchantlname, Integer int1) {
        balance = BigDecimal.valueOf(int1);
        this.merchantCPR = merchantCPR;
        this.merchantfname = merchantfname;
        this.merchantlname = merchantlname;
    }

    @When("the merchant register in the bank")
    public void theMerchantRegisterInTheBank() {
        merchantBankAccountID = service.registerClientAtBank(merchantCPR, merchantfname, merchantlname, balance);
        bankAccounts.add(merchantBankAccountID);
    }

    @When("the mechant register in DTUPay with his CPR as ID")
    public void theMechantRegisterInDTUPayWithHisCPRAsID() {
        //System.out.printf("Send %s to register DTUPay account", merchantBankAccountID);
        success = service.registerMerchant(merchantBankAccountID);
    }

    @Then("the merchant has gotten an account in DTUPay")
    public void theMerchantHasGottenAnAccountInDTUPay() {
        assertTrue(success);
    }

/*    @After
    public void end()
    {
        service.deleteBankAccounts(bankAccounts);
    }*/
}
