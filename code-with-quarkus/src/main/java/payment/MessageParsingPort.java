package payment;

import com.google.gson.Gson;
import com.rabbitmq.client.*;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.accountmanager.client.ClientFactory;
import org.accountmanager.model.AccountManager;

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
	    
}
