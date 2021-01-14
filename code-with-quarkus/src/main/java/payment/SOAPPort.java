package payment;

import java.math.BigDecimal;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;

public class SOAPPort {
	BankService bank = new BankServiceService().getBankServicePort();
	
	public boolean TransferMoney(String cid, String mid, int amount) {
		try {
			bank.transferMoneyFromTo(cid, mid, BigDecimal.valueOf(amount), "Payment");
			return true;
		} catch (BankServiceException_Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	
	

}
