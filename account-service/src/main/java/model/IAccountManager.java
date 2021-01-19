package model;


import client.Customer;
import client.Merchant;

public interface IAccountManager {
    public boolean registerCustomer(Customer c);
    public boolean registerMerchant(Merchant m);
    public boolean hasCustomer(String ID);
    public boolean hasMerchant(String ID);
    public boolean checkIfClientHasABankAccount(String CPR);
}
