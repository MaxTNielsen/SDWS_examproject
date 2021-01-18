package payment;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.accountmanager.client.ClientFactory;
import org.accountmanager.model.AccountManager;
import org.tokenManagement.messaging.model.TokenValidationRequest;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import io.quarkus.runtime.ShutdownEvent;

@ApplicationScoped
public class MessageParsingPort {
    static Transaction t;
    private static PaymentBL payment = PaymentBL.getInstance();

    static final String TOKEN_MANAGEMENT_QUEUE = "token_management_queue";
    static final String EXCHANGE_NAME = "MICROSERVICES_EXCHANGE";
    // static final String PAYMENT_REQ_QUEUE = "payment_req_queue";
    static final String PAYMENT_RESP_QUEUE = "payment_resp_queue";
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

    public void startConnection() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connection = connectionFactory.newConnection();

        listenDTUPay = connection.createChannel();
        listenDTUPay.exchangeDeclare(EXCHANGE_NAME, "topic");
    }

    public void listen() throws IOException, TimeoutException {
        startConnection();
        listenForPayment();
    }

    public static void listenForPayment() throws TimeoutException {
        try {
            String queueName = listenDTUPay.queueDeclare("payment " + PAYMENT_ROUTING_KEY, false, false, false, null).getQueue();
            listenDTUPay.queueBind(queueName, EXCHANGE_NAME, "payment.#");

            System.out.println("Inside Payment listen method");

            Object monitor = new Object();
            listenDTUPay.queuePurge(queueName);
            listenDTUPay.basicQos(0);
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                String response = "";

                try {
                    Gson gson = new Gson();
                    String message = new String(delivery.getBody(), "UTF-8");

                    //This can handle the date inside Transaction instance
                    Transaction t = gson.fromJson(message, Transaction.class);

                    System.out.println("Inside payment callback");
                    System.out.println("[x] receiving " + t.toString());

                    boolean successful = payment.makeTransaction(t);
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
