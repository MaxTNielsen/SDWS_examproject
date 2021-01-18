package org.accountmanager.model;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;

import org.accountmanager.client.ClientFactory;
import org.accountmanager.client.Customer;
import org.accountmanager.client.Merchant;
import org.accountmanager.controller.AccountEventController;

import java.util.HashMap;
import java.util.Map;

public class AccountManager implements IAccountManager {
    static AccountManager instance;
    BankService bank = new BankServiceService().getBankServicePort();
    Map<String, Customer> customers = new HashMap<>();
    Map<String, Merchant> merchants = new HashMap<>();

    public static AccountManager getInstance() {
        if (instance == null)
            instance = new AccountManager();
        return instance;
    }

    public AccountManager() {
        customers.put("038b98b6-2711-461d-83ca-7e3d10acd158", ClientFactory.buildCustomer("038b98b6-2711-461d-83ca-7e3d10acd158"));
        customers.put("d7d42ca5-3923-4def-a7d0-4be6d9622764", ClientFactory.buildCustomer("d7d42ca5-3923-4def-a7d0-4be6d9622764"));

        merchants.put("06ab138a-3c61-42d9-bbfe-1543d46aa58c", ClientFactory.buildMerchant("06ab138a-3c61-42d9-bbfe-1543d46aa58c"));
        AccountEventController.listenToEverything();
    }

    public Map<String, Customer> getCustomers() {
        return customers;
    }

    public boolean registerCustomer(Customer c) {
        if (customers.containsKey(c.ID))
            return false;

        if (!checkIfClientHasABankAccount(c.ID))
            return false;
        customers.put(c.ID, c);
        return true;
    }

    public boolean registerMerchant(Merchant m) {
        if (merchants.containsKey(m.ID))
            return false;

        if (!checkIfClientHasABankAccount(m.ID))
            return false;
        merchants.put(m.ID, m);
        return true;
    }

    public boolean checkIfClientHasABankAccount(String ID) {
        try {
            bank.getAccount(ID);
            return true;
        } catch (BankServiceException_Exception e) {
            System.out.println("Error message in bankregistration");
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean hasCustomer(String ID) {
        return customers.containsKey(ID);
    }

    public boolean hasMerchant(String ID) {
        return merchants.containsKey(ID);
    }
}
