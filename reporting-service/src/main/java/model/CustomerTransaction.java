package model;

import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Simplified Transaction class which contains only that data fields, which should be in the customer report
public class CustomerTransaction {
    private String token;
    private String merchantName;
    private int amount;
    private LocalDateTime timeStamp;
    private boolean refunded;
    static BankService bank = new BankServiceService().getBankServicePort();

    public CustomerTransaction() {

    }

    public CustomerTransaction(Transaction _transaction) {
        this.token = _transaction.getToken();
        try {
            Account bankAccount = bank.getAccount(_transaction.getMerchId());
            this.merchantName = bankAccount.getUser().getFirstName() + bankAccount.getUser().getLastName();
        } catch (BankServiceException_Exception e) {
            this.merchantName = _transaction.getMerchId();
        }
        this.amount = _transaction.getAmount();
        this.timeStamp = _transaction.getTimeStamp();
        this.refunded = _transaction.isRefunded();
    }

    public String toString() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return new String("Time: " + timeStamp.format(dateFormatter) + " Token: " + token + " Merchant: " + merchantName + " Amount: " + amount + " Refunded: " + refunded);
    }
}
