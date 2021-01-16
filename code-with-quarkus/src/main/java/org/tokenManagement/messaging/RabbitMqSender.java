package org.tokenManagement.messaging;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;



public class RabbitMqSender implements EventSender {

	private static final String EXCHANGE_NAME = "MICROSERVICES_EXCHANGE";
	private static final String QUEUE_TYPE = "topic";
	private static final String TOPIC = "token.response";

	@Override
	public void sendEvent(Event event) throws Exception {
		//preparation
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
			//declare exchange
			channel.exchangeDeclare(EXCHANGE_NAME, QUEUE_TYPE);
			String message = new Gson().toJson(event);
			System.out.println("[RabbitMqSender] sending "+message);
			channel.basicPublish(EXCHANGE_NAME, TOPIC, null, message.getBytes("UTF-8"));
		}
	}

}