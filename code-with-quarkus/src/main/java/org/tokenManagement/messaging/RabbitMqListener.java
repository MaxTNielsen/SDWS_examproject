package org.tokenManagement.messaging;

import com.google.gson.Gson;

import com.rabbitmq.client.*;
import org.accountmanager.client.ClientFactory;
import org.accountmanager.model.AccountManager;
import org.tokenManagement.service.TokenManager;
import org.tokenManagement.service.TokenManagerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class RabbitMqListener {
	static String hostName = "localhost";
	private static final String EXCHANGE_NAME = "MICROSERVICES_EXCHANGE";
	private static final String QUEUE_TYPE = "topic";
	private static final String TOPIC = "token.request";
	static Connection tokenConnection;

	EventReceiver service;

	public RabbitMqListener(EventReceiver service) {
		this.service = service;
	}

//	public void listen() throws Exception {
//		//preparation
//		ConnectionFactory factory = new ConnectionFactory();
//		factory.setHost(hostName);
//		Connection connection = factory.newConnection();
//		Channel channel = connection.createChannel();
//		//declare exchange
//		channel.exchangeDeclare(EXCHANGE_NAME, QUEUE_TYPE);
//		String queueName = channel.queueDeclare().getQueue();
//		channel.queueBind(queueName, EXCHANGE_NAME, TOPIC);
//		System.out.println("Listening");
//		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//			String message = new String(delivery.getBody(), "UTF-8");
//			//System.out.println("[RabbitMqListener] receiving "+message);
//			Event event = new Gson().fromJson(message, Event.class);
//			try {
//				service.receiveEvent(event);
//			} catch (Exception e) {
//				throw new Error(e);
//			}
//		};
//		channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
//		});
//	}
	public static void listenWithRPCPattern() {
		try {
			//preparation
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
					//recieve request
					String request = new String(delivery.getBody(), "UTF-8");
					System.out.println("Token Service [x] receiving " + request);

					//call TokenService to handle request, returns a string of response
					TokenManager tokenManager = new TokenManagerFactory().getService();
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
			channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
			});
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
	}
}
