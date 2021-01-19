package payment;

import java.io.IOException;

import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

// @ApplicationScoped
public class MessageParsingPort {

    static final String EXCHANGE_NAME = "MICROSERVICES_EXCHANGE";
    static final String PAYMENT_ROUTING_KEY = "payment.transaction";
    static Connection connection;
    private static Channel listenDTUPay;
    
    /*void onStop(@Observes ShutdownEvent ev) {
        try {
            connection.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }*/

    static void startConnection() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connection = connectionFactory.newConnection();

        listenDTUPay = connection.createChannel();
        listenDTUPay.exchangeDeclare(EXCHANGE_NAME, "topic");
    }

    public static void listen() throws IOException, TimeoutException {
        startConnection();
        listenForPayment();
    }

    static void listenForPayment() {
        try {
            String queueName = listenDTUPay.queueDeclare("listen payment queue", false, false, false, null).getQueue();
            listenDTUPay.queueBind(queueName, EXCHANGE_NAME, PAYMENT_ROUTING_KEY);

            Object monitor = new Object();
            listenDTUPay.queuePurge(queueName);
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                String response = "";

                try {
                    Gson gson = new Gson();
                    String message = new String(delivery.getBody(), "UTF-8");

                    Transaction t = gson.fromJson(message, Transaction.class);

                    boolean successful = PaymentBL.getInstance().makeTransaction(t);
                    response = Boolean.toString(successful);
                } catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                } finally {

                    listenDTUPay.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    listenDTUPay.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };
            listenDTUPay.basicConsume(queueName, false, deliverCallback, consumerTag -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
