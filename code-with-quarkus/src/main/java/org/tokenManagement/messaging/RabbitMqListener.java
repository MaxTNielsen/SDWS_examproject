package org.tokenManagement.messaging;

import com.google.gson.Gson;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;



public class RabbitMqListener {

	private static final String EXCHANGE_NAME = "MICROSERVICES_EXCHANGE";
	private static final String QUEUE_TYPE = "topic";
	private static final String TOPIC = "*.request";

	EventReceiver service;

	public RabbitMqListener(EventReceiver service) {
		this.service = service;
	}

	public void listen() throws Exception {
		//preparation
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		//declare exchange
		channel.exchangeDeclare(EXCHANGE_NAME, QUEUE_TYPE);
		String queueName = channel.queueDeclare().getQueue();
		channel.queueBind(queueName, EXCHANGE_NAME, TOPIC);
		System.out.println("Listening");
		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			String message = new String(delivery.getBody(), "UTF-8");
			System.out.println("[RabbitMqListener] receiving "+message);
			Event event = new Gson().fromJson(message, Event.class);
			try {
				service.receiveEvent(event);
			} catch (Exception e) {
				throw new Error(e);
			} 
		};
		channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
		});
	}
}
