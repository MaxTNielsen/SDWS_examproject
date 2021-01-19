package REST;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtupay.DTUPay;
import Utils.Transaction;
import reporting.model.Event;

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
            //System.out.println("Merchant registration success");
            return Response.ok().build();
        } else {
            //System.out.println("Merchant registration failed");
            return Response.status(400, "Registration failed").build();
        }
    }

    @Path("/payment")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doTransaction(Transaction t) throws IOException {
        boolean result = dtuPay.DTUPayDoPayment(t);

        System.out.println("Payment is " + result);

        if (result) {
           /* String setRouting = "reporting.payment";
            String requestType = "NEW_TRANSACTION";
            Object obj[] = new Object[]{(Object) t};
            Event request = new Event(requestType, obj);
            String requestString = gson.toJson(request);
            Event response = gson.fromJson(dtuPay.forwardMQtoMicroservices(requestString, setRouting), Event.class);*/

            System.out.println("Transaction successful");
            return Response.ok().build();
        } else
            System.out.println("Transaction failed");
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
        String setRouting = "reporting.refund";
        Object obj1[] = new Object[]{transactionID};
        Event OriginalTransactionRequest = new Event("GET_TRANSACTION", obj1);
        String OriginalTransactionRequestString = gson.toJson(OriginalTransactionRequest);
        Event response = gson.fromJson(dtuPay.forwardMQtoMicroservices(OriginalTransactionRequestString, setRouting), Event.class);
        System.out.println(response.getEventType());
        if (response.getEventType() == "TRANSACTION_FOUND") {
            Transaction originalTransaction = gson.fromJson(response.getArguments()[0].toString(), Transaction.class);
            Transaction refundTransaction = originalTransaction.createRefund();
            boolean bankResult = dtuPay.DTUPayDoPayment(refundTransaction);
            boolean reported = false;
            if (bankResult) {
                String requestType = "NEW_REFUND";
                Object obj[] = new Object[]{transactionID};
                Event request = new Event(requestType, obj);
                String requestString = gson.toJson(request);
                Event reportRefundResponse = gson.fromJson(dtuPay.forwardMQtoMicroservices(requestString, setRouting), Event.class);
                if (reportRefundResponse.getEventType() == "REFUND_REGISTERED") {
                    reported = true;
                }
            }
            System.out.println(bankResult + ":::" + reported);
            if (bankResult && reported) {
                return Response.ok().build();
            }
        }
        return Response.status(404, "Report generation failure").build();
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
