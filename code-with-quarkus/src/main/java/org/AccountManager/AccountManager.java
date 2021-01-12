package org.AccountManager;

import com.Client.Customer;
import com.Client.Merchant;

import java.util.HashMap;
import java.util.Map;

public class AccountManager {
    static AccountManager instance;
    Map<String, Customer> customers = new HashMap<>();
    Map<String, Merchant> merchants = new HashMap<>();

    
    public static AccountManager getInstance()
    {
        if (instance == null)
            instance = new AccountManager();
        return instance;
    }

    public AccountManager()
    {
        registerCustomer(new Customer("cid1"));
        registerMerchant(new Merchant("mid1"));
    }

    public boolean registerCustomer(Customer c)
    {
        if (customers.containsKey(c.ID))
            return false;
        customers.put(c.ID, c);
        return true;
    }

    
    public boolean registerMerchant(Merchant m)
    {
        if (merchants.containsKey(m.ID))
            return false;
        merchants.put(m.ID, m);
        return true;
    }

    public boolean hasCustomer(String ID)
    {
        return customers.containsKey(ID);
    }

    public boolean hasMerchant(String ID)
    {
        return merchants.containsKey(ID);
    }
}
