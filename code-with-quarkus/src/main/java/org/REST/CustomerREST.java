package org.REST;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.dtupay.DTUPay;

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
        dtuPay.sendMSGToRegCustomer(ID);
        System.out.println("After registration ");
        System.out.println("containsKey " + dtuPay.getAccountRegMap().containsKey(ID));
        if (dtuPay.getAccountRegMap().containsKey(ID) && dtuPay.getAccountRegMap().get(ID))
            return Response.ok().build();
        else
            return Response.status(400, "Registration failed").build();
    }
}
