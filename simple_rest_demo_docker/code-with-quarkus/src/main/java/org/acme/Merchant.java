package org.acme;

public class Merchant extends Clients {

    public Merchant() {

    }

    public Merchant(String id, String name) {
        super(id, name);
    }

    public Merchant(String id, String name, String CPR) {
        super(id, name, CPR);
    }
}
