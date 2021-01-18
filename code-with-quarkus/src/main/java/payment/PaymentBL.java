package payment;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class PaymentBL {

    SOAPPort soap;
    MessageParsingPort msg;
    static PaymentBL instance;

    private PaymentBL() {
        try {
            soap = new SOAPPort();
            msg = new MessageParsingPort();
            msg.listen();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (TimeoutException e) {
            System.out.println(e.getMessage());
        }
    }

    public static PaymentBL getInstance() {
        if (instance == null)
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
