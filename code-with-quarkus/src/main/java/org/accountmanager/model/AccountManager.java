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
<<<<<<< HEAD
        customers.put("6243c4c1-d7ba-4ea0-8363-141ca09c9082", ClientFactory.buildCustomer("6243c4c1-d7ba-4ea0-8363-141ca09c9082"));
        customers.put("d7d42ca5-3923-4def-a7d0-4be6d9622764", ClientFactory.buildCustomer("d7d42ca5-3923-4def-a7d0-4be6d9622764"));

        merchants.put("f7d779bc-4cc2-40c2-859a-89e5aa7361c4", ClientFactory.buildMerchant("f7d779bc-4cc2-40c2-859a-89e5aa7361c4"));
=======
        customers.put("1c78cbb5-3b2c-4f75-b325-a0895c135af2", ClientFactory.buildCustomer("1c78cbb5-3b2c-4f75-b325-a0895c135af2"));
        //customers.put("cfb38983-1e05-4200-a7dc-86948f405de6", ClientFactory.buildCustomer("cfb38983-1e05-4200-a7dc-86948f405de6"));

        merchants.put("b0908289-4524-44f3-be1e-689cd4a6d7cd", ClientFactory.buildMerchant("b0908289-4524-44f3-be1e-689cd4a6d7cd"));


>>>>>>> f4535db95ae1f985b54f8fbceb6e1d31d8685d26
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
