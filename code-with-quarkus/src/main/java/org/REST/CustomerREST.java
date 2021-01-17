package org.REST;

import java.io.IOException;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import org.Json.TokenGenerationRequest;

import org.dtupay.DTUPay;
import org.dtupay.Transaction;
import javax.ws.rs.*;


import javax.ws.rs.Consumes;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import javax.ws.rs.client.Entity;

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


    @Path("/report/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response createReport(@PathParam("id") String ID) {
        String getRouting = "reporting.customer";
        dtuPay.forwardMQtoMicroservices(ID, getRouting);
        System.out.println("Customer report generation for " + ID + " has started");
        return Response.status(404, "Report generation failure").build();
        //TODO finish it
        //return Response.ok(Entity.entity(manager.getCustomers().get(ID), MediaType.APPLICATION_JSON)).build();
    }

    @Path("/tokens")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response requestTokens(TokenGenerationRequest request) throws IOException {
        System.out.println("post tokens");
        //dtuPay.startUp();
        String response = dtuPay.sendTokenGenerationRequest(request);

//           if (dtuPay.getNewTokenMap().containsKey(request.getCustomerId())) {
//            ArrayList<String> newTokens = dtuPay.getNewTokenMap().get(request.getCustomerId());
            return Response.ok(Entity.entity(response,MediaType.APPLICATION_JSON)).build();
//        }
//        else
//            return Response.status(400, "Failed").build();
//
    }


}
