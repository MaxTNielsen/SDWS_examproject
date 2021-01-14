package org.CustomerREST;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/customers")
public class CustomerREST {
    private static final String CUSTOMER_REG_QUEUE = "CUSTOMER_REG_QUEUE";

    public static void main(String[] args) throws IOException, TimeoutException {
        CustomerREST s = new CustomerREST();
        s.sendMsgCustomerReg("2f04f47f-6d0f-413b-b85c-2cefcf568ab7");
    }

    public CustomerREST()
    {

    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public void sendMsgCustomerReg(String ID) throws IOException, TimeoutException
    {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
		Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        
        channel.queueDeclare(CUSTOMER_REG_QUEUE, false, false, false, null);

        
        channel.basicPublish("", CUSTOMER_REG_QUEUE, null, ID.getBytes());
    }
}
