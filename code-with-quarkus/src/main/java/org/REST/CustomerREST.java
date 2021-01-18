package org.REST;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.Json.TokenGenerationRequest;
import org.dtupay.DTUPay;
import org.dtupay.Transaction;
import reporting.model.Event;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/customers")
public class CustomerREST {
    DTUPay dtuPay = DTUPay.getInstance();
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public Response RegisterCostumer(String ID) {

        String setRoutingKey = "accountmanager.customer.registration";
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
        if (dtuPay.DTUPayDoPayment(t)) {

            String result = dtuPay.sendPaymentRequest(t);
            boolean b = Boolean.parseBoolean(result);
            boolean reported = false;
            if (b) {
                String setRouting = "reporting.payment";
                String requestType = "NEW_TRANSACTION";
                Object obj[] = new Object[]{(Object) t};
                Event request = new Event(requestType, obj);
                String requestString = gson.toJson(request);
                Event response = gson.fromJson(dtuPay.forwardMQtoMicroservices(requestString, setRouting), Event.class);
                /*if(response.getEventType().equals("CUSTOMER_REPORT_RESPONSE"))
                {
                    reported = true;
                }*/

                System.out.println("Transaction successful");

                return Response.ok().build();
            } else {
                System.out.println("Transaction failed");
                return Response.status(400, "Transaction failed").build();
            }
        }
        return Response.ok().build();
    }

    @Path("/report")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response createReport(@QueryParam("id") String ID,
                                 @QueryParam("intervalStart") String intervalStart,
                                 @QueryParam("intervalEnd") String intervalEnd) {
        String setRouting = "reporting.customer";
        String requestType = "COSTUMER_REPORT";
        Object obj[] = new Object[]{ID, intervalStart, intervalEnd};
        Event request = new Event(requestType, obj);
        String requestString = gson.toJson(request);
        Event response = gson.fromJson(dtuPay.forwardMQtoMicroservices(requestString, setRouting), Event.class);
        if(!response.getEventType().equals("CUSTOMER_REPORT_RESPONSE"))
        {
            return Response.status(404, "Report generation failure").build();
        }
        return Response.ok(response.getArguments()[0], MediaType.APPLICATION_JSON).build();
    }

    @Path("/tokens")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response requestTokens(TokenGenerationRequest request) throws IOException {
        System.out.println("[REST] POST: " + request.toString());
        //dtuPay.startUp();
        String response = dtuPay.sendTokenGenerationRequest(request);
        System.out.println("[REST] Response: " + response);
        if (!response.equals(""))
            return Response.ok(response, MediaType.APPLICATION_JSON).build();
        else
            return Response.status(400, "Token Generation Failed").build();

    }
}
