package payment;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class PaymentBL {
	
	SOAPPort soap = new SOAPPort();
	MessageParsingPort msg = new MessageParsingPort();
	static PaymentBL instance;

	private PaymentBL(){
	}

	public static PaymentBL getInstance(){
		if(instance == null)
			return new PaymentBL();
		return instance;
	}

	public boolean makeTransaction(Transaction t) {
		if (t.getAmount() > 0) {
			soap.TransferMoney(t);
			if (t.isApproved()) return true;
			else return false;
		}
		return false;
	}
}
