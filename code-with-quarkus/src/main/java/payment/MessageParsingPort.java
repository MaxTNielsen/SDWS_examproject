package payment;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class MessageParsingPort {
    static Transaction t;
    private static PaymentBL payment = new PaymentBL();

    static final String TOKEN_MANAGEMENT_QUEUE = "token_management_queue";
    static final String PAYMENT_REQ_QUEUE = "payment_req_queue";
    static final String PAYMENT_RESP_QUEUE = "payment_resp_queue";

    public void validateToken(TokenServiceRequestMessage requestMessage, Transaction t) throws IOException, TimeoutException {
        this.t = t;
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare("token_management_queue", false, false, false, null);


        channel.basicPublish("", "token_management_queue", null, requestMessage.toJson().getBytes("UTF-8"));
    }

    public static void listenTokenValidation() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection;
        try {
            connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare("token_management_queue", false, false, false, null);
            // channel.exchangeDeclare(EXCHANGE_NAME, QUEUE_TYPE);
            // String queueName = channel.queueDeclare().getQueue();
            // channel.queueBind(queueName, EXCHANGE_NAME, TOPIC);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                TokenServiceResponseMessage response = new TokenServiceResponseMessage(new String(delivery.getBody(), "UTF-8"));
                ;
                String CID = response.getUserId();
                t.setToken(CID);
                System.out.println("[x] receiving " + CID);

                payment.makeTransaction(response, t);

                try {
                    paymentResponse(t);
                } catch (TimeoutException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
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
    //--------- Payment messages ----------

    public static void paymentResponse(Transaction t) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(PAYMENT_RESP_QUEUE, false, false, false, null);
        Gson gson = new Gson();
        String s = gson.toJson(t);

        channel.basicPublish("", PAYMENT_RESP_QUEUE, null, s.getBytes("UTF-8"));
    }

    public static void listenPaymentRequest() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection;
        try {
            connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare("PAYMENT_REQ_QUEUE", false, false, false, null);
            // channel.exchangeDeclare(EXCHANGE_NAME, QUEUE_TYPE);
            // String queueName = channel.queueDeclare().getQueue();
            // channel.queueBind(queueName, EXCHANGE_NAME, TOPIC);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                Gson gson = new Gson();
                String json = new String(delivery.getBody());
                System.out.println("Transaction: " + json);
                Transaction t = gson.fromJson(json, Transaction.class);

                System.out.println("[x] receiving " + json);

                payment.paymentReq(t);

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
