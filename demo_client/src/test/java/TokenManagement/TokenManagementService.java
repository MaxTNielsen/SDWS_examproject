package TokenManagement;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


public class TokenManagementService {
    WebTarget baseUrl;

    public TokenManagementService() {
        Client client = ClientBuilder.newClient();
        baseUrl = client.target("http://localhost:8080/");
    }

    public boolean generateToken(String cprNumber)
    {
        Response response = baseUrl.path("tokens").request()
                .post(Entity.entity(cprNumber,MediaType.APPLICATION_JSON));

        System.out.println(response.getClass());

        return response.getStatus() == 200;
    }

    public boolean tokenExists(String ID)
    {
        Response response = baseUrl.path("tokens").path(ID).request()
                .get();
        return response.getStatus() == 200;
    }


}
