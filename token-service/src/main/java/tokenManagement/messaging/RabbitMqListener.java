package tokenManagement.messaging;

import com.google.gson.Gson;
import com.rabbitmq.client.*;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import tokenManagement.messaging.model.Event;
import tokenManagement.service.TokenManager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@ApplicationScoped
public class RabbitMqListener {
    static String hostName = "rabbitMq";
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

					//convert received string to event
					Gson gson = new Gson();
					Event event = gson.fromJson(request,Event.class);

					//call TokenManager to handle request, returns a string of response
					TokenManager tokenManager = TokenManager.getInstance();
					Event response_event = tokenManager.receiveEvent(event);

					//convert response event to string
					if (response_event != null)
						response = gson.toJson(response_event);


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
