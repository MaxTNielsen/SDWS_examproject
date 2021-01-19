package REST;

import Utils.Event;
import Utils.Transaction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtupay.DTUPay;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/merchants")
public class MerchantsREST {
    DTUPay dtuPay = DTUPay.getInstance();
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public Response RegisterMerchant(String ID) throws NotFoundException {
        String setRoutingKey = "accountmanager.merchant.registration";
        String answerToRequest = dtuPay.forwardMQtoMicroservices(ID, setRoutingKey);
        Boolean b = Boolean.parseBoolean(answerToRequest);

        if (b) {
            return Response.ok().build();
        } else {
            return Response.status(400, "Registration failed").build();
        }
    }

    @Path("/payment")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doTransaction(Transaction t) throws IOException {
        boolean result = dtuPay.DTUPayDoPayment(t);

        if (result) {
            String setRouting = "reporting.payment";
            String requestType = "NEW_TRANSACTION";
            Object obj[] = new Object[]{(Object) t};
            Event request = new Event(requestType, obj);
            String requestString = gson.toJson(request);
            Event response = gson.fromJson(dtuPay.forwardMQtoMicroservices(requestString, setRouting), Event.class);
            return Response.ok().build();
        } else
        return Response.status(400, "Transaction failed").build();
    }

    @Path("/report")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response createReport(@QueryParam("id") String ID,
                                 @QueryParam("intervalStart") String intervalStart,
                                 @QueryParam("intervalEnd") String intervalEnd) {
        String setRouting = "reporting.merchant";
        String requestType = "MERCHANT_REPORT";
        Object obj[] = new Object[]{ID, intervalStart, intervalEnd};
        Event request = new Event(requestType, obj);
        String requestString = gson.toJson(request);
        Event response = gson.fromJson(dtuPay.forwardMQtoMicroservices(requestString, setRouting), Event.class);
        if (!response.getEventType().equals("MERCHANT_REPORT_RESPONSE")) {
            return Response.status(404, "Report generation failure").build();
        }
        return Response.ok(response.getArguments()[0], MediaType.APPLICATION_JSON).build();
    }

    @Path("/refund")
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response createReport(@QueryParam("originaltoken") String transactionID) throws IOException {
        if (dtuPay.doRefund(transactionID))
        {
            return Response.ok().build();
        }
        return Response.status(404, "Refund failure").build();
    }
}
