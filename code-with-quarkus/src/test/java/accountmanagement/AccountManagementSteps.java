package accountmanagement;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import accountmanager.client.ClientFactory;
import accountmanager.model.AccountManager;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import dtu.ws.fastmoney.User;
import io.cucumber.java.After;
import io.cucumber.java.en.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccountManagementSteps {

    BankService bankService = new BankServiceService().getBankServicePort();
    AccountManager manager = AccountManager.getInstance();
    String customerID;
    String merchantID;
    String accountID;
    List<String> bankAccounts = new ArrayList<>();

    @Given("A random bank account with CPR {string} and name {string} {string}")
    public void aRandomBankAccountWithCPRAndName(String cprNo, String firstName, String lastName) {
        int money = 1000;
        BigDecimal balance = BigDecimal.valueOf(money);
        User user = new User();
        String randomCPR = UUID.randomUUID().toString();
        user.setCprNumber(randomCPR);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        try {
            accountID = bankService.createAccountWithBalance(user, balance);
            bankAccounts.add(accountID);
        } catch (BankServiceException_Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Given("the customer has a bank account")
    public void theCustomerHasABankAccount() {
        customerID = accountID;
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

    @Given("the merchant has a bank account")
    public void theMerchantHasABankAccount() {
        merchantID = accountID;
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

    @After
    public void end() {
        for (String account : bankAccounts) {
            try {
                bankService.retireAccount(account);
            } catch (BankServiceException_Exception e) {
                e.printStackTrace();
            }
        }
    }

}
