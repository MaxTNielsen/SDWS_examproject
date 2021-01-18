package payment;

import java.math.BigDecimal;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;

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
}
