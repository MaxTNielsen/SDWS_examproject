package payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
    int amount;
    BigDecimal balance;
    Exception exception;
    TokenManagementService tokens = new TokenManagementService();
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
            payment.userList.add(cid);
        } catch (BankServiceException_Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Given("the customer is registered with DTUPay")
    public void theCustomerIsRegisteredWithDTUPay() {
        customer = new Customer(cid);
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
            payment.userList.add(mid);
        } catch (BankServiceException_Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Given("the merchant is registered with DTUPay")
    public void theMerchantIsRegisteredWithDTUPay() {
        merchant = new Merchant(mid);
    }

    @Given("the customer has {int} tokens")
    public void theCustomerHasTokens(Integer int1) {
    }

    @When("the merchant initiates a payment for {int} kr by the customer")
    public void theMerchantInitiatesAPaymentForKrByTheCustomer(int amount) {
        this.amount = amount;
        successful = payment.pay(merchant.ID, customer.ID, amount);
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
    public void theBalanceOfTheCustomerInTheBankIs(Integer int1) {
        assertEquals(990, payment.getBalance(payment.userList.get(0)).intValue());
    }

    @Then("the balance of the merchant in the bank is {int}")
    public void theBalanceOfTheMerchantInTheBankIs(Integer int1) {
        assertEquals(2010, payment.getBalance(payment.userList.get(1)).intValue());
    }

    @After
    public void removeUsers() {
        payment.retireUsers();
    }

}
