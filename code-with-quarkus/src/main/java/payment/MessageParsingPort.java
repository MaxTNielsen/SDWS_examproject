package payment;

import com.google.gson.Gson;
import com.rabbitmq.client.*;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.accountmanager.client.ClientFactory;
import org.accountmanager.model.AccountManager;

<<<<<<< HEAD
import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

// @ApplicationScoped
public class MessageParsingPort {
    static Transaction t;

    static final String TOKEN_MANAGEMENT_QUEUE = "token_management_queue";
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

    static void listenForPayment() throws TimeoutException {
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

                    System.out.println("Inside payment callback");
                    System.out.println("[x] receiving " + t.toString());

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

=======
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@ApplicationScoped
public class MessageParsingPort {

	static String hostName = "localhost";
	private static final String EXCHANGE_NAME = "MICROSERVICES_EXCHANGE";
	private static final String TOPIC = "payment.request";
	static Connection paymentConnection;

	void onStart(@Observes StartupEvent ev) {
		PaymentBL.getInstance();
	}

	void onStop(@Observes ShutdownEvent ev) {
		try {
			paymentConnection.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	 
	    public static void listenForPayment(){
		System.out.println("Payment service listening");
	        try {
				ConnectionFactory factory = new ConnectionFactory();
				factory.setHost(hostName);
				paymentConnection = factory.newConnection();
				Channel channel = paymentConnection.createChannel();
				String queueName = channel.queueDeclare().getQueue();
				channel.queueBind(queueName, EXCHANGE_NAME, TOPIC);
	            //listenDTUPay.queueBind(queueName, EXCHANGE_NAME, "payment.#");
				System.out.println("Inside Payment listen method");

	            Object monitor = new Object();
	            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
	                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
	                        .Builder()
	                        .correlationId(delivery.getProperties().getCorrelationId())
	                        .build();

	                String response = "";
	                
	                try {
	                	Gson gson = new Gson();
	                	String message = new String(delivery.getBody(), "UTF-8");
	                	System.out.println("[Payment listening] message: "+ message);
	                	Transaction transaction = gson.fromJson(message ,Transaction.class);
	                	//String requestString = gson.toJson(responseEvent.getArguments()[2]);

	                	//This can handle the date inside Transaction instance
	                	//Transaction t = gson.fromJson(responseEvent.getArguments()[2].toString(), Transaction.class);
	                	//Transaction t = gson.fromJson(requestString,Transaction.class);
		               
		                System.out.println("[x] receiving "+message);

		                boolean successful = PaymentBL.getInstance().makeTransaction(transaction);
		                response = Boolean.toString(successful);
		                System.out.println("[MessageParsingPort] Payment result is: " + response);
	                } catch (RuntimeException e) {
	                    System.out.println(" [.] " + e.toString());
	                } finally {
						channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
						channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

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
	    
>>>>>>> f4535db95ae1f985b54f8fbceb6e1d31d8685d26
}
