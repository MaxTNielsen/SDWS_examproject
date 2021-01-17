package org.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import org.dtupay.DTUPay;

import com.google.gson.Gson;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import dtu.ws.fastmoney.User;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import payment.Transaction;


public class PaymentStepDef {
	DTUPay dtuPay = new DTUPay();
	Gson gson = new Gson();
	
	String cid, mid, token;
    int amount;
    BigDecimal balance;
    BankService bank = new BankServiceService().getBankServicePort();
    CompletableFuture<Boolean> result = new CompletableFuture<Boolean>();
    boolean successful, unsuccessful;
    ArrayList<String> userList = new ArrayList<String>();
    public void retireUsers() {
    	for (int i = 0 ; i<userList.size() ; i++) {
    		try {
				bank.retireAccount(userList.get(i));
			} catch (BankServiceException_Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
	
    @Given("the customer {string} {string} with CPR {string} has a bank account with balance {int}")
    public void theCustomerWithCPRHasABankAccountWithBalance(String name, String surname, String CPR, Integer balance) {
    	this.balance = BigDecimal.valueOf(balance);
    	User m = new User();
        m.setFirstName(name);
        m.setLastName(surname);
        m.setCprNumber(CPR);
    	try {
			cid = bank.createAccountWithBalance(m, this.balance);
			userList.add(cid);
		} catch (BankServiceException_Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			userList.add(mid);
		} catch (BankServiceException_Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Given("the token {string} is valid")
    public void theTokenIsValid(String token) {
        this.token = token; 
    }

	@When("the merchant initiates a transaction for {int}")
	public void theMerchantInitiatesATransactionFor(Integer amount) {
	    Transaction t = new Transaction(token, mid, amount);
//	    new Thread(()->{
//	    	result.complete(value)
//	    }
	    String message = gson.toJson(t);
	    String result = dtuPay.forwardMQtoMicroservices(message, "payment");
	    successful = Boolean.parseBoolean(result);
	    
	    
	}

	@Then("the transaction is successful")
	public void theTransactionIsSuccessful() {
	    assertTrue(successful);
	}

	@Then("the balance of the customer is {int}")
	public void theBalanceOfTheCustomerIs(Integer int1) {
		try {
			assertEquals(int1,bank.getAccount(cid).getBalance());
		} catch (BankServiceException_Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Then("the balance of the merchant is {int}")
	public void theBalanceOfTheMerchantIs(Integer int1) {
		try {
			assertEquals(int1,bank.getAccount(mid).getBalance());
		} catch (BankServiceException_Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@When("the token {string} is invalid")
	public void theTokenIsInvalid(String token) {
	    this.token = token;
	}

	@Then("the transaction is unsuccessful")
	public void theTransactionIsUnsuccessful() {
	    assertFalse(successful);
	}
	
	@After public void removeUsers() {
		retireUsers();
	}

}
