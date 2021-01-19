package REST;

import Utils.Event;
import Utils.TokenGenerationRequest;
import Utils.TokenGenerationResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtupay.DTUPay;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;


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
            //System.out.println("Create account success");
            return Response.ok().build();
        } else {
            //System.out.println("Create account success");
            return Response.status(400, "Registration failed").build();
        }
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
        if (!response.getEventType().equals("CUSTOMER_REPORT_RESPONSE")) {
            return Response.status(404, "Report generation failure").build();
        }
        return Response.ok(response.getArguments()[0], MediaType.APPLICATION_JSON).build();
    }

    @Path("/tokens")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response requestTokens(TokenGenerationRequest request) throws IOException {
        String response = dtuPay.sendTokenGenerationRequest(request);

        if (!response.equals("")) {
            Event event = gson.fromJson(response,Event.class);
            String jsonString = gson.toJson(event.getArguments()[0]);
            TokenGenerationResponse token = gson.fromJson(jsonString,TokenGenerationResponse.class);
            ArrayList<String> tokens = token.getTokens();
            return Response
                    .status(Response.Status.OK)
                    .entity(tokens)
                    .build();
        } else {
            return Response.status(400, "Token Generation Failed").build();
        }
    }

   /* @Path("/tokens")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTokens(TokenGenerationRequest request) {
        System.out.println("[REST] POST: " + request.toString());
        String response = dtuPay.sendTokenGenerationRequest(request);
        System.out.println("[REST] Response: " + response);
        if (!response.equals("")) {
            System.out.println("TOKEN GENERATION SUCCEED!");
            return Response.status(Response.Status.OK).entity(response).build();
        } else {
            return Response.status(400, "Token Generation Failed").build();
        }
    }*/
}
