package org.accountManagement;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.accountmanager.client.ClientFactory;
import org.accountmanager.model.AccountManager;

import io.cucumber.java.en.*;

public class AccountManagementSteps {

    AccountManager manager = AccountManager.getInstance();
    String customerID;
    String merchantID;

    @Given("the customer with bank ID account {string} has a bank account")
    public void theCustomerWithBankIDAccountHasABankAccount(String string) {
        customerID = string;
    }

    @When("the customer register in DTUPay")
    public void theCustomerRegisterInDTUPay() {
        manager.registerCustomer(ClientFactory.buildCustomer(customerID));
    }

    @Then("the customer is registered in DTUPay")
    public void theCustomerIsRegisteredInDTUPay() {
        boolean success = manager.hasCustomer(customerID);
        assertTrue(success);
    }

    @Given("the merchant with bank account ID {string} has a bank account")
    public void theMerchantWithBankAccountIDHasABankAccount(String string) {
        merchantID = string;
    }

    @When("the merchant register in DTUPay")
    public void theMerchantRegisterInDTUPay() {
        manager.registerMerchant(ClientFactory.buildMerchant(merchantID));
    }

    @Then("the merchant is registered in DTUPay")
    public void theMerchantIsRegisteredInDTUPay() {
        boolean success = manager.hasMerchant(merchantID);
        assertTrue(success);
    }

}
