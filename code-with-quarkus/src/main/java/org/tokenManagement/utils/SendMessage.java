package org.tokenManagement.utils;
import com.google.gson.Gson;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import org.tokenManagement.messaging.*;

import java.nio.charset.StandardCharsets;

public class SendMessage {

    private static final String EXCHANGE_NAME = "MICROSERVICES_EXCHANGE";
    private static final String QUEUE_TYPE = "topic";
    private static final String TOPIC = "token.request";

    //for testing 
    public static void main(String[] argv) throws Exception {
    	//prepare request message
        TokenGenerationRequest request = new TokenGenerationRequest("2231251",1);
        Event event = new Event("TOKEN_GENERATION_REQUEST", new Object[] { request });
//	    TokenValidationRequest request = new TokenValidationRequest("123");
//	    Event event = new Event("TOKEN_VALIDATION_REQUEST", new Object[] { request });
        
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            //declare exchange
            channel.exchangeDeclare(EXCHANGE_NAME, QUEUE_TYPE);
            String message = new Gson().toJson(event);
            System.out.println("[x] sending "+message);
            channel.basicPublish(EXCHANGE_NAME, TOPIC, null, message.getBytes("UTF-8"));
        }
    }

}