package org.tokenManagement.service;

import com.rabbitmq.client.*;
import org.tokenManagement.model.Token;
import org.tokenManagement.model.TokenServiceRequestMessage;
import org.tokenManagement.model.TokenServiceResponseMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class NetworkModule {
    private static final String RPC_QUEUE_NAME = "token_management_queue";
    private static ConnectionFactory factory;
    TokenManager manager;

    public NetworkModule () {
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        manager = TokenManager.getInstance();
    }

    public TokenServiceResponseMessage requestHandler(TokenServiceRequestMessage request)
    {
        TokenServiceResponseMessage responseMessage = null;
        System.out.println("received");
        switch (request.getType())
        {
            case GET_VALIDITY:
                boolean result = manager.validateToken(request.getToken());
                responseMessage = new TokenServiceResponseMessage(TokenServiceResponseMessage.tokenServiceResponseMessageType.RESPONSE_GET_VALIDITY,result);
                break;
            case GET_ALL_TOKENS:
                ArrayList<Token> tokens = new ArrayList<Token>(manager.tokens.values());
                ArrayList<String> tokenIDs = new ArrayList<String>();
                for(Token currentToken:tokens)
                {
                    tokenIDs.add(currentToken.getId());
                }
                responseMessage = new TokenServiceResponseMessage(TokenServiceResponseMessage.tokenServiceResponseMessageType.RESPONSE_GET_ALL_TOKENS, tokenIDs);
                break;
            case REQUEST_NEW_TOKENS:
                ArrayList<String> newTokenIds = manager.getNewTokens(request.getUserCpr());
                responseMessage = new TokenServiceResponseMessage(TokenServiceResponseMessage.tokenServiceResponseMessageType.RESPONSE_NEW_TOKENS,newTokenIds);
                break;
        }
        return responseMessage;
    }

    //Funcion to start RabbitMQ server
    public void runNetworkModule() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
            channel.queuePurge(RPC_QUEUE_NAME);

            channel.basicQos(1);

            System.out.println(" [x] Awaiting RPC requests");

            Object monitor = new Object();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                TokenServiceResponseMessage responseMessage = null;
                System.out.println("new");
                try {
                    TokenServiceRequestMessage message = new TokenServiceRequestMessage(new String(delivery.getBody(), "UTF-8"));
                    System.out.println("Request received:" + message.toString());
                    responseMessage = this.requestHandler(message);
                } catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                } finally {
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, responseMessage.toJson().getBytes("UTF-8"));
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    // RabbitMq consumer worker thread notifies the RPC server owner thread
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };

            channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> { }));
            // Wait and be prepared to consume the message from RPC client.
            while (true) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}