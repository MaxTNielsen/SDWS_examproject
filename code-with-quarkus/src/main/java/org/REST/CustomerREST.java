package org.REST;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.dtupay.DTUPay;
import org.dtupay.Transaction;
import reporting.model.Event;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/customers")
public class CustomerREST {
    DTUPay dtuPay = DTUPay.getInstance();

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public Response RegisterCostumer(String ID) {

        String setRoutingKey = "accountmanager.customer";
        String answerToRequest = dtuPay.forwardMQtoMicroservices(ID, setRoutingKey);
        boolean b = Boolean.parseBoolean(answerToRequest);

        if (b) {
            System.out.println("Create account success");
            return Response.ok().build();
        } else {
            System.out.println("Create account success");
            return Response.status(400, "Registration failed").build();
        }
    }

    @Path("/payment")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doTransaction(Transaction t) throws IOException {
        dtuPay.sendPaymentRequest(t);
        if (dtuPay.getTransactionMap().containsKey(t.getToken()) && dtuPay.getTransactionMap().get(t.getToken()))
            return Response.ok().build();
        else
            return Response.status(400, "Registration failed").build();
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
        String requestType = "COSTUMER_REPORT";
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Object obj[] = new Object[3];
        obj[0] = ID;
        obj[1] = intervalStartPoint;
        obj[2] = intervalEndPoint;
        Event request = new Event(requestType, obj);
        String requestString = gson.toJson(request);
        System.out.println("Costumer report generation for " + requestString +" has started");
        Event response = gson.fromJson(dtuPay.forwardMQtoMicroservices(requestString, setRouting), Event.class);
        if(!response.getEventType().equals("CUSTOMER_REPORT_RESPONSE"))
        {
            return Response.status(404, "Report generation failure").build();
        }
        return Response.ok(response.getArguments()[0], MediaType.APPLICATION_JSON).build();
    }
}
