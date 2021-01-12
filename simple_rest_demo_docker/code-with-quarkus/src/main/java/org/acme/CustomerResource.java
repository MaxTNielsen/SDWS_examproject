package org.acme;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

@Path("/accounts")
public class CustomerResource {
    SimpleDTUPayBL dtuPay = SimpleDTUPayBL.getInstance_();

    @Path("{id}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getCustomers(@PathParam("id") String id) {
        try {
            return dtuPay.getCustomer(Integer.parseInt(id));
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
        }
        return "Customer doesn't exist";
    }

    @Path("customer")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerCustomer(Customer c) {
        try {
            if (!dtuPay.getCustomerMap().containsKey(c.getId())) {
                dtuPay.registerCustomer(c);
                return Response.created(new URI("http://localhost:8080/accounts" + c.getId())).build();
            } else return Response.status(404, "Customer already registered").build();
        } catch (URISyntaxException e) {
            return Response.status(400, e.getMessage()).build();
        }
    }
}