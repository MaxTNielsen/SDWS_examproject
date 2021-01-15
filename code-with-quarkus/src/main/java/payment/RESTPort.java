package payment;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RESTPort {

	@Path("/payment")
	public class CustomersResource {
	   String customerNotFound = "customer not found";
	   String customerExist = "customer already exist";

	   @POST
	    @Consumes(MediaType.APPLICATION_JSON)
	    public Response doTransaction(Transaction t) {
		   	PaymentBL pay = new PaymentBL();		   
	        if (pay.makeTransaction(t)) {
	            return Response.status(201, "Transaction completed").build();
	        } else return Response.status(400, "Transaction failed").build();
	    }

	}
}
