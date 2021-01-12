package com.simpledtupay;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.After;

import javax.ws.rs.WebApplicationException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SimpleDTUPaySteps {
    String cid, mid, accountID;
    int amount;
    BigDecimal balance;
    Exception exception;
    List<Transaction> t;
    List<String> accountIDs = new ArrayList<>();
    SimpleDTUPay dtuPayClient = new SimpleDTUPay();
    boolean successful, unsuccessful;

    @Given("a customer with id {string}")
    public void aCustomerWithId(String cid) {
        this.cid = cid;
        dtuPayClient.registerCustomer(cid, "customer1");
    }

    @Given("a merchant with id {string}")
    public void aMerchantWithId(String mid) {
        this.mid = mid;
        dtuPayClient.registerMerchant(mid, "merchant1");
    }

    @When("the merchant initiates a payment for {int} kr by the customer")
    public void theMerchantInitiatesAPaymentForKrByTheCustomer(int amount) {
        this.amount = amount;
        this.successful = dtuPayClient.pay(mid, cid, amount);
    }

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        assertTrue(successful);
    }

    /*NEW SCENARIO !!!!!!!!!!!!!!!!!!!!!!!!!*/

    @Given("a successful payment of {int} kr from customer {string} to merchant {string}")
    public void aSuccessfulPaymentOfKrFromCustomerToMerchant(int amount, String cid, String mid) {
        this.cid = cid;

    }

    @When("the manager asks for a list of transactions")
    public void theManagerAsksForAListOfTransactions() {
        this.t = dtuPayClient.transactions(cid);
    }

    @Then("the list contains a transaction where customer {string} paid {int} kr to merchant {string}")
    public void theListContainsATransactionWhereCustomerPaidKrToMerchant(String cid, int amount, String mid) {
        assertNotNull(t);
    }

    /*NEW SCENARIO !!!!!!!!!!!!!!!!!!!!!!!!!*/

    @Given("a new customer with id {string}")
    public void aNewCustomerWithId(String cid) {
        this.cid = cid;
    }

    @Given("the same merchant with id {string}")
    public void theSameMerchantWithId(String mid) {
        this.mid = mid;
    }

    @When("the merchant initiates a new payment for {int} kr by the customer")
    public void theMerchantMakesANewTransaction(int amount) {
        this.amount = amount;
        try {
            this.unsuccessful = dtuPayClient.pay(mid, cid, amount);
        } catch (WebApplicationException e) {
            this.exception = e;
        }
    }

    @Then("the payment is not successful")
    public void thePaymentIsNotSuccessful() {
        assertFalse(successful);
    }

    @Then("a error message is returned saying customer not know")
    public void aErrorMessageIsReturnedSayingCustomerNotKnow() {
        assertNotNull(exception);
    }

    /*NEW SCENARIO - SOAP TASK !!!!!!!!!!!!!!!!!!!!!!!!!*/

    @Given("the customer {string} {string} with CPR {string} has a bank account")
    public void theCustomerWithCPRHasABankAccount(String string, String string2, String string3) {
        this.accountID = dtuPayClient.registerBankAccount(string, string2, string3);
        this.accountIDs.add(accountID);
        //dtuPayClient.deleteAccounts(accountIDs);
        //throw new io.cucumber.java.PendingException();
    }

    @Given("the balance of that account is {int}")
    public void theBalanceOfThatAccountIs(Integer int1) {
        this.balance = BigDecimal.valueOf(int1);
        //assertEquals(dtuPayClient.getBalance(accountID), balance);
        throw new io.cucumber.java.PendingException();
    }

    @Given("the customer is registered with DTUPay")
    public void theCustomerIsRegisteredWithDTUPay() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("the merchant {string} {string} with CPR number {string} has a bank account")
    public void theMerchantWithCPRNumberHasABankAccount(String string, String string2, String string3) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("the balance of his account is {int}")
    public void theBalanceOfHisAccountIs(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("the merchant is registered with DTUPay")
    public void theMerchantIsRegisteredWithDTUPay() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("the merchant initiates a payment for {string} kr by the customer")
    public void theMerchantInitiatesAPaymentForKrByTheCustomer(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("the balance of the customer at the bank is {int} kr")
    public void theBalanceOfTheCustomerAtTheBankIsKr(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("the balance of the merchant at the bank is {int} kr")
    public void theBalanceOfTheMerchantAtTheBankIsKr(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @After
    public void deleteLists() {
        dtuPayClient.deleteAccounts(accountIDs);
    }
}