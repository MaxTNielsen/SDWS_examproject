package TokenManagement;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

public class TokenManagementService {
    WebTarget baseUrl;

    public TokenManagementService() {
        Client client = ClientBuilder.newClient();
        baseUrl = client.target("http://localhost:8083/");
    }

    public Response generateToken(String request)
    {
        Response response = baseUrl.path("customers/tokens").request()
                .post(Entity.entity(request,MediaType.APPLICATION_JSON));

        return response;
    }
    public String getValidToken(String customerId) {
        String token = "";
        System.out.println("Generating a valid token for customerId: " + customerId);
        TokenGenerationRequest request = new TokenGenerationRequest(customerId,1);
        Response response = baseUrl.path("customers/tokens").request()
                .post(Entity.entity(request,MediaType.APPLICATION_JSON));
        if (response.getStatus() == 200) {
            ArrayList<String> tokens =  response.readEntity(ArrayList.class);
            token = tokens.get(0);
            System.out.println("Token is: " + token);
        }
        return token;

    }




}
