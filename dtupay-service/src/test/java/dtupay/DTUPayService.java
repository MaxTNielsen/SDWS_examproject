package dtupay;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class DTUPayService {
    WebTarget baseUrl;

    public DTUPayService(){
        Client client = ClientBuilder.newClient();
        baseUrl = client.target("http://localhost:8080/");
    }

    public boolean sendRequest(String request){
        Response response = baseUrl.path("customers/tokens").request()
                .post(Entity.entity(request, MediaType.APPLICATION_JSON));
        return true;
    }
}
