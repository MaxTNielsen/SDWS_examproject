package payment;

import org.tokenManagement.service.TokenManager;

public class PaymentBL {
	
	SOAPPort soap = new SOAPPort();
	static PaymentBL instance;

	public PaymentBL(){
		MessageParsingPort.listenForPayment();
	}

	public static PaymentBL getInstance() {
		if (instance == null)
			instance = new PaymentBL();
		return instance;
	}

	public boolean makeTransaction(Transaction t) {
		System.out.println("[PaymentBL] Transaction=" + t.toString());
		if (t.getAmount() > 0) {
			soap.TransferMoney(t);
			if (t.isApproved()) return true;
			else return false;
		}
		return false;

	}
}
