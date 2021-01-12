package org.acme;

import dtu.ws.fastmoney.*;


import javax.ws.rs.NotFoundException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleDTUPayBL implements ISimpleDTUPayBL {

    static SimpleDTUPayBL instance_ = new SimpleDTUPayBL();
    BankService bank = new BankServiceService().getBankServicePort();

    private Map<String, Customer> customerMap = new HashMap<>();
    private Map<String, Merchant> merchantMap = new HashMap<>();

    private SimpleDTUPayBL() {
    }

    public static SimpleDTUPayBL getInstance_() {
        return instance_;
    }

    public Map<String, Customer> getCustomerMap() {
        return customerMap;
    }

    @Override
    public String getCustomer(int id) throws NotFoundException {
        return "ID: " + id + " Name: " + customerMap.get(String.valueOf(id)).getName();
    }

    @Override
    public void registerCustomer(Customer c) {
        if (!customerMap.containsKey(String.valueOf(c.getId()))) {
            customerMap.put(String.valueOf(c.getId()), c);
        }
    }

    @Override
    public void registerMerchant(Merchant m) {
        if (!merchantMap.containsKey(String.valueOf(m.getId()))) {
            merchantMap.put(String.valueOf(m.getId()), m);
        }
    }

    @Override
    public List<Transaction> getAllTransactions(String cid) throws NotFoundException {
        return customerMap.get(cid).getTransactions();
    }

    @Override
    public boolean makeTransaction(Transaction t) {
        if (customerMap.containsKey(t.getCustomId()) && merchantMap.containsKey(t.getMerchId())) {
            t.setApproved(true);
            customerMap.get(t.getCustomId()).addTransaction(t);
            return t.isApproved();
        }
        return t.isApproved();
    }

    @Override
    public void registerBankAccount(String fname, String sname, String cpr, BigDecimal balance) {
        User u = new User();
        u.setFirstName(fname);
        u.setLastName(sname);
        u.setCprNumber(cpr);
        BigDecimal bigDecimal = new BigDecimal("1000");
        try {
            bank.createAccountWithBalance(u, bigDecimal);
        } catch (BankServiceException_Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public BigDecimal getBalance(String accountID){
        try{
            return bank.getAccount(accountID).getBalance();
        } catch (BankServiceException_Exception e){
            System.out.println(e.getMessage());}
        return null;
    }

    @Override
    public boolean checkIfCustomerHasABankAccount(String accountID) {
        //bank.
        return false;
    }


}