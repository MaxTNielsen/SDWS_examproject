package org.CustomerREST;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import org.Json.AccountRegistrationReponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/customers")
public class CustomerREST {
    static CustomerREST instance;

    private static final String CUSTOMER_REG_QUEUE = "CUSTOMER_REG_QUEUE";
    static final String CUSTOMER_REG_RESPONSE_QUEUE = "CUSTOMER_REG_RESPONSE_QUEUE";
    Map<String, Boolean> accountRegMap;

    public CustomerREST() {
        listenMsgCustomerRegResponse();
        accountRegMap = new HashMap<>();
    }

    public static CustomerREST getInstance() {
        if (instance == null)
            instance = new CustomerREST();
        return instance;
    }

    public static void main(String[] args) throws IOException, TimeoutException {
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public void sendMsgCustomerReg(String ID) throws IOException, TimeoutException {
        Response r = Response.status(406, "Create account fail").build();
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(CUSTOMER_REG_QUEUE, false, false, false, null);

        channel.basicPublish("", CUSTOMER_REG_QUEUE, null, ID.getBytes());
        channel.close();
        connection.close();
        // try {
        //     // System.out.println("mark 2");
        //     // Thread.sleep(3000);
        //     // System.out.println("mark 3");
        //     // System.out.println("ID: " + ID);
        //     // System.out.println("status: " + accountRegMap.get(ID));
        //     // if (accountRegMap.containsKey(ID) && accountRegMap.get(ID))
        //     // {
        //     //     System.out.println("respond ok");
        //     //     return r = Response.ok().build();

        //     // }
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
        // System.out.println("mark 1");
        // System.out.println("respond fail");
        // return r;
    }

    void listenMsgCustomerRegResponse()
    {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
		Connection connection;
        try {
            connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
        
            channel.queueDeclare(CUSTOMER_REG_RESPONSE_QUEUE, false, false, false, null);
            System.out.println("register reg response queue");
            // channel.exchangeDeclare(EXCHANGE_NAME, QUEUE_TYPE);
            // String queueName = channel.queueDeclare().getQueue();
            // channel.queueBind(queueName, EXCHANGE_NAME, TOPIC);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                Gson gson = new Gson();
                String json = new String(delivery.getBody());
                System.out.println("delivery: " + json);
                AccountRegistrationReponse r = gson.fromJson(json, AccountRegistrationReponse.class);
                System.out.println(r.ID);
                System.out.println(r.status);
                accountRegMap.put(r.ID, r.status);
            };
            

            // channel.basicPublish("", CUSTOMER_REG_QUEUE, null, message.getBytes());
            channel.basicConsume("", true, deliverCallback, consumerTag -> {
            });
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
