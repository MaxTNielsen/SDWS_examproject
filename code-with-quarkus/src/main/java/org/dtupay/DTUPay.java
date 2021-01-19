package org.dtupay;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;


import io.cucumber.core.gherkin.messages.internal.gherkin.Token;
import io.quarkus.runtime.ShutdownEvent;
import org.tokenManagement.messaging.model.TokenValidationRequest;
import payment.PaymentBL;

import org.Json.*;

import org.accountmanager.model.AccountManager;
import org.tokenManagement.service.TokenManager;
import payment.TokenValidationResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.io.IOException;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class DTUPay implements AutoCloseable 
{

    String hostName = "localhost";

    private static final String TOKEN_ROUTING_KEY = "token.request";
    private static final String PAYMENT_ROUTING_KEY = "payment.request";
    private static final String EXCHANGE_NAME = "MICROSERVICES_EXCHANGE";
    static final String ACCOUNT_VALIDATION_ROUTING_KEY = "accountmanager.validation.*";
    static final String PAYMENT_ROUTING_KEY = "payment.transaction";


    Connection DTUPayConnection;
    Channel microservicesChannel;
    Channel replyChannel;


    void onStop(@Observes ShutdownEvent ev) {
        try {
            DTUPayConnection.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    AccountManager m = AccountManager.getInstance();

    TokenManager tokenManager = TokenManager.getInstance();

    PaymentBL payment_service = PaymentBL.getInstance();

    private Map<String, Boolean> accountRegMap = new HashMap<>();

    private Map<String, ArrayList<String>> newTokenMap = new HashMap<>();
    static DTUPay instance;

    public DTUPay() {
        startUp();
    }

    public static DTUPay getInstance() {
        if (instance == null)
            return new DTUPay();
        return instance;
    }

    public void startUp() {
        dtuPayConnection();
        initializeAllChannels();
    }

    void dtuPayConnection() {
        try {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(hostName);
            DTUPayConnection = connectionFactory.newConnection();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    void initializeAllChannels() {
        try {
            microservicesChannel = DTUPayConnection.createChannel();
            replyChannel = DTUPayConnection.createChannel();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String forwardMQtoMicroservices(String serializedMessage, String routingKey) {
        String correlateID = UUID.randomUUID().toString();
        try {
            String replyQueueName = replyChannel.queueDeclare("reply " + routingKey, false, false, false, null).getQueue();
<<<<<<< HEAD
            replyChannel.queuePurge(replyQueueName);

=======
>>>>>>> f4535db95ae1f985b54f8fbceb6e1d31d8685d26
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().correlationId(correlateID)
                    .replyTo(replyQueueName).build();
            microservicesChannel.exchangeDeclare(EXCHANGE_NAME, "topic");
            // System.out.println("DTU publish: " + serializedMessage);
            // System.out.println("replyQueueName: " + replyQueueName);

            // System.out.println("routingKey: " + routingKey);
            microservicesChannel.basicPublish(EXCHANGE_NAME, routingKey, properties, serializedMessage.getBytes("UTF-8"));
            final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

            String ctag = microservicesChannel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
                if (delivery.getProperties().getCorrelationId().equals(correlateID)) {
                    response.offer(new String(delivery.getBody(), "UTF-8"));
                }
            }, consumerTag -> {
            });

            String result;
            try {
                result = response.take();
                microservicesChannel.basicCancel(ctag);
                //System.out.println("result: " + result);
                return result;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private Map<String, Boolean> transactionMap = new HashMap<>();

    public Map<String, Boolean> getTransactionMap() {
        return transactionMap;
    }

    public String sendTokenGenerationRequest(TokenGenerationRequest request) {
        System.out.println("Inside sendTokenGenerationRequest " + request.toString());
        Gson gson = new Gson();
        String response = "";
        Object[] objects = new Object[3];
        objects[0] = "c";
        objects[1] = request.getCustomerId();
        objects[2] = request;
        Event e = new Event("TOKEN_GENERATION_REQUEST", objects);
        String serilizedmessage = gson.toJson(e);

        if (!Boolean.parseBoolean(forwardMQtoMicroservices(serilizedmessage, ACCOUNT_VALIDATION_ROUTING_KEY)))
            return response;
        //System.out.println("Finished ID validation");

        response = forwardMQtoMicroservices(serilizedmessage, TOKEN_ROUTING_KEY);

        if (response.equals(""))
            return response;

        //System.out.println("DTU pay get response:" + response);
        return response;
    }

    public boolean DTUPayDoPayment(Transaction t) {
        //T = token, mid, amount
        Gson gson = new Gson();
        TokenValidationRequest tokenValidationRequest = new TokenValidationRequest(t.getToken());
        Object[] objects = new Object[3];
        objects[0] = "m";
        objects[1] = t.getMerchId();
        objects[2] = tokenValidationRequest;
        Event e = new Event("TOKEN_VALIDATION_REQUEST", objects);

        String serilizedmessage = gson.toJson(e);
        String customerId = "";

<<<<<<< HEAD
        if (!Boolean.parseBoolean(forwardMQtoMicroservices(serilizedmessage, ACCOUNT_VALIDATION_ROUTING_KEY)))
=======
        //check whether merchant exists
        System.out.println("[DTUPay] e.toString is" + e.toString());
        if (!Boolean.parseBoolean(forwardMQtoMicroservices(serilizedmessage, ACCOUNT_VALIDATION_ROUTING_KEY))) {
            System.out.println("[DTUPay] Merchant validation failed");
>>>>>>> f4535db95ae1f985b54f8fbceb6e1d31d8685d26
            return false;
        }

        //check if token is valid
        String token_response = forwardMQtoMicroservices(serilizedmessage, TOKEN_ROUTING_KEY);
        Event validation_result = gson.fromJson(token_response,Event.class);
        System.out.println("[DTUPay] token_response: "+ token_response);

<<<<<<< HEAD
        String response = forwardMQtoMicroservices(serilizedmessage, TOKEN_ROUTING_KEY);

        Event event = gson.fromJson(response, Event.class);

        String jsonString = gson.toJson(event.getArguments()[0]);
        TokenValidationResponse tokenValidationResponse = gson.fromJson(jsonString, TokenValidationResponse.class);

        t.setCustomId(tokenValidationResponse.getCustomerId());
        //t.setApproved(true);
        tokenValidationResponse.isValid();

        if (!tokenValidationResponse.isValid())
=======
        if (token_response.equals("")) {
            System.out.println("[DTUPay] Token validation failed");
>>>>>>> f4535db95ae1f985b54f8fbceb6e1d31d8685d26
            return false;
        }
        else
            {
                String jsonString = gson.toJson(validation_result.getArguments()[0]);
                System.out.println("[DTUPay] TokenValidationResponse: "+ jsonString);
                TokenValidationResponse response = gson.fromJson(jsonString,TokenValidationResponse.class);
                customerId = response.getCustomerId();
            }



<<<<<<< HEAD
=======
        //get customerId from token service response
        t.setCustomId("0aa0383e-df5a-4afb-85f7-0dd5d33dfb77");

        String s = gson.toJson(t);
        System.out.println("[DTUPay] Sending payment " + s);
        // prepare the transaction.class
>>>>>>> f4535db95ae1f985b54f8fbceb6e1d31d8685d26
        if (!Boolean.parseBoolean(forwardMQtoMicroservices(s, PAYMENT_ROUTING_KEY)))
            return false;

        return true;
    }

    @Override
    public void close() throws Exception {
        DTUPayConnection.close();
    }
}
