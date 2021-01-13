package org.accountmanager.client;

public class ClientFactory {
    public static Client getClients(String identifier, String id) {
        switch (identifier) {
            case "M":
                return new Merchant(id);
            case "C":
                return new Customer(id);
        }
        return null;
    }
}
