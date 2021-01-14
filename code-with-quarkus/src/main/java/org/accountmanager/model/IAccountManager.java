package org.accountmanager.model;

import org.accountmanager.client.Customer;
import org.accountmanager.client.Merchant;

import java.util.List;

public interface IAccountManager {
    public boolean registerCustomer(Customer c);
    public boolean registerMerchant(Merchant m);
    public boolean hasCustomer(String ID);
    public boolean hasMerchant(String ID);
    public boolean checkIfClientHasABankAccount(String CPR);
}
