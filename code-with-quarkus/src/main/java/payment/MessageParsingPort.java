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
	private static PaymentBL payment = new PaymentBL();
	
    static final String TOKEN_MANAGEMENT_QUEUE = "token_management_queue";
    static final String EXCHANGE_NAME = "MICROSERVICES_EXCHANGE";
   // static final String PAYMENT_REQ_QUEUE = "payment_req_queue";
    static final String PAYMENT_RESP_QUEUE = "payment_resp_queue";
    static Connection connection;
    
    void onStop(@Observes ShutdownEvent ev) {
        try {
            connection.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    


    
    public String requestTokenValidation(String message) throws TimeoutException {
        String correlateID = UUID.randomUUID().toString();
        try {
        	ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost("localhost");
    		Connection connection = connectionFactory.newConnection();
            Channel replyChannel = connection.createChannel();
            Channel channel = connection.createChannel();
            
            String replyQueueName = replyChannel.queueDeclare().getQueue();
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().correlationId(correlateID)
                    .replyTo(replyQueueName).build();
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            channel.basicPublish(EXCHANGE_NAME, "token.request", properties, message.getBytes("UTF-8"));
            final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

            channel.queueDeclare("token_management_queue", false, false, false, null);
            // channel.exchangeDeclare(EXCHANGE_NAME, QUEUE_TYPE);
            // String queueName = channel.queueDeclare().getQueue();
            // channel.queueBind(queueName, EXCHANGE_NAME, TOPIC);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                TokenServiceResponseMessage response = new TokenServiceResponseMessage(new String(delivery.getBody(), "UTF-8"));
                ;
                String CID = response.getUserId();
                t.setTokenID(CID);
                System.out.println("[x] receiving " + CID);

                payment.makeTransaction(response, t);

                try {
                    paymentResponse(t);
                } catch (TimeoutException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

            String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
                if (delivery.getProperties().getCorrelationId().equals(correlateID)) {
                    response.offer(new String(delivery.getBody(), "UTF-8"));
                }
            }, consumerTag -> {
            });

            String result;
            try {
                result = response.take();
                channel.basicCancel(ctag);
                System.out.println("result: " + result);
                return result;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
	
//	public void validateToken(TokenServiceRequestMessage requestMessage, Transaction t) throws IOException, TimeoutException
//    {
//		this.t = t;
//        ConnectionFactory connectionFactory = new ConnectionFactory();
//        connectionFactory.setHost("localhost");
//		Connection connection = connectionFactory.newConnection();
//        Channel channel = connection.createChannel();
//        
//        channel.queueDeclare("token_management_queue", false, false, false, null);
//
//        
//        channel.basicPublish("", "token_management_queue", null, requestMessage.toJson().getBytes( "UTF-8"));
//    }
	
	
//	 public static void listenTokenValidation()
//	    {
//	        ConnectionFactory connectionFactory = new ConnectionFactory();
//	        connectionFactory.setHost("localhost");
//			Connection connection;
//	        try {
//	            connection = connectionFactory.newConnection();
//	            Channel channel = connection.createChannel();
//	        
//	            channel.queueDeclare("token_management_queue", false, false, false, null);
//	            // channel.exchangeDeclare(EXCHANGE_NAME, QUEUE_TYPE);
//	            // String queueName = channel.queueDeclare().getQueue();
//	            // channel.queueBind(queueName, EXCHANGE_NAME, TOPIC);
//
//	            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//	            	TokenServiceResponseMessage response = new TokenServiceResponseMessage(new String(delivery.getBody(), "UTF-8"));;
//	                String CID = response.getUserId();
//	                t.setToken(CID);
//	                System.out.println("[x] receiving "+CID);
//	                
//	                payment.makeTransaction(response, t);
//	                
//	                try {
//						paymentResponse(t);
//					} catch (TimeoutException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//	                
//	            };
//	            
//
//	            // channel.basicPublish("", CUSTOMER_REG_QUEUE, null, message.getBytes());
//	            channel.basicConsume("", true, deliverCallback, consumerTag -> {
//	            });
//	        } catch (IOException e) {
//	            // TODO Auto-generated catch block
//	            e.printStackTrace();
//	        } catch (TimeoutException e) {
//	            // TODO Auto-generated catch block
//	            e.printStackTrace();
//	        }
//	        
//
//	    }
	 
	 
	 //--------- Payment messages ----------

	 
//		public static void paymentResponse(Transaction t) throws IOException, TimeoutException
//	    {			
//	        ConnectionFactory connectionFactory = new ConnectionFactory();
//	        connectionFactory.setHost("localhost");
//			Connection connection = connectionFactory.newConnection();
//	        Channel channel = connection.createChannel();
//	        
//	        channel.queueDeclare(PAYMENT_RESP_QUEUE, false, false, false, null);
//	        
//	        Gson gson = new Gson();
//          String s = gson.toJson(t);
//	        
//	        channel.basicPublish("", PAYMENT_RESP_QUEUE, null, s.getBytes( "UTF-8"));
//	    }

	 
	    public static void listenMerchant() throws TimeoutException {
	        try {
	        	ConnectionFactory connectionFactory = new ConnectionFactory();
		        connectionFactory.setHost("localhost");
				Connection connection;
		        connection = connectionFactory.newConnection();
		        Channel channel = connection.createChannel();
	            String queueName = channel.queueDeclare().getQueue();
	            channel.queueBind(queueName, EXCHANGE_NAME, "payment.*");

	            Object monitor = new Object();
	            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
	                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
	                        .Builder()
	                        .correlationId(delivery.getProperties().getCorrelationId())
	                        .build();

	                String response = "";
	                
	                try {
	                	Gson gson = new Gson();
		                String json = new String(delivery.getBody());
		                System.out.println("Transaction: " + json);
		                Transaction t = gson.fromJson(json, Transaction.class);
		            	
		                System.out.println("[x] receiving "+json);
		                
		                Boolean successful = payment.paymentRequest(t);
		                response = Boolean.toString(successful);
	                } catch (RuntimeException | TimeoutException e) {
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
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	 
	 
//	 public static void listenPaymentRequest()
//	    {
//	        ConnectionFactory connectionFactory = new ConnectionFactory();
//	        connectionFactory.setHost("localhost");
//			Connection connection;
//	        try {
//	            connection = connectionFactory.newConnection();
//	            Channel channel = connection.createChannel();
//	        
//	            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
//	            // channel.exchangeDeclare(EXCHANGE_NAME, QUEUE_TYPE);
//	            String queueName = channel.queueDeclare().getQueue();
//	            System.out.println(queueName);
//	            channel.queueBind(queueName, EXCHANGE_NAME, "payment");
//	            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//	            	Gson gson = new Gson();
//	                String json = new String(delivery.getBody());
//	                System.out.println("Transaction: " + json);
//	                Transaction t = gson.fromJson(json, Transaction.class);
//	            	
//	                System.out.println("[x] receiving "+json);
//	                
//	                payment.paymentReq(t);
//	                 
//	            };
//	            // channel.basicPublish("", CUSTOMER_REG_QUEUE, null, message.getBytes());
//	            channel.basicConsume("", true, deliverCallback, consumerTag -> {
//	            });
//	        } catch (IOException e) {
//	            // TODO Auto-generated catch block
//	            e.printStackTrace();
//	        } catch (TimeoutException e) {
//	            // TODO Auto-generated catch block
//	            e.printStackTrace();
//	        }
//	        
//
//	    }


}
