package behaviourtests;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class Service {

	public Customer doSomething(Customer customer) {
		Client client = ClientBuilder.newClient();
	     WebTarget r =
	      client.target("http://localhost:8080/");
	     var response = r.path("status").request().put(Entity.json(customer),Customer.class);
	     return response;
	}
}
