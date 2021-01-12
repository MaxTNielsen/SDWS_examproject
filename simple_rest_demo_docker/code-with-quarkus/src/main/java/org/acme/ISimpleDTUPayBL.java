package org.acme;

import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.User;

import java.math.BigDecimal;
import java.util.List;

public interface ISimpleDTUPayBL {
    boolean makeTransaction(Transaction t);

    String getCustomer(int id);

    void registerCustomer(Customer c);

    void registerMerchant(Merchant m);

    List<Transaction> getAllTransactions(String cid);

    public void registerBankAccount(String fname, String sname, String cpr, BigDecimal balance) throws BankServiceException_Exception;

    public BigDecimal getBalance(String accountID);

    public boolean checkIfCustomerHasABankAccount(String accountID);
}
