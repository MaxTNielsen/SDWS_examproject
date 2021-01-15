package org.accountmanager.controller;

import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import org.Json.AccountRegistrationReponse;
import org.accountmanager.client.ClientFactory;
import org.accountmanager.model.AccountManager;

public class AccountEventController {
    static final String CUSTOMER_REG_QUEUE = "CUSTOMER_REG_QUEUE";
    static final String CUSTOMER_REG_RESPONSE_QUEUE = "CUSTOMER_REG_RESPONSE_QUEUE";
    static final String MERCHANT_REG_QUEUE = "MERCHANT_REG_QUEUE";
    static final String MERCHANT_REG_RESPONSE_QUEUE = "MERCHANT_REG_RESPONSE_QUEUE";

    public static void listenCustomer()
    {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
		Connection connection;
        try {
            connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
        
            channel.queueDeclare(CUSTOMER_REG_QUEUE, false, false, false, null);
            // channel.exchangeDeclare(EXCHANGE_NAME, QUEUE_TYPE);
            // String queueName = channel.queueDeclare().getQueue();
            // channel.queueBind(queueName, EXCHANGE_NAME, TOPIC);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                String ID = message;
                System.out.println("CUSTOMER [x] receiving "+message);

                boolean successful = AccountManager.getInstance().registerCustomer(ClientFactory.buildCustomer(ID));
                sendCustomerRegResponse(ID, successful);
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

    static void sendCustomerRegResponse(String ID, boolean status)
    {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
		Connection connection;
        try {
            connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
        
            channel.queueDeclare(CUSTOMER_REG_RESPONSE_QUEUE, false, false, false, null);

            AccountRegistrationReponse r = new AccountRegistrationReponse(ID, status);
            // JsonObject jsonObject = new JsonObject();
            Gson gson = new Gson();
            String s = gson.toJson(r);
            channel.basicPublish("", CUSTOMER_REG_RESPONSE_QUEUE, null, s.getBytes("utf-8"));
            // channel.addConfirmListener(ackCallback, nackCallback)
            /*channel.close();
            connection.close();*/
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    /*!!!!!!!!!!!! MERCHANT !!!!!!!!!!!!*/

    public static void listenMerchant()
    {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection;
        try {
            connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(MERCHANT_REG_QUEUE, false, false, false, null);
            // channel.exchangeDeclare(EXCHANGE_NAME, QUEUE_TYPE);
            // String queueName = channel.queueDeclare().getQueue();
            // channel.queueBind(queueName, EXCHANGE_NAME, TOPIC);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                String ID = message;
                System.out.println("MERCHANT [x] receiving "+message);

                boolean successful = AccountManager.getInstance().registerCustomer(ClientFactory.buildCustomer(ID));
                sendMerchantRegResponse(ID, successful);
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


    static void sendMerchantRegResponse(String ID, boolean status)
    {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection;
        try {
            connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(MERCHANT_REG_RESPONSE_QUEUE, false, false, false, null);

            AccountRegistrationReponse r = new AccountRegistrationReponse(ID, status);
            // JsonObject jsonObject = new JsonObject();
            Gson gson = new Gson();
            String s = gson.toJson(r);
            channel.basicPublish("", MERCHANT_REG_RESPONSE_QUEUE, null, s.getBytes("utf-8"));
            // channel.addConfirmListener(ackCallback, nackCallback)
            /*channel.close();
            connection.close();*/
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
