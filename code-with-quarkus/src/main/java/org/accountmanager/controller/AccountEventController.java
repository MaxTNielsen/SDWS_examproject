package org.accountmanager.controller;

import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import io.quarkus.runtime.ShutdownEvent;
import org.Json.AccountRegistrationReponse;
import org.accountmanager.client.ClientFactory;
import org.accountmanager.model.AccountManager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class AccountEventController {

    static String hostName = "localhost";
    static final String CUSTOMER_REG_RESPONSE_QUEUE = "CUSTOMER_REG_RESPONSE_QUEUE";
    static final String MERCHANT_REG_RESPONSE_QUEUE = "MERCHANT_REG_RESPONSE_QUEUE";
    static final String EXCHANGE_NAME = "MICROSERVICES_EXCHANGE";
    static final String CUSTOMER_ROUTING_KEY = "accountmanager.customer";
    static final String MERCHANT_ROUTING_KEY = "accountmanager.merchant";
    static Connection accountEventControllerConnection;
    static Channel customerChannel;
    static Channel customerRegResponseChannel;
    static Channel merchantChannel;
    static Channel merchantRegResponseChannel;

      /*  void onStart(@Observes StartupEvent ev) {
            LOGGER.info("The application is starting...");
        }*/

    void onStop(@Observes ShutdownEvent ev) {
        try {
            accountEventControllerConnection.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void ACEConnection() {
        try {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(hostName);
            accountEventControllerConnection = connectionFactory.newConnection();

            customerChannel = accountEventControllerConnection.createChannel();
            customerChannel.exchangeDeclare(EXCHANGE_NAME, "topic");
            customerRegResponseChannel = accountEventControllerConnection.createChannel();
            customerRegResponseChannel.queueDeclare(CUSTOMER_REG_RESPONSE_QUEUE, false, false, false, null);

            merchantChannel = accountEventControllerConnection.createChannel();
            merchantChannel.exchangeDeclare(EXCHANGE_NAME, "topic");
            merchantRegResponseChannel = accountEventControllerConnection.createChannel();
            merchantRegResponseChannel.queueDeclare(MERCHANT_REG_RESPONSE_QUEUE, false, false, false, null);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void listenToEverything() {
        ACEConnection();
        listenCustomer();
        listenMerchant();
    }

    public static void listenCustomer() {
        try {
            String queueName = customerChannel.queueDeclare().getQueue();
            customerChannel.queueBind(queueName, EXCHANGE_NAME, CUSTOMER_ROUTING_KEY);
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                String ID = message;
                System.out.println("CUSTOMER [x] receiving " + message);
                boolean successful = AccountManager.getInstance().registerCustomer(ClientFactory.buildCustomer(ID));
                sendCustomerRegResponse(ID, successful);
            };
            customerChannel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void sendCustomerRegResponse(String ID, boolean status) {
        try {
            AccountRegistrationReponse r = new AccountRegistrationReponse(ID, status);
            Gson gson = new Gson();
            String s = gson.toJson(r);
            customerRegResponseChannel.basicPublish("", CUSTOMER_REG_RESPONSE_QUEUE, null, s.getBytes("utf-8"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*!!!!!!!!!!!! MERCHANT !!!!!!!!!!!!*/

    public static void listenMerchant() {
        try {
            String queueName = merchantChannel.queueDeclare().getQueue();
            merchantChannel.queueBind(queueName, EXCHANGE_NAME, MERCHANT_ROUTING_KEY);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                String ID = message;
                System.out.println("MERCHANT [x] receiving " + message);

                boolean successful = AccountManager.getInstance().registerCustomer(ClientFactory.buildCustomer(ID));
                sendMerchantRegResponse(ID, successful);
            };
            merchantChannel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void sendMerchantRegResponse(String ID, boolean status) {
        try {
            AccountRegistrationReponse r = new AccountRegistrationReponse(ID, status);
            Gson gson = new Gson();
            String s = gson.toJson(r);
            merchantRegResponseChannel.basicPublish("", MERCHANT_REG_RESPONSE_QUEUE, null, s.getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
