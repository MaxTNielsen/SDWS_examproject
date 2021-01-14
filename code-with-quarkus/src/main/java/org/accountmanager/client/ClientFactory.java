package org.accountmanager.client;

public class ClientFactory {

    public static Customer buildCustomer(String id) {
        return new Customer(id);
    }

    public static Merchant buildMerchant(String id) {
        return new Merchant(id);
    }
}
