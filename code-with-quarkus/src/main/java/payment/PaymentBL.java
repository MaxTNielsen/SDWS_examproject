package payment;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;


public class PaymentBL {
	
	SOAPPort soap = new SOAPPort();
	MessageParsingPort msg = new MessageParsingPort();
	
	public PaymentBL(){
		try {
			msg.listen();
			//msg.requestTokenValidation(null);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
