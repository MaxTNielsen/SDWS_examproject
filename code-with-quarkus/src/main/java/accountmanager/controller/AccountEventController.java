package accountmanager.controller;

import com.google.gson.Gson;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import Utils.Event;
import accountmanager.client.ClientFactory;
import accountmanager.model.AccountManager;
import io.quarkus.runtime.ShutdownEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import java.io.IOException;

@ApplicationScoped
public class AccountEventController implements AutoCloseable {

    static String hostName = "localhost";
    static final String EXCHANGE_NAME = "MICROSERVICES_EXCHANGE";
    static final String CUSTOMER_REG_ROUTING_KEY = "accountmanager.customer.registration";
    static final String MERCHANT_REG_ROUTING_KEY = "accountmanager.merchant.registration";
    static final String ACCOUNT_VALIDATION_ROUTING_KEY = "accountmanager.validation.*";
    static Connection accountEventControllerConnection;
    static Channel customerRegChannel;
    static Channel merchantRegChannel;
    static Channel accountValidationChannel;


    void onStart(@Observes ShutdownEvent ev) {
         listenToEverything();
     }

    void onStop(@Observes ShutdownEvent ev) {
       try {
            accountEventControllerConnection.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
     }

    @Override
    public void close() throws Exception {
        //accountEventControllerConnection.close();
    }

    //Initialize connections and channels
    private static void ACEConnection() {
        try {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(hostName);
            accountEventControllerConnection = connectionFactory.newConnection();

            customerRegChannel = accountEventControllerConnection.createChannel();
            customerRegChannel.exchangeDeclare(EXCHANGE_NAME, "topic");

            merchantRegChannel = accountEventControllerConnection.createChannel();
            merchantRegChannel.exchangeDeclare(EXCHANGE_NAME, "topic");

            accountValidationChannel = accountEventControllerConnection.createChannel();
            accountValidationChannel.exchangeDeclare(EXCHANGE_NAME, "topic");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //Make sure all MQs are initialized
    public static void listenToEverything() {
        ACEConnection();
        listenCustomer();
        listenMerchant();
        listenAccountIDValidation();
    }

    static void listenCustomer() {
        try {
            String queueName = customerRegChannel.queueDeclare("customer  reg queue", false, false, false, null).getQueue();
            customerRegChannel.queueBind(queueName, EXCHANGE_NAME, CUSTOMER_REG_ROUTING_KEY);
            Object monitor = new Object();
            customerRegChannel.queuePurge(queueName);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                String response = "";

                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                    String ID = message;
                    //System.out.println("CUSTOMER [x] receiving " + message);

                    //Attempt to register Customer by ID
                    boolean successful = AccountManager.getInstance().registerCustomer(ClientFactory.buildCustomer(ID));
                    response = Boolean.toString(successful);
                } catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                } finally {
                    customerRegChannel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    customerRegChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };
            customerRegChannel.basicConsume(queueName, false, deliverCallback, consumerTag -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*!!!!!!!!!!!! MERCHANT !!!!!!!!!!!!*/

    static void listenMerchant() {
        try {
            String queueName = merchantRegChannel.queueDeclare("merchant reg queue", false, false, false, null).getQueue();
            // String queueName = merchantRegChannel.queueDeclare().getQueue();
            merchantRegChannel.queueBind(queueName, EXCHANGE_NAME, MERCHANT_REG_ROUTING_KEY);
            customerRegChannel.queuePurge(queueName);
            Object monitor = new Object();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                String response = "";

                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                    String ID = message;
                    //System.out.println("Merchant [x] receiving " + message);

                    //Attempt to register Merchant by ID
                    boolean successful = AccountManager.getInstance().registerMerchant(ClientFactory.buildMerchant(ID));
                    response = Boolean.toString(successful);
                } catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                } finally {
                    merchantRegChannel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    merchantRegChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };

            merchantRegChannel.basicConsume(queueName, false, deliverCallback, consumerTag -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void listenAccountIDValidation() {
        try {
            String queueName = accountValidationChannel.queueDeclare().getQueue();
            accountValidationChannel.queueBind(queueName, EXCHANGE_NAME, ACCOUNT_VALIDATION_ROUTING_KEY);

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
                    Event e = gson.fromJson(json, Event.class);
                    String ID = e.getArguments()[1].toString();
                    System.out.println("Inside ID validation: " + ID);
                    boolean ans;

                    //Two branches - either its a merchant ID or a customer ID
                    if (e.getArguments()[0].toString().equals("m")) {
                        ans = AccountManager.getInstance().hasMerchant(ID);
                    } else {
                        ans = AccountManager.getInstance().hasCustomer(ID);
                    }

                    response = Boolean.toString(ans);

                } catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                } finally {
                    accountValidationChannel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    accountValidationChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };
            accountValidationChannel.basicConsume(queueName, false, deliverCallback, consumerTag -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}