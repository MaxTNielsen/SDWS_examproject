package reporting.model;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Simplified Transaction class which contains only that data fields, which should be in the merchant report
public class MerchantTransaction {
    private String token;
    private int amount;
    private LocalDateTime timeStamp;
    private boolean refunded;

    public MerchantTransaction() {
    }

    public MerchantTransaction(Transaction _transaction) {
        this.token = _transaction.getToken();
        this.amount = _transaction.getAmount();
        this.timeStamp = _transaction.getTimeStamp();
        this.refunded = _transaction.isRefunded();
    }

    public String toString() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return new String("Time: " + timeStamp.format(dateFormatter) + " Token: " + token + " Amount: " + amount + " Refunded: " + refunded);
    }
}
