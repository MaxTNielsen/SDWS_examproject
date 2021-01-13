package accountManagement;


import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import dtu.ws.fastmoney.User;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;


public class AccountManageService {
    WebTarget baseUrl;
    BankService bankService;

    public AccountManageService() {
        bankService = new BankServiceService().getBankServicePort();
        Client client = ClientBuilder.newClient();
        baseUrl = client.target("http://localhost:8080/");
    }

    public boolean registerCustomer(String ID) {
        Response response = baseUrl.path("customers").request()
                .post(Entity.entity(ID, MediaType.TEXT_PLAIN));
        return response.getStatus() == 200;
    }

    public boolean registerMerchant(String ID) {
        Response response = baseUrl.path("merchants").request()
                .post(Entity.entity(ID, MediaType.TEXT_PLAIN));
        return response.getStatus() == 200;
    }

    public String registerClientAtBank(String cpr, String fname, String lname, BigDecimal balance) {
        User user = new User();
        user.setCprNumber(cpr);
        user.setFirstName(fname);
        user.setLastName(lname);
        try {
            return bankService.createAccountWithBalance(user, balance);
        } catch (BankServiceException_Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean isCustomerRegistered(String ID) {
        Response response = baseUrl.path("customers").path(ID).request()
                .get();
        return response.getStatus() == 200;
    }

    public boolean isMerchantRegistered(String ID) {
        Response response = baseUrl.path("merchants").path(ID).request()
                .get();
        return response.getStatus() == 200;
    }

    public void deleteBankAccounts(List<String> l) {
        for (String s : l) {
            try {
                bankService.retireAccount(s);
            } catch (BankServiceException_Exception e) {
                System.out.println(e);
            }
        }
    }
}
