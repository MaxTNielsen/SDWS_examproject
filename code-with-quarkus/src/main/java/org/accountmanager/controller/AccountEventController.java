package org.accountmanager.controller;

import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import org.Json.AccountRegistrationReponse;
import org.accountmanager.client.ClientFactory;
import org.accountmanager.model.AccountManager;

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
            Object monitor = new Object();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                String response = "";

                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                    String ID = message;
                    System.out.println("CUSTOMER [x] receiving " + message);
                    // response += fib(n);
                    boolean successful = AccountManager.getInstance().registerCustomer(ClientFactory.buildCustomer(ID));
                    response = Boolean.toString(successful);
                } catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                } finally {
                    customerChannel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    customerChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    // RabbitMq consumer worker thread notifies the RPC server owner thread
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };
            customerChannel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*!!!!!!!!!!!! MERCHANT !!!!!!!!!!!!*/

    public static void listenMerchant() {
        try {
            String queueName = merchantChannel.queueDeclare().getQueue();
            merchantChannel.queueBind(queueName, EXCHANGE_NAME, MERCHANT_ROUTING_KEY);

            Object monitor = new Object();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                String response = "";

                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                    String ID = message;
                    System.out.println("Merchant [x] receiving " + message);
                    // response += fib(n);
                    boolean successful = AccountManager.getInstance().registerMerchant(ClientFactory.buildMerchant(ID));
                    response = Boolean.toString(successful);
                } catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                } finally {
                    merchantChannel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    merchantChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    // RabbitMq consumer worker thread notifies the RPC server owner thread
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };
            
            merchantChannel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
