package org.dtupay;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
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
    Connection DTUPayConnection;
    Channel customerRegistrationChannel;
    Channel merchantRegistrationChannel;
    Channel customerRegistrationResponseChannel;
    Channel merchantRegistrationResponseChannel;

    AccountManager m = AccountManager.getInstance();

    private Map<String, Boolean> accountRegMap = new HashMap<>();
    static DTUPay instance;

    public DTUPay() {
        startUp();
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
        dtuPayConnection();
        connectRegistration();
        connectListen();
        listenMsgCustomerRegResponse();
        listenMsgMerchantRegResponse();
    }

    public void dtuPayConnection() {
        try {
            ConnectionFactory customerConnectionFactory = new ConnectionFactory();
            customerConnectionFactory.setHost("localhost");
            DTUPayConnection = customerConnectionFactory.newConnection();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void connectRegistration() {
        try {
            customerRegistrationChannel = DTUPayConnection.createChannel();
            System.out.println("Registration customer channel created");
            merchantRegistrationChannel = DTUPayConnection.createChannel();
            System.out.println("Registration merchant channel created");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void connectListen() {
        try {
            customerRegistrationResponseChannel = DTUPayConnection.createChannel();
            System.out.println("Connect customer channel created");
            merchantRegistrationResponseChannel = DTUPayConnection.createChannel();
            System.out.println("Connect merchant channel created");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendMSGToRegCustomer(String ID) throws IOException {
        customerRegistrationChannel.queueDeclare(CUSTOMER_REG_QUEUE, false, false, false, null);
        customerRegistrationChannel.basicPublish("", CUSTOMER_REG_QUEUE, null, ID.getBytes());
    }

    void listenMsgCustomerRegResponse() {
        try {
            customerRegistrationResponseChannel.queueDeclare(CUSTOMER_REG_RESPONSE_QUEUE, false, false, false, null);
            System.out.println("register customer reg response queue");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                Gson gson = new Gson();
                String json = new String(delivery.getBody());
                System.out.println("delivery for the customer: " + json);
                AccountRegistrationReponse r = gson.fromJson(json, AccountRegistrationReponse.class);
                System.out.println("ID PRINT CUSTOMER" + r.ID);
                System.out.println(r.status);
                accountRegMap.put(r.ID, r.status);
            };

            customerRegistrationResponseChannel.basicConsume("", true, deliverCallback, consumerTag -> {
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMSGToRegMerchant(String ID) throws IOException {
        merchantRegistrationChannel.queueDeclare(MERCHANT_REG_QUEUE, false, false, false, null);
        merchantRegistrationChannel.basicPublish("", MERCHANT_REG_QUEUE, null, ID.getBytes());
    }

    void listenMsgMerchantRegResponse() {
        try {
            merchantRegistrationResponseChannel.queueDeclare(MERCHANT_REG_RESPONSE_QUEUE, false, false, false, null);
            System.out.println("register merchant reg response queue");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                Gson gson = new Gson();
                String json = new String(delivery.getBody());
                System.out.println("delivery for the merhcant: " + json);
                AccountRegistrationReponse r = gson.fromJson(json, AccountRegistrationReponse.class);
                System.out.println("ID PRINT MERCHANT " + r.ID);
                System.out.println(r.status);
                accountRegMap.put(r.ID, r.status);
            };

            merchantRegistrationResponseChannel.basicConsume("", true, deliverCallback, consumerTag -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
