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
        customers.put("0aa0383e-df5a-4afb-85f7-0dd5d33dfb77", ClientFactory.buildCustomer("0aa0383e-df5a-4afb-85f7-0dd5d33dfb77"));
        customers.put("cfb38983-1e05-4200-a7dc-86948f405de6", ClientFactory.buildCustomer("cfb38983-1e05-4200-a7dc-86948f405de6"));

        merchants.put("fe9a206c-a32c-42da-880b-8f051cec6d29", ClientFactory.buildMerchant("fe9a206c-a32c-42da-880b-8f051cec6d29"));
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
