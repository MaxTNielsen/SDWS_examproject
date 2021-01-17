package payment;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;


public class PaymentBL {
	
	SOAPPort soap = new SOAPPort();
	MessageParsingPort msg = new MessageParsingPort();
	
	
	
	public void makeTransaction( Transaction t) {
		if (t.getAmount() > 0) {
			soap.TransferMoney(t);
		}
	}
	
	
	public boolean paymentRequest(Transaction t) throws TimeoutException {
		TokenValidationRequest requestMessage = new TokenValidationRequest(t.getToken());
		requestMessage.setToken(t.getToken());
		Event ev = new Event("TOKEN_VALIDATION_REQUEST", new Object[] { requestMessage });
		Gson gson = new Gson();
		String requestString = gson.toJson(ev);
		String response = msg.requestTokenValidation(requestString);
		
	    Event responseEvent = gson.fromJson(response ,Event.class);
	    String jsonString3 = gson.toJson(responseEvent.getArguments()[0]);
        TokenValidationResponse tokenResponse = gson.fromJson(jsonString3,TokenValidationResponse.class);
        if (tokenResponse.isValid()) {
        	t.setCustomId(tokenResponse.getCustomerId());
        	makeTransaction(t);
        	if (t.isApproved()) return true;
        	else return false;
        }
        else return false;
	}

	

}
