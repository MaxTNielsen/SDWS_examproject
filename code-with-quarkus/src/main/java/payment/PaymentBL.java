package payment;

import org.tokenManagement.service.TokenManager;

public class PaymentBL {
<<<<<<< HEAD

    SOAPPort soap;
    static PaymentBL instance;

    private PaymentBL() {
        try {
            soap = new SOAPPort();
            MessageParsingPort.listen();
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
=======
	
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
>>>>>>> f4535db95ae1f985b54f8fbceb6e1d31d8685d26
}
