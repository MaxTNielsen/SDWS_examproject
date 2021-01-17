package reporting.controller;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import reporting.model.TransactionManager;

import javax.enterprise.event.Observes;

public class Controller {
    private static TransactionManager manager;
    private static Controller instance = null;


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

    void onStop(@Observes ShutdownEvent ev)
    {

    }

    void onStart(@Observes StartupEvent ev)
    {

    }

    private void initialization()
    {
        // Initialize the connection
        /*try {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(hostName);
            reportCreatorEventControllerConnection = connectionFactory.newConnection();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }*/

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
