package org.REST;

import org.dtupay.DTUPay;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Path("/merchants")
public class MerchantsREST {
    String notFound = "merchant not found";
    String existed = "merchant already exist";
    String noBankAccount = "merchant has no bank account";
    DTUPay dtuPay = DTUPay.getInstance();

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public Response RegisterMerchant(String ID) throws IOException, TimeoutException, NotFoundException {
        String getRouting = "accountmanager.merchant";
        String s = dtuPay.forwardMQtoMicroservices(ID, getRouting);
        Boolean b = Boolean.parseBoolean(s);

        // System.out.println("After registration ");
        // System.out.println("containsKey " + dtuPay.getAccountRegMap().containsKey(ID));
        // if (dtuPay.getAccountRegMap().containsKey(ID) && dtuPay.getAccountRegMap().get(ID))
        if (b)
        {
            System.out.println("Merchant registration success");
            return Response.ok().build();
        }
        else
        {
            System.out.println("Merchant registration failed");
            return Response.status(400, "Registration failed").build();
        }
    }

   /* @GET
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
    }*/
}
