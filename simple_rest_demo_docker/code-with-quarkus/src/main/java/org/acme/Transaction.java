package org.acme;

public class Transaction {
    private String merchId;
    private String customId;
    private int amount;
    private boolean approved = false;

    public Transaction(){

    }

    public Transaction(String merchId, String customId, int amount) {
        this.merchId = merchId;
        this.customId = customId;
        this.amount = amount;
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
}
