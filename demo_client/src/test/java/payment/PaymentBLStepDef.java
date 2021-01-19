package payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import Client.Customer;
import Client.Merchant;

import TokenManagement.TokenManagementService;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import dtu.ws.fastmoney.User;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PaymentBLStepDef {
    String cid, mid, token;
    Customer customer;
    Merchant merchant;
    int amount, value, valueMerchant;
    BigDecimal balance;
    TokenManagementService tokenService = new TokenManagementService();
    PaymentService payment = new PaymentService();
    BankService bank = new BankServiceService().getBankServicePort();
    boolean successful, unsuccessful;

    @Given("the customer {string} {string} with CPR {string} has a bank account with balance {int}")
    public void theCustomerWithCPRHasABankAccountWithBalance(String name, String surname, String CPR, Integer balance) {
        this.balance = BigDecimal.valueOf(balance);
        User m = new User();
        m.setFirstName(name);
        m.setLastName(surname);
        m.setCprNumber(CPR);
        try {
        cid = bank.createAccountWithBalance(m, this.balance);
        payment.userList.add(cid);// cid
        } catch (BankServiceException_Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Given("the customer is registered with DTUPay")
    public void the_customer_is_registered_with_dtu_pay() {
        payment.registerCustomer(cid);
        customer = new Customer(cid);
    }

    @Given("the customer has a valid tokens")
    public void the_customer_has_a_valid_tokens() {
        token = tokenService.getValidToken(cid);
    }

    @Given("the merchant {string} {string} with CPR {string} has a bank account with balance {int}")
    public void theMerchantWithCPRHasABankAccountWithBalance(String name, String surname, String CPR, Integer balance) {
        this.balance = BigDecimal.valueOf(balance);
        User m = new User();
        m.setFirstName(name);
        m.setLastName(surname);
        m.setCprNumber(CPR);
        try {
        mid = bank.createAccountWithBalance(m, this.balance);
        payment.userList.add(mid); //mid
        } catch (BankServiceException_Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Given("the merchant is registered with DTUPay")
    public void theMerchantIsRegisteredWithDTUPay() {
        payment.registerMerchant(mid);
        merchant = new Merchant(mid);
    }

    @Given("And the customer has a valid tokens")
    public void theCustomerHasTokens() {
        token = tokenService.getValidToken(cid);

    }

    @When("the merchant initiates a payment for {int} kr by the customer")
    public void theMerchantInitiatesAPaymentForKrByTheCustomer(int amount) {
        this.amount = amount;
        valueMerchant = payment.getBalance(payment.userList.get(1)).intValue();

        value = payment.getBalance(payment.userList.get(0)).intValue();

        successful = payment.pay(token, mid, amount);

        valueMerchant = payment.getBalance(payment.userList.get(1)).intValue();
        value = payment.getBalance(payment.userList.get(0)).intValue();
    }

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        assertTrue(successful);
    }

    @Then("the payment is unsuccessful")
    public void thePaymentIsUnsuccessful() {
        assertFalse(successful);
    }

    @Then("the balance of the customer in the bank is {int}")
    public void theBalanceOfTheCustomerInTheBankIs(int int1) {
        assertEquals(value, int1);
    }

    @Then("the balance of the merchant in the bank is {int}")
    public void theBalanceOfTheMerchantInTheBankIs(int int1) {
        assertEquals(valueMerchant, int1);
    }

    @After
    public void removeUsers() {
        payment.retireUsers();
    }

}
