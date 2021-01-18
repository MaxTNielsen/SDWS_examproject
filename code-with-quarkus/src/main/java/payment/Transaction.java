package payment;

import java.time.LocalDateTime;

public class Transaction {

    private String token;
    private String merchId;
    private String customId;
    private int amount;
    private boolean approved = false;
    //private LocalDateTime timeStamp;
    private boolean refunded;

    public Transaction(){

    }

//    public Transaction(String merchId, String customId, int amount) {
//        this.merchId = merchId;
//        this.customId = customId;
//        this.amount = amount;
//        this.timeStamp= LocalDateTime.now();
//        this.refunded = false;
//    }

    public Transaction(String token, String merchId, int amount) {
        this.token = token;
        this.merchId = merchId;
        this.amount = amount;
        //this.timeStamp= LocalDateTime.now();
        this.refunded = false;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMerchId() {
        return merchId;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
    public void setMerchId(String merchId) {
        this.merchId = merchId;
    }

    public String getCustomId() {
        return customId;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

   // public LocalDateTime getTimeStamp(){return this.timeStamp;}

    public boolean isRefunded(){return this.refunded;}

    public void setToRefunded(){this.refunded = true;}
}
