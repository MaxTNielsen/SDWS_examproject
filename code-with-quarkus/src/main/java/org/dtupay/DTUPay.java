package org.dtupay;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;


import io.quarkus.runtime.ShutdownEvent;
import org.Json.*;

import org.accountmanager.model.AccountManager;
import org.tokenManagement.service.TokenManager;
import org.tokenManagement.service.TokenManagerFactory;

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
public class DTUPay {

    String hostName = "localhost";
    private static final String PAYMENT_REQ_QUEUE = "payment_req_queue";
    private static final String PAYMENT_RESP_QUEUE = "payment_resp_queue";
    private static final String TOKEN_ROUTING_KEY = "token.request";
    private static final String EXCHANGE_NAME = "MICROSERVICES_EXCHANGE";


    Connection DTUPayConnection;
    Channel paymentChannel;
    Channel paymentResponseChannel;
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
    //to start up token service
    TokenManager token_service = new TokenManagerFactory().getService();

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
            paymentChannel = DTUPayConnection.createChannel();
            System.out.println("Payment channel created");
            microservicesChannel = DTUPayConnection.createChannel();
            replyChannel = DTUPayConnection.createChannel();
            System.out.println("Topic channel initialized");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public String forwardMQtoMicroservices(String message, String routingKey) {
        String correlateID = UUID.randomUUID().toString();
        try {
            String replyQueueName = replyChannel.queueDeclare("reply "+routingKey, false, true, false, null).getQueue();
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().correlationId(correlateID)
                    .replyTo(replyQueueName).build();
            microservicesChannel.exchangeDeclare(EXCHANGE_NAME, "topic");
            // System.out.println("DTU publish: " + message);
            // System.out.println("replyQueueName: " + replyQueueName);
            
            // System.out.println("routingKey: " + routingKey);
            microservicesChannel.basicPublish(EXCHANGE_NAME, routingKey, properties, message.getBytes("UTF-8"));
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
                System.out.println("result: " + result);
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

    public void sendPaymentRequest(Transaction t) throws IOException {
        paymentChannel.queueDeclare(PAYMENT_REQ_QUEUE, false, false, false, null);
        Gson gson = new Gson();
        String s = gson.toJson(t);
        paymentChannel.basicPublish("", PAYMENT_REQ_QUEUE, null, s.getBytes());
    }

    void listenPaymentResponse() {
        try {
            paymentResponseChannel.queueDeclare(PAYMENT_RESP_QUEUE, false, false, false, null);
            System.out.println("payment response queue");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                Gson gson = new Gson();
                String json = new String(delivery.getBody());
                System.out.println("Transaction: " + json);
                Transaction t = gson.fromJson(json, Transaction.class);
                System.out.println("Token" + t.getToken());
                System.out.println("Transaction was " + t.isApproved());
                transactionMap.put(t.getToken(), t.isApproved());
            };

            paymentResponseChannel.basicConsume("", true, deliverCallback, consumerTag -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sendTokenGenerationRequest(TokenGenerationRequest request) throws IOException {
        String response = "";
        Event event = new Event("TOKEN_GENERATION_REQUEST", new Object[] { request });
        String message = new Gson().toJson(event);
        response = forwardMQtoMicroservices(message, TOKEN_ROUTING_KEY);
        System.out.println("DTU pay get response:" + response);
        return response;
    }


}
