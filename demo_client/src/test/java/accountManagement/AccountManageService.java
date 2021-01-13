package accountManagement;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


public class AccountManageService {
    WebTarget baseUrl;

	public AccountManageService() {
		Client client = ClientBuilder.newClient();
		baseUrl = client.target("http://localhost:8080/");
	}
    
    public boolean registerCustomer(String ID)
    {
        Response response = baseUrl.path("customers").request()
                .post(Entity.entity(ID, MediaType.TEXT_PLAIN));
        return response.getStatus() == 200;
    }

    public boolean registerMerchant(String ID)
    {
        Response response = baseUrl.path("merchants").request()
                .post(Entity.entity(ID, MediaType.TEXT_PLAIN));
        return response.getStatus() == 200;
    }

    public boolean isCustomerRegistered(String ID)
    {
        Response response = baseUrl.path("customers").path(ID).request()
                .get();
        return response.getStatus() == 200;
    }

    public boolean isMerchantRegistered(String ID)
    {
        Response response = baseUrl.path("merchants").path(ID).request()
                .get();
        return response.getStatus() == 200;
    }
}
