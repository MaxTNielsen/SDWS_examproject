package reporting.controller;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import payment.Transaction;
import reporting.model.TransactionManager;

public class Controller {
    private static TransactionManager manager;
    private static Controller instance = null;

    static String hostName = "localhost";
    static final String CUSTOMER_REG_RESPONSE_QUEUE = "CUSTOMER_REG_RESPONSE_QUEUE";
    static final String MERCHANT_REG_RESPONSE_QUEUE = "MERCHANT_REG_RESPONSE_QUEUE";
    static final String MANAGER_REG_RESPONSE_QUEUE = "MANAGER_REG_RESPONSE_QUEUE";

    static final String EXCHANGE_NAME = "MICROSERVICES_EXCHANGE";

    static final String CUSTOMER_ROUTING_KEY = "reporting.customer";
    static final String MERCHANT_ROUTING_KEY = "reporting.merchant";
    static final String MANAGER_ROUTING_KEY = "reporting.manager";

    static Connection reportCreatorEventControllerConnection;

    static Channel customerChannel;
    static Channel customerRegResponseChannel;
    static Channel merchantChannel;
    static Channel merchantRegResponseChannel;
    static Channel managerChannel;
    static Channel managerRegResponseChannel;


    private Controller ()
    {
        manager = TransactionManager.getInstance();
    }

    public Controller getInstance()
    {
        if(instance == null)
        {
            instance = new Controller();
        }
        return instance;
    }

    private void initialization()
    {
        // Initialize the connection
        try {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(hostName);
            reportCreatorEventControllerConnection = connectionFactory.newConnection();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Initialize channels
        /*try {
            paymentChannel = DTUPayConnection.createChannel();
            System.out.println("Payment channel created");
            microservicesChannel = DTUPayConnection.createChannel();
            System.out.println("Topic channel initialized");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }*/
    }







}
