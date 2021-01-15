package org.dtupay;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.Json.AccountRegistrationReponse;
import org.accountmanager.model.AccountManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/*@QuarkusMain
implements QuarkusApplication*/
public class DTUPay {

    private static final String CUSTOMER_REG_QUEUE = "CUSTOMER_REG_QUEUE";
    private static final String MERCHANT_REG_QUEUE = "MERCHANT_REG_QUEUE";
    private static final String CUSTOMER_REG_RESPONSE_QUEUE = "CUSTOMER_REG_RESPONSE_QUEUE";
    private static final String MERCHANT_REG_RESPONSE_QUEUE = "MERCHANT_REG_RESPONSE_QUEUE";
    Channel customerRegistrationChannel;
    Channel merchantRegistrationChannel;

    AccountManager m = AccountManager.getInstance();
    ;
    private Map<String, Boolean> accountRegMap = new HashMap<>();
    static DTUPay instance;

    public DTUPay() {
        startUp();
        listenMsgCustomerRegResponse();
        listenMsgMerchantRegResponse();
    }

    public static DTUPay getInstance() {
        if (instance == null)
            return new DTUPay();
        return instance;
    }

    public Map<String, Boolean> getAccountRegMap() {
        return accountRegMap;
    }

    /*@Override
    public int run(String... args) throws Exception {
        *//*System.out.println("Do startup logic here");
        opCustomerRegistratonChannelAndConnection();
        listenMsgCustomerRegResponse();
        listenMsgMerchantRegResponse();
        AccountManager m = AccountManager.getInstance();
        AccountEventController.listen();*//*
        Quarkus.waitForExit();
        return 0;
    }*/

    public void startUp() {
        try {
            ConnectionFactory customerConnectionFactory = new ConnectionFactory();
            customerConnectionFactory.setHost("localhost");
            Connection DTUPayConnection = customerConnectionFactory.newConnection();
            customerRegistrationChannel = DTUPayConnection.createChannel();
            System.out.println("Customer channel created");
            merchantRegistrationChannel = DTUPayConnection.createChannel();
            System.out.println("Merchant channel created");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendMSGToRegCustomer(String ID) throws IOException, TimeoutException {
        //Response r = Response.status(406, "Create account fail").build();
        customerRegistrationChannel.queueDeclare(CUSTOMER_REG_QUEUE, false, false, false, null);
        customerRegistrationChannel.basicPublish("", CUSTOMER_REG_QUEUE, null, ID.getBytes());
        /*channel.close();
        connection.close();*/
    }

    void listenMsgCustomerRegResponse() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection;
        try {
            connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(CUSTOMER_REG_RESPONSE_QUEUE, false, false, false, null);
            System.out.println("register customer reg response queue");
            // channel.exchangeDeclare(EXCHANGE_NAME, QUEUE_TYPE);
            // String queueName = channel.queueDeclare().getQueue();
            // channel.queueBind(queueName, EXCHANGE_NAME, TOPIC);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                Gson gson = new Gson();
                String json = new String(delivery.getBody());
                System.out.println("delivery for the customer: " + json);
                AccountRegistrationReponse r = gson.fromJson(json, AccountRegistrationReponse.class);
                System.out.println("ID PRINT CUSTOMER" + r.ID);
                System.out.println(r.status);
                accountRegMap.put(r.ID, r.status);
            };

            channel.basicConsume("", true, deliverCallback, consumerTag -> {
            });

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMSGToRegMerchant(String ID) throws IOException, TimeoutException {
        /*Response r = Response.status(406, "Create account fail").build();
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");*//**//*
        Connection connection = connectionFactory.newConnection();*//*
        Channel channel = connection.createChannel();*/
        merchantRegistrationChannel.queueDeclare(MERCHANT_REG_QUEUE, false, false, false, null);
        merchantRegistrationChannel.basicPublish("", MERCHANT_REG_QUEUE, null, ID.getBytes());
        /*channel.close();
        connection.close();*/
    }

    void listenMsgMerchantRegResponse() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection;
        try {
            connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(MERCHANT_REG_RESPONSE_QUEUE, false, false, false, null);
            System.out.println("register merchant reg response queue");
            // channel.exchangeDeclare(EXCHANGE_NAME, QUEUE_TYPE);
            // String queueName = channel.queueDeclare().getQueue();
            // channel.queueBind(queueName, EXCHANGE_NAME, TOPIC);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                Gson gson = new Gson();
                String json = new String(delivery.getBody());
                System.out.println("delivery for the merhcant: " + json);
                AccountRegistrationReponse r = gson.fromJson(json, AccountRegistrationReponse.class);
                System.out.println("ID PRINT MERCHANT " + r.ID);
                System.out.println(r.status);
                accountRegMap.put(r.ID, r.status);
            };

            channel.basicConsume("", false, deliverCallback, consumerTag -> {
            }); //changed to false

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}