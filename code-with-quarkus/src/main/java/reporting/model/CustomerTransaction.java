package reporting.model;

import org.dtupay.Transaction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Simplified Transaction class which contains only that data fields, which should be in the customer report
public class CustomerTransaction {
    private String token;
    private String merchantName;
    private int amount;
    private LocalDateTime timeStamp;
    private boolean refunded;

    public CustomerTransaction()
    {

    }

    public CustomerTransaction(Transaction _transaction)
    {
        this.token = _transaction.getToken();
        // TODO this should change to get the Merchant name from ClientManager
        this.merchantName = _transaction.getMerchId();
        this.amount = _transaction.getAmount();
        this.timeStamp = _transaction.getTimeStamp();
        this.refunded = _transaction.isRefunded();
    }

    public String toString()
    {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return new String("Time: " + timeStamp.format(dateFormatter) +" Token: " + token + " Merchant: " + merchantName + " Amount: " + amount + " Refunded: " + refunded);
    }
}
