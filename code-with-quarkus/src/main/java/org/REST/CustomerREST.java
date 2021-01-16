package org.REST;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.dtupay.DTUPay;
import org.dtupay.Transaction;

import javax.ws.rs.Consumes;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/customers")
public class CustomerREST {
    DTUPay dtuPay = DTUPay.getInstance();

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public Response RegisterCostumer(String ID) throws IOException, TimeoutException, NotFoundException {
        String getRouting = "accountmanager.customer";
        dtuPay.forwardMQtoMicroservices(ID, getRouting);
        System.out.println("After registration ");
        System.out.println("containsKey " + dtuPay.getAccountRegMap().containsKey(ID));
        if (dtuPay.getAccountRegMap().containsKey(ID) && dtuPay.getAccountRegMap().get(ID))
            return Response.ok().build();
        else
            return Response.status(400, "Registration failed").build();
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
}
