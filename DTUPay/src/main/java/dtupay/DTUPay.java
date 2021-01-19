package dtupay;

import Utils.Event;
import Utils.TokenGenerationRequest;
import Utils.Transaction;
import accountmanager.model.AccountManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.quarkus.runtime.ShutdownEvent;
import org.tokenManagement.messaging.model.TokenValidationRequest;
import org.tokenManagement.service.TokenManager;
import payment.PaymentBL;
import payment.TokenValidationResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@ApplicationScoped
public class DTUPay implements AutoCloseable 
{

    String hostName = "localhost";
    private static final String TOKEN_ROUTING_KEY = "token.request";
    private static final String EXCHANGE_NAME = "MICROSERVICES_EXCHANGE";
    static final String ACCOUNT_VALIDATION_ROUTING_KEY = "accountmanager.validation.*";
    static final String PAYMENT_ROUTING_KEY = "payment.transaction";

    Connection DTUPayConnection;
    Channel microservicesChannel;
    Channel replyChannel;

    void onStop(@Observes ShutdownEvent ev) {
        try {
            DTUPayConnection.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void close() throws Exception {
        //DTUPayConnection.close();
    }

    //Remove all of these when we split into microservices

    AccountManager m = AccountManager.getInstance();

    TokenManager tokenManager = TokenManager.getInstance();

    PaymentBL payment_service = PaymentBL.getInstance();

    private Map<String, Boolean> accountRegMap = new HashMap<>();

    private Map<String, ArrayList<String>> newTokenMap = new HashMap<>();
    static DTUPay instance;

    public DTUPay() {
        startUp();
    }

    public static DTUPay getInstance() {
        if (instance == null)
            return new DTUPay();
        return instance;
    }

    public void startUp() {
        dtuPayConnection();
        initializeAllChannels();
    }

    void dtuPayConnection() {
        try {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(hostName);
            DTUPayConnection = connectionFactory.newConnection();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    void initializeAllChannels() {
        try {
            microservicesChannel = DTUPayConnection.createChannel();
            replyChannel = DTUPayConnection.createChannel();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    //Main gate for communication with the microservices utilizing a RPC queue
    //All request from the REST API is channel to the microservices by using this queue as a publisher
    //Receives the responses and returns the output to the REST API
    public String forwardMQtoMicroservices(String serializedMessage, String routingKey) {
        String correlateID = UUID.randomUUID().toString();
        try {
            String replyQueueName = replyChannel.queueDeclare("reply " + routingKey, false, false, false, null).getQueue();
            replyChannel.queuePurge(replyQueueName);

            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().correlationId(correlateID)
                    .replyTo(replyQueueName).build();
            microservicesChannel.exchangeDeclare(EXCHANGE_NAME, "topic");
            microservicesChannel.basicPublish(EXCHANGE_NAME, routingKey, properties, serializedMessage.getBytes("UTF-8"));
            final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

            String ctag = microservicesChannel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
                if (delivery.getProperties().getCorrelationId().equals(correlateID)) {
                    response.offer(new String(delivery.getBody(), "UTF-8"));
                }
            }, consumerTag -> {
            });

            String result;
            try {
                result = response.take();
                microservicesChannel.basicCancel(ctag);
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

    private Map<String, Boolean> transactionMap = new HashMap<>();

    public Map<String, Boolean> getTransactionMap() {
        return transactionMap;
    }


    //Method responsible for handling token requests
    public String sendTokenGenerationRequest(TokenGenerationRequest request) {
        System.out.println("Inside sendTokenGenerationRequest " + request.toString());
        Gson gson = new Gson();
        String response = "";
        Object[] objects = new Object[3];
        objects[0] = "c";
        objects[1] = request.getCustomerId();
        objects[2] = request;
        Event e = new Event("TOKEN_GENERATION_REQUEST", objects);
        String serilizedmessage = gson.toJson(e);

        //check whether the ID from the request is valid
        if (!Boolean.parseBoolean(forwardMQtoMicroservices(serilizedmessage, ACCOUNT_VALIDATION_ROUTING_KEY)))
            return response;

        //now ask the token ms for tokens to the associated customer ID
        response = forwardMQtoMicroservices(serilizedmessage, TOKEN_ROUTING_KEY);

        if (response.equals(""))
            return response;

        //response is a TokenGenerationResponse object
        return response;
    }

    //method handling the payments initialized by a merchant through a REST post method
    public boolean DTUPayDoPayment(Transaction t) {
        Gson gson = new Gson();
        TokenValidationRequest tokenValidationRequest = new TokenValidationRequest(t.getToken());
        Object[] objects = new Object[3];
        objects[0] = "m";
        objects[1] = t.getMerchId();
        objects[2] = tokenValidationRequest;
        Event e = new Event("TOKEN_VALIDATION_REQUEST", objects);
        String serilizedmessage = gson.toJson(e);

        //validate the merchant
        if (!Boolean.parseBoolean(forwardMQtoMicroservices(serilizedmessage, ACCOUNT_VALIDATION_ROUTING_KEY)))
            return false;

        //validate token
        String response = forwardMQtoMicroservices(serilizedmessage, TOKEN_ROUTING_KEY);

        Event event = gson.fromJson(response, Event.class);

        String jsonString = gson.toJson(event.getArguments()[0]);
        TokenValidationResponse tokenValidationResponse = gson.fromJson(jsonString, TokenValidationResponse.class);

        t.setCustomId(tokenValidationResponse.getCustomerId());

        if (!tokenValidationResponse.isValid())
            return false;

        String s = gson.toJson(t);

        //payment microservice verifies the transaction
        return Boolean.parseBoolean(forwardMQtoMicroservices(s, PAYMENT_ROUTING_KEY));
    }

    public boolean doRefund(String transactionID)
    {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String setRouting = "reporting.refund";
        Object obj1[] = new Object[]{transactionID};
        Event OriginalTransactionRequest = new Event("GET_TRANSACTION", obj1);
        String OriginalTransactionRequestString = gson.toJson(OriginalTransactionRequest);
        Event response = gson.fromJson(this.forwardMQtoMicroservices(OriginalTransactionRequestString, setRouting), Event.class);
        if (response.getEventType().equals("TRANSACTION_FOUND"))
        {
            Transaction originalTransaction = gson.fromJson(gson.toJson(response.getArguments()[0]), Transaction.class);
            if(originalTransaction.isRefunded())
            {
                System.out.println("Transaction has been refunded before!");
                return false;
            }
            Transaction refundTransaction = originalTransaction.createRefund();
            String s = gson.toJson(refundTransaction);

            //Initiate bank transaction
            boolean bankResult = false;
            try
            {
                bankResult =  Boolean.parseBoolean(forwardMQtoMicroservices(s, PAYMENT_ROUTING_KEY));
            }
            catch (Exception e)
            {
                bankResult = false;
            }
            boolean reported = false;
            if (bankResult)
            {
                String requestType = "NEW_REFUND";
                Object obj[] = new Object[]{transactionID};
                Event request = new Event(requestType, obj);
                String requestString = gson.toJson(request);
                Event reportRefundResponse = gson.fromJson(this.forwardMQtoMicroservices(requestString, setRouting), Event.class);
                if (reportRefundResponse.getEventType().equals("REFUND_REGISTERED"))
                {
                    reported = true;
                }
            }
            if (bankResult && reported)
            {
                return true;
            }
        }
        return false;
    }
}