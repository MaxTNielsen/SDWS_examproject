package org.REST;

import org.accountmanager.client.ClientFactory;
import org.accountmanager.client.Merchant;
import org.accountmanager.model.AccountManager;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/merchants")
public class MerchantsREST {
    AccountManager manager = AccountManager.getInstance();
    ClientFactory factory = new ClientFactory();
    String notFound = "merchant not found";
    String existed = "merchant already exist";
    String noBankAccount = "merchant has no bank account";

    @GET
    public Response getAllMerchants()
    {
        return Response.ok(Entity.entity(manager.getMerchants(), MediaType.APPLICATION_JSON)).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClient(@PathParam("id") String ID)
    {

        if (!manager.getMerchants().containsKey(ID))
            return Response.status(404, notFound).build();
        return Response.ok(Entity.entity(manager.getMerchants().get(ID), MediaType.APPLICATION_JSON)).build();
    }

    // @Path("/customers")
    @POST
    public Response createMerchant(String ID, String CPR)
    {
        if (manager.hasMerchant(ID))
            return Response.status(406, existed).build();

        if (!manager.checkIfClientHasABankAccount(CPR))
            return Response.status(406, noBankAccount).build();
        
        Merchant c = factory.buildMerchant(ID);
        manager.registerMerchant(c);
        return Response.ok().build();
    }
}
