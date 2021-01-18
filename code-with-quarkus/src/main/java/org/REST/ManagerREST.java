package org.REST;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.cucumber.java.an.E;
import org.dtupay.DTUPay;

import org.dtupay.Transaction;
import reporting.model.Event;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Path("/manager")
public class ManagerREST {
    DTUPay dtuPay = DTUPay.getInstance();
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();

    @Path("/report")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response createReport()
    {
        String getRouting = "reporting.manager.report";
        String requestType = "MANAGER_REPORT";
        Event request = new Event(requestType);
        String requestString = gson.toJson(request);
        Event response = gson.fromJson(dtuPay.forwardMQtoMicroservices(requestString, getRouting), Event.class);
        if(!response.getEventType().equals("MANAGER_REPORT_RESPONSE"))
        {
            return Response.status(404, "Report generation failure").build();
        }
        return Response.ok(response.getArguments()[0], MediaType.APPLICATION_JSON).build();
    }

    @Path("/moneyflow")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getMoneyFlow()
    {
        String getRouting = "reporting.manager.moneyflow";
        String requestType = "MANAGER_MONEY_FLOW";
        Event request = new Event(requestType);
        String requestString = gson.toJson(request);
        Event response = gson.fromJson(dtuPay.forwardMQtoMicroservices(requestString, getRouting), Event.class);
        if(!response.getEventType().equals("MANAGER_MONEY_FLOW"))
        {
            return Response.status(404, "Report generation failure").build();
        }
        return Response.ok(response.getArguments()[0], MediaType.APPLICATION_JSON).build();
    }
}
