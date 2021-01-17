package payment;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class PaymentBL {
    SOAPPort soap = new SOAPPort();
    MessageParsingPort msg = new MessageParsingPort();

    public void paymentReq(Transaction t) {
        //Either make the transaction object from message or deligate to the other methods.
        //Transaction t = decodeMessage(message);
        validateToken(t);
    }

    public void makeSimpleTransaction(Transaction t) {
        if (t.getAmount() > 0) soap.TransferMoney(t);
    }

    public void makeTransaction(TokenServiceResponseMessage response, Transaction t) {
        if (t.getAmount() > 0) {
            if (response.getValidity()) {
                soap.TransferMoney(t);
            }
        }
    }

    public void validateToken(Transaction t) {
        TokenServiceRequestMessage requestMessage = new TokenServiceRequestMessage(TokenServiceRequestMessage.tokenServiceRequestMessageType.REQUEST_PAYMENT_VALIDATION);
        requestMessage.setToken("TOKENID-" + t.getTokenID());
        try {
            msg.validateToken(requestMessage, t);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
