package org.tokenManagement.messaging;

import com.rabbitmq.client.*;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.tokenManagement.service.TokenManager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@ApplicationScoped
public class RabbitMqListener {
    static String hostName = "localhost";
    private static final String EXCHANGE_NAME = "MICROSERVICES_EXCHANGE";
    private static final String TOPIC = "token.request";
    static Connection tokenConnection;

    void onStart(@Observes StartupEvent ev) {
        TokenManager.getInstance();
    }

    void onStop(@Observes ShutdownEvent ev) {
        try {
            tokenConnection.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void listenWithRPCPattern() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(hostName);
            tokenConnection = factory.newConnection();
            Channel channel = tokenConnection.createChannel();
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, TOPIC);
            Object monitor = new Object();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                String response = "";

                try {
                    String request = new String(delivery.getBody(), "UTF-8");
                    System.out.println("Token Service [x] receiving " + request);

                    //call TokenManager to handle request, returns a string of response
                    TokenManager tokenManager = TokenManager.getInstance();
                    response = tokenManager.receiveEvent(request);

                } catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    // RabbitMq consumer worker thread notifies the RPC server owner thread
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };
            channel.basicConsume(queueName, false, deliverCallback, consumerTag -> {
            });
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
