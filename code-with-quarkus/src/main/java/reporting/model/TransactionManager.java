package reporting.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class TransactionManager {

    private HashMap<String, Transaction> transactions; //all transactions: TokenID -> Transaction
    private HashMap<String, ArrayList<String>> customerTransactionHistory; //UserID -> List of the tokenID-s of its transactions
    private HashMap<String, ArrayList<String>> merchantTransactionHistory; //UserID -> List of the tokenID-s of its transactions
    private static TransactionManager instance = null;

    private TransactionManager()
    {
        transactions = new HashMap<>();
        customerTransactionHistory = new HashMap<>();
        merchantTransactionHistory = new HashMap<>();
    }

    public static TransactionManager getInstance()
    {
        if(instance == null)
        {
            instance = new TransactionManager();
        }
        return instance;
    }

    public void addTransaction(Transaction _transaction)
    {
        // register _transaction to the Transaction collection
        transactions.put(_transaction.getToken(),_transaction);

        // add transaction for the Consumer
        if(!customerTransactionHistory.containsKey(_transaction.getCustomId()))
        {
            ArrayList<String> newTokenList = new ArrayList<String>();
            newTokenList.add(_transaction.getToken());
            customerTransactionHistory.put(_transaction.getCustomId(),newTokenList);
        }
        else
        {
            customerTransactionHistory.get(_transaction.getCustomId()).add(_transaction.getToken());
        }

        // add transaction for the Merchant
        if(!merchantTransactionHistory.containsKey(_transaction.getMerchId()))
        {
            ArrayList<String> newTokenList = new ArrayList<String>();
            newTokenList.add(_transaction.getToken());
            merchantTransactionHistory.put(_transaction.getMerchId(),newTokenList);
        }
        else
        {
            merchantTransactionHistory.get(_transaction.getMerchId()).add(_transaction.getToken());
        }
    }

    public ArrayList<Transaction> managerReport()
    {
        return (new ArrayList<Transaction>(transactions.values()));
    }

    public int managerMoneyFlowReport()
    {
        int moneyFlow = 0;
        for (HashMap.Entry<String,Transaction> entry : transactions.entrySet())
        {
            if(!entry.getValue().isRefunded())
            {
                moneyFlow += entry.getValue().getAmount();
            }
        }
        return moneyFlow;
    }

    public ArrayList<CustomerTransaction> customerReport(String _customerId, LocalDateTime _periodStart, LocalDateTime _periodEnd)
    {
        ArrayList<String> tokenIDs = customerTransactionHistory.get(_customerId);
        ArrayList<CustomerTransaction> transactionList = new ArrayList<>();
        for(String currentTokenID:tokenIDs)
        {
            if(transactions.get(currentTokenID).getTimeStamp().isAfter(_periodStart) &&
                    transactions.get(currentTokenID).getTimeStamp().isBefore(_periodEnd))
            {
                transactionList.add(new CustomerTransaction(transactions.get(currentTokenID)));
            }
        }
        return transactionList;
    }

    public ArrayList<MerchantTransaction> merchantReport(String _merchantId, LocalDateTime _periodStart, LocalDateTime _periodEnd)
    {
        ArrayList<String> tokenIDs = merchantTransactionHistory.get(_merchantId);
        ArrayList<MerchantTransaction> transactionList = new ArrayList<>();
        for(String currentTokenID:tokenIDs)
        {
            if(transactions.get(currentTokenID).getTimeStamp().isAfter(_periodStart) &&
                    transactions.get(currentTokenID).getTimeStamp().isBefore(_periodEnd))
            {
                transactionList.add(new MerchantTransaction(transactions.get(currentTokenID)));
            }
        }
        return transactionList;
    }

    public void refundTransaction(String _tokenID)
    {
        if(transactions.containsKey(_tokenID))
        {
            transactions.get(_tokenID).setToRefunded();
        }
    }
}
