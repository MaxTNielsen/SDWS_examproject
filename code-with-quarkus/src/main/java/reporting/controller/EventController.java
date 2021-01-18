package reporting.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.*;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import reporting.model.*;

import javax.enterprise.event.Observes;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class EventController {
    private TransactionManager transactionManager;
    private static EventController instance = null;
    static String hostName = "localhost";
    static final String EXCHANGE_NAME = "MICROSERVICES_EXCHANGE";
    static final String REPORTING_ROUTING_KEY = "reporting.#";
    static Connection reportCreatorEventControllerConnection;
    static Channel reportChannel;

    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();


    private EventController() {
        transactionManager = TransactionManager.getInstance();
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(hostName);
        try
        {
            reportCreatorEventControllerConnection = connectionFactory.newConnection();
            reportChannel = reportCreatorEventControllerConnection.createChannel();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static EventController getInstance() {
        if (instance == null)
        {
            instance = new EventController();
        }
        return instance;
    }

    void onStart(@Observes StartupEvent ev) throws Exception {
        TransactionManager.getInstance();
        EventController.getInstance();
        EventController.getInstance().listenEvent();
    }

    void onStop(@Observes ShutdownEvent ev) throws IOException {
        reportCreatorEventControllerConnection.close();
    }

    public void listenEvent() {
        try {
            String queueName = reportChannel.queueDeclare().getQueue();
            reportChannel.queueBind(queueName, EXCHANGE_NAME, REPORTING_ROUTING_KEY);
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
                    response = eventHandler(message);
                    responseString = gson.toJson(response);

                } catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                } finally {
                    reportChannel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, responseString.getBytes("UTF-8"));
                    reportChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };
            reportChannel.basicConsume(queueName, false, deliverCallback, consumerTag -> {
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
        else if(message.getEventType().equals("COSTUMER_REPORT"))
        {
            if(obj == null || obj.length < 3)
            {
                System.err.println("Invalid request message! String object does not found");
                response = new Event("INVALID_REQUEST_ERROR");
                return response;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String customerID = (String) obj[0];
            LocalDateTime intervalStart = LocalDateTime.parse((String) obj[1], formatter);
            LocalDateTime intervalEnd = LocalDateTime.parse((String) obj[2], formatter);
            ArrayList<CustomerTransaction> transactions = transactionManager.customerReport(customerID, intervalStart, intervalEnd);
            Object responseObjects[] = new Object[1];
            responseObjects[0] = transactions;
            response = new Event("CUSTOMER_REPORT_RESPONSE", responseObjects);
        }
        else if(message.getEventType().equals("MERCHANT_REPORT"))
        {
            if(obj == null || obj.length < 3)
            {
                System.err.println("Invalid request message! String object does not found");
                response = new Event("INVALID_REQUEST_ERROR");
                return response;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String merchantID = (String) obj[0];
            LocalDateTime intervalStart = LocalDateTime.parse((String) obj[1], formatter);
            LocalDateTime intervalEnd = LocalDateTime.parse((String) obj[2], formatter);
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
            // NOTE:
            // If you put a Transaction instance to the obj[], this is the right way to get it back:
            Transaction newTransaction = gson.fromJson(obj[0].toString(), Transaction.class);
            transactionManager.addTransaction(newTransaction);
            response = new Event("TRANSACTION_REGISTERED");
        }
        else if(message.getEventType().equals("GET_TRANSACTION"))
        {
            if(obj == null || obj.length < 1)
            {
                System.err.println("Invalid request message! String object does not found");
                response = new Event("INVALID_REQUEST_ERROR");
                return response;
            }
            String token = (String) obj[0];
            Transaction result = transactionManager.getTransactionByToken(token);
            Object responseObject[] = new Object[]{(Object) result};
            if(result != null)
            {
                response = new Event("TRANSACTION_FOUND",responseObject);
            }
            else
            {
                response = new Event("TRANSACTION_NOT_FOUND");
            }
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

    public void clearAll ()
    {
        transactionManager.cleanAll();
    }
}
