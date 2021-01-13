package TokenManagement;
import org.apache.http.util.EntityUtils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


public class TokenManagementService {
    WebTarget baseUrl;

    public TokenManagementService() {
        Client client = ClientBuilder.newClient();
        baseUrl = client.target("http://localhost:8080/");
    }

    public Response generateToken(String cprNumber)
    {
        Response response = baseUrl.path("tokens").request()
                .post(Entity.entity(cprNumber,MediaType.APPLICATION_JSON));

        return response;
    }



}
