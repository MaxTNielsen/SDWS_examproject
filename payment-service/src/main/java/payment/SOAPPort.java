package payment;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;

import java.math.BigDecimal;

public class SOAPPort {
    BankService bank = new BankServiceService().getBankServicePort();

    public void TransferMoney(Transaction t) {
        try {
            bank.transferMoneyFromTo(t.getCustomId(), t.getMerchId(), BigDecimal.valueOf(t.getAmount()), "Payment");
            t.setApproved(true);
        } catch (BankServiceException_Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public BigDecimal getBalance(String id) {
        try {
            return bank.getAccount(id).getBalance();
        } catch (BankServiceException_Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new BigDecimal(0);
        }
    }

}
