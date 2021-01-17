package org.REST;

import org.dtupay.DTUPay;
import org.dtupay.Transaction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Path("/manager")
public class ManagerREST {
    DTUPay dtuPay = DTUPay.getInstance();

    @Path("/report")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response createReport()
    {
        /*String getRouting = "reporting.manager";
        dtuPay.forwardMQtoMicroservices(ID, getRouting);
        System.out.println("Customer report generation for " + ID +" has started");*/
        return Response.status(404, "Report generation failure").build();
        //TODO finish it
        //return Response.ok(Entity.entity(manager.getCustomers().get(ID), MediaType.APPLICATION_JSON)).build();
    }

    @Path("/moneyflow")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getMoneyFlow()
    {
        /*String getRouting = "reporting.customer";
        dtuPay.forwardMQtoMicroservices(ID, getRouting);
        System.out.println("Customer report generation for " + ID +" has started");*/
        return Response.status(404, "Report generation failure").build();
        //TODO finish it
        //return Response.ok(Entity.entity(manager.getCustomers().get(ID), MediaType.APPLICATION_JSON)).build();
    }
}
