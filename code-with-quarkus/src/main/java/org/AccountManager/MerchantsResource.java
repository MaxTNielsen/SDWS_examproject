package org.AccountManager;

import com.Client.Merchant;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/merchants")
public class MerchantsResource {
    AccountManager manager = AccountManager.getInstance();
    String notFound = "customer not found";
    String existed = "merchant already exist";

    @GET
    public Response getAllMerchants()
    {
        return Response.ok(Entity.entity(manager.merchants, MediaType.APPLICATION_JSON)).build();
    }

    @GET
    public Response getMerchant(@QueryParam("id") String ID)
    {

        if (!manager.merchants.containsKey(ID))
            return Response.status(404, notFound).build();
        return Response.ok(Entity.entity(manager.merchants.get(ID), MediaType.APPLICATION_JSON)).build();
    }

    // @Path("/customers")
    @POST
    public Response createMerchant(String ID)
    {
        if (manager.hasMerchant(ID))
            return Response.status(406, existed).build();
        
        Merchant c = new Merchant(ID);
        manager.registerMerchant(c);
        return Response.ok().build();
        
    }
}
