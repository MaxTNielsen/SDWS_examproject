package org.acme;

import javax.ws.rs.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/transactions")
public class TransactionResource {

    SimpleDTUPayBL dtuPay1 = SimpleDTUPayBL.getInstance_();
    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransactions(@PathParam("id") String id) {
        try {
            if (!dtuPay1.getCustomerMap().containsKey(id))
                return Response.status(400, "Customer not found").build();
            else
                return Response.ok(Entity.entity(dtuPay1.getAllTransactions(id), MediaType.APPLICATION_JSON)).build();
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}