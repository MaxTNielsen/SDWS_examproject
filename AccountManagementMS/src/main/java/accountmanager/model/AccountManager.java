package accountmanager.model;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;

import accountmanager.client.ClientFactory;
import accountmanager.client.Customer;
import accountmanager.client.Merchant;
import accountmanager.controller.AccountEventController;

import java.util.HashMap;
import java.util.Map;
// @QuarkusMain
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
        customers.put("ffee1e7e-6a99-461f-a8a4-213c07ff05fc", ClientFactory.buildCustomer("ffee1e7e-6a99-461f-a8a4-213c07ff05fc"));
        customers.put("c70d9b1b-08a2-4149-a1cb-179dc4445603", ClientFactory.buildCustomer("c70d9b1b-08a2-4149-a1cb-179dc4445603"));

        merchants.put("10dfe4da-667c-4d79-99b0-a2fab97a5ec1", ClientFactory.buildMerchant("10dfe4da-667c-4d79-99b0-a2fab97a5ec1"));
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


    public Merchant getMerchant(String ID)
    {
        if (hasMerchant(ID))
            return merchants.get(ID);
        return null;
    }

    public Customer getCustomer(String ID)
    {
        if (hasCustomer(ID))
            return customers.get(ID);
        return null;
    }
}
