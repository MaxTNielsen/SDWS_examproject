package org.accountmanager.client;

public class Merchant extends User implements Client {
    public Merchant(String ID)
    {
        super(ID);
    }

    public Merchant(String cprNo, String ID) {
        super(cprNo, ID);
    }

    public Merchant(String cprNo, String firstName, String lastName, String ID) {
        super(cprNo, firstName, lastName, ID);
    }
}
