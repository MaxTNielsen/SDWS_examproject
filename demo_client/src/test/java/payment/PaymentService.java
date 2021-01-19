package payment;

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Client.*;
import io.cucumber.messages.internal.com.google.gson.Gson;
import org.apache.http.util.EntityUtils;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import dtu.ws.fastmoney.User;

public class PaymentService {
    WebTarget baseUrl;
    WebTarget baseUrlForBank;
    BigDecimal balance;
    BankService bank;
    public ArrayList<String> userList = new ArrayList<String>();

    public PaymentService() {
        bank = new BankServiceService().getBankServicePort();
        Client client = ClientBuilder.newClient();
        baseUrl = client.target("http://localhost:8080/");
    }

    public void retireUsers() {
        for (int i = 0; i < userList.size(); i++) {
            try {
                bank.retireAccount(userList.get(i));
            } catch (BankServiceException_Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
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

    public boolean pay(String tokenID, String mid, String cid, int amount) throws WebApplicationException {

<<<<<<< HEAD
        Response response = baseUrl.path("merchants").path("payment").request().post(Entity.entity(t, MediaType.APPLICATION_JSON_TYPE));
=======
        Transaction t = new Transaction(tokenID ,mid, cid,amount);
        System.out.println(t.getCustomId());
        Gson gson = new Gson();
        String request_string = gson.toJson(t);

        Response response = baseUrl.path("merchants").path("payment").request().post(Entity.entity(request_string, MediaType.APPLICATION_JSON_TYPE));
>>>>>>> f4535db95ae1f985b54f8fbceb6e1d31d8685d26
        if (response.getStatus() == 200) {
            response.close();
            t.setApproved(true);
            return t.isApproved();
        } else return t.isApproved();
    }

    public BigDecimal getBalance(String id) {
        try {
            return bank.getAccount(id).getBalance();
        } catch (BankServiceException_Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new BigDecimal(0);
        }
    }

    public Response generateToken(String request)
    {
        Response response = baseUrl.path("customers/tokens").request()
                .post(Entity.entity(request,MediaType.APPLICATION_JSON));

        return response;
    }

 /*   public Event getToken(String request)
    {
        return baseUrl.path("customers").path("tokens").request().accept(MediaType.APPLICATION_JSON).get(Event.class);
    }
*/
}