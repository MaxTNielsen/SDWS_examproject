package helloservice;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class HelloService {

	WebTarget baseUrl;

	public HelloService() {
		Client client = ClientBuilder.newClient();
		baseUrl = client.target("http://localhost:8080/");
	}
	
	public String hello() {
		return baseUrl.path("hello-resteasy").request().get(String.class);
	}
}
