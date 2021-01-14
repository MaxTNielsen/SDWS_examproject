package org.accountmanager.controller;

import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import org.accountmanager.client.ClientFactory;
import org.accountmanager.model.AccountManager;

public class AccountEventController {
    static final String CUSTOMER_REG_QUEUE = "CUSTOMER_REG_QUEUE";


    public static void listen()
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
                System.out.println("[x] receiving "+message);

                AccountManager.getInstance().registerCustomer(ClientFactory.buildCustomer(ID));
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
