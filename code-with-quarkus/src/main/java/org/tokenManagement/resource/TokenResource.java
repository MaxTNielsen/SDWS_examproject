package org.tokenManagement.resource;

import org.tokenManagement.service.TokenManager;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/tokens")
public class TokenResource {
    TokenManager manager = TokenManager.getInstance();
    String fail_message = "Not found/failed";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTokens(){
        return Response.ok(Entity.entity(manager.tokens.values(), MediaType.APPLICATION_JSON)).build();
    }

    @Path("{cprNumber}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getToken(@PathParam("cprNumber") String cprNumber){
        String token = manager.getToken(cprNumber);
        if (token=="")
            return Response.status(404, fail_message).build();
        else return Response.ok(Entity.entity(token, MediaType.TEXT_PLAIN)).build();
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response generateToken(String cprNumber){

            return Response.ok(Entity.entity(manager.generateToken(cprNumber),MediaType.TEXT_PLAIN)).build();

    }



}
