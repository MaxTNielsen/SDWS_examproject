package org.REST;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.dtupay.DTUPay;
import reporting.model.Event;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public Response createReport(@QueryParam("id") String ID,
                                 @QueryParam("intervalStart") String intervalStart,
                                 @QueryParam("intervalEnd") String intervalEnd) {
        System.out.println("received");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime intervalStartPoint = LocalDateTime.parse(intervalStart, formatter);
        LocalDateTime intervalEndPoint = LocalDateTime.parse(intervalEnd, formatter);
        String setRouting = "reporting";
        String requestType = "MERCHANT_REPORT";
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Object obj[] = new Object[3];
        obj[0] = ID;
        obj[1] = intervalStartPoint;
        obj[2] = intervalEndPoint;
        Event request = new Event(requestType, obj);
        String requestString = gson.toJson(request);
        System.out.println("Merchant report generation for " + requestString +" has started");
        Event response = gson.fromJson(dtuPay.forwardMQtoMicroservices(requestString, setRouting), Event.class);
        if(!response.getEventType().equals("MERCHANT_REPORT_RESPONSE"))
        {
            return Response.status(404, "Report generation failure").build();
        }
        return Response.ok(response.getArguments()[0], MediaType.APPLICATION_JSON).build();
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
