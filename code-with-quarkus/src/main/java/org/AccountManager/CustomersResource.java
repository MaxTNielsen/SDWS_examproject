package org.AccountManager;

import com.Client.Customer;

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

@Path("/customers")
public class CustomersResource {
    AccountManager manager = AccountManager.getInstance();
    String customerNotFound = "customer not found";
    String customerExist = "customer already exist";
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomers()
    {
        return Response.ok(Entity.entity(manager.customers.values(), MediaType.APPLICATION_JSON)).build();
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public Response createCustomer(String ID)
    {
        if (manager.hasCustomer(ID))
            return Response.status(406, customerNotFound).build();
        
        Customer c = new Customer(ID);
        manager.registerCustomer(c);
        return Response.ok().build();
        
    }

    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getCustomer(@PathParam("id") String ID)
    // public Response getCustomer(@QueryParam("id") String ID)
    {

        if (!manager.hasCustomer(ID))
            return Response.status(404, customerExist).build();
        return Response.ok(Entity.entity(manager.customers.get(ID), MediaType.APPLICATION_JSON)).build();
    }
}
