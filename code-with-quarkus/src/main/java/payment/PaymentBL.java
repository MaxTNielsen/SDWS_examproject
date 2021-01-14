package payment;

public class PaymentBL {
	RESTPort rest = new RESTPort();
	SOAPPort soap = new SOAPPort();
	
	
	

	public boolean makeTransaction(Transaction t) {
		if (t.getAmount() > 0) {
		return (soap.TransferMoney(t.getCustomId(), t.getMerchId(), t.getAmount()));
		}
		else return false;
	}

}
