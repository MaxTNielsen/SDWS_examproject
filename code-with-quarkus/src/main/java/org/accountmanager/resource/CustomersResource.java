package org.accountmanager.resource;

import org.accountmanager.client.ClientFactory;
import org.accountmanager.client.Customer;
import org.accountmanager.model.AccountManager;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/customers")
public class CustomersResource {
    AccountManager manager = AccountManager.getInstance();
    ClientFactory factory = new ClientFactory();
    String customerNotFound = "customer not found";
    String customerExist = "customer already exist";
    String customerHasNoBank = "customer has no bank account";
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomers()
    {
        return Response.ok(Entity.entity(manager.getCustomers().values(), MediaType.APPLICATION_JSON)).build();
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    // public Response createCustomer(String ID, String CPR)
    public Response createCustomer(String ID)
    {
        if (manager.hasCustomer(ID))
            return Response.status(406, customerExist).build();

        if (!manager.checkIfClientHasABankAccount(ID))
            return Response.status(406, customerHasNoBank).build();
        
        Customer c = factory.buildCustomer(ID);
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
            return Response.status(404, customerNotFound).build();
        return Response.ok(Entity.entity(manager.getCustomers().get(ID), MediaType.APPLICATION_JSON)).build();
    }
}
