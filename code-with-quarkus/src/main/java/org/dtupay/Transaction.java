package org.dtupay;

import java.time.LocalDateTime;

public class Transaction {

    private String tokenID;
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

    public Transaction(String tokenID, String merchId, int amount) {
        this.tokenID = tokenID;
        this.merchId = merchId;
        this.amount = amount;
        //this.timeStamp= LocalDateTime.now();
        this.refunded = false;
    }

    public String getTokenID() {
        return tokenID;
    }

    public void setTokenID(String tokenID) {
        this.tokenID = tokenID;
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
