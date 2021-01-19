package payment;

import dtu.ws.fastmoney.BankServiceException_Exception;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeoutException;

public class PaymentBL {

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
            System.out.println("Transaction object inside bank payment method " + t);
            System.out.println("Customer amount before " + soap.getBalance(t.getCustomId()));
            System.out.println("Merchant amount before " + soap.getBalance(t.getMerchId()));

            soap.TransferMoney(t);
            if (t.isApproved()) {
                System.out.println("Transaction object after payment " + t);
                System.out.println("Customer amount after " + soap.getBalance(t.getCustomId()));
                System.out.println("Merchant amount after " + soap.getBalance(t.getMerchId()));
                return true;
            } else return false;
        }
        return false;
    }
}
