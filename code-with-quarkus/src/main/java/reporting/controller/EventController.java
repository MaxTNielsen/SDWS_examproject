package reporting.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import io.cucumber.java.an.E;
import reporting.model.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class EventController {
    private TransactionManager transactionManager;
    private static EventController instance = null;

    static String hostName = "localhost";
    static final String EXCHANGE_NAME = "MICROSERVICES_EXCHANGE";

    static final String CUSTOMER_ROUTING_KEY = "reporting.customer";
    static final String MERCHANT_ROUTING_KEY = "reporting.merchant";
    static final String MANAGER_ROUTING_KEY = "reporting.manager";

    static Connection reportCreatorEventControllerConnection;

    static Channel customerChannel;
    static Channel merchantChannel;
    static Channel managerChannel;


    private EventController()
    {
        transactionManager = TransactionManager.getInstance();
    }

    public static EventController getInstance() {
        if (instance == null)
        {
            instance = new EventController();
        }
        return instance;
    }

    public void listenEvent() {
        try {
            String queueName = customerChannel.queueDeclare().getQueue();
            customerChannel.queueBind(queueName, EXCHANGE_NAME, CUSTOMER_ROUTING_KEY);
            Object monitor = new Object();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                Event response = new Event();
                String responseString = "";

                try {
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    Event message = gson.fromJson(new String(delivery.getBody(), "UTF-8"), Event.class);
                    System.out.println("CUSTOMER [x] receiving " + new String(delivery.getBody(), "UTF-8"));
                    response = eventHandler(message);
                    responseString = gson.toJson(response);

                } catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                } finally {
                    customerChannel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, responseString.getBytes("UTF-8"));
                    customerChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    // RabbitMq consumer worker thread notifies the RPC server owner thread
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };
            customerChannel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Event eventHandler(Event message)
    {
        Object obj[] =  message.getArguments();
        Event response = null;
        if(message.getEventType().equals("MANAGER_REPORT"))
        {
            ArrayList<Transaction> transactions = transactionManager.managerReport();
            Object responseObjects[] = new Object[1];
            responseObjects[0] = transactions;
            response = new Event("MANAGER_REPORT_RESPONSE", responseObjects);
        }
        else if(message.getEventType().equals("MANAGER_MONEY_FLOW"))
        {
            int moneyFlow = transactionManager.managerMoneyFlowReport();
            Object responseObjects[] = new Object[1];
            responseObjects[0] = moneyFlow;
            response = new Event("MANAGER_MONEY_FLOW", responseObjects);
        }
        else if(message.getEventType().equals("CONSUMER_REPORT"))
        {
            if(obj == null || obj.length < 1)
            {
                System.err.println("Invalid request message! String object does not found");
                response = new Event("INVALID_REQUEST_ERROR");
                return response;
            }
            String customerID = (String) obj[0];
            LocalDateTime intervalStart = (LocalDateTime) obj[1];
            LocalDateTime intervalEnd = (LocalDateTime) obj[2];
            ArrayList<CustomerTransaction> transactions = transactionManager.customerReport(customerID, intervalStart, intervalEnd);
            Object responseObjects[] = new Object[1];
            responseObjects[0] = transactions;
            response = new Event("CUSTOMER_REPORT_RESPONSE", responseObjects);
        }
        else if(message.getEventType().equals("MERCHANT_REPORT"))
        {
            if(obj == null || obj.length < 1)
            {
                System.err.println("Invalid request message! String object does not found");
                response = new Event("INVALID_REQUEST_ERROR");
                return response;
            }
            String merchantID = (String) obj[0];
            LocalDateTime intervalStart = (LocalDateTime) obj[1];
            LocalDateTime intervalEnd = (LocalDateTime) obj[2];
            ArrayList<MerchantTransaction> transactions = transactionManager.merchantReport(merchantID, intervalStart, intervalEnd);
            Object responseObjects[] = new Object[1];
            responseObjects[0] = transactions;
            response = new Event("MERCHANT_REPORT_RESPONSE", responseObjects);
        }
        else if(message.getEventType().equals("NEW_TRANSACTION"))
        {
            if(obj == null || obj.length < 1)
            {
                System.err.println("Invalid request message! Transaction object does not found");
                response = new Event("INVALID_REQUEST_ERROR");
                return response;
            }
            Transaction newTransaction = (Transaction) obj[0];
            transactionManager.addTransaction(newTransaction);
            response = new Event("TRANSACTION_REGISTERED");
        }
        else if(message.getEventType().equals("NEW_REFUND"))
        {
            if(obj == null || obj.length < 1)
            {
                System.err.println("Invalid request message! String object does not found");
                response = new Event("INVALID_REQUEST_ERROR");
                return response;
            }
            String token = (String) obj[0];
            transactionManager.refundTransaction(token);
            response = new Event("REFUND_REGISTERED");
        }
        else
        {
            response = new Event("INVALID_TYPE_ERROR");
            System.err.println("Invalid request type: " + message.getEventType());
        }
        return response;
    }
}
