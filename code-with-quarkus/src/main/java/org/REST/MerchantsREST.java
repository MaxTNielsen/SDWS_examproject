package org.REST;

import org.dtupay.DTUPay;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Path("/merchants")
public class MerchantsREST {
    DTUPay dtuPay = DTUPay.getInstance();

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public Response RegisterMerchant(String ID) throws NotFoundException {

        String setRoutingKey = "accountmanager.merchant";
        String answerToRequest = dtuPay.forwardMQtoMicroservices(ID, setRoutingKey);
        Boolean b = Boolean.parseBoolean(answerToRequest);

        if (b) {
            System.out.println("Merchant registration success");
            return Response.ok().build();
        } else {
            System.out.println("Merchant registration failed");
            return Response.status(400, "Registration failed").build();
        }
    }

    @Path("/report")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response createReport() {
        /*String getRouting = "reporting.manager";
        dtuPay.forwardMQtoMicroservices(ID, getRouting);
        System.out.println("Customer report generation for " + ID +" has started");*/
        return Response.status(404, "Report generation failure").build();
        //TODO finish it
        //return Response.ok(Entity.entity(manager.getCustomers().get(ID), MediaType.APPLICATION_JSON)).build();
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
