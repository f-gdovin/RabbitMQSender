/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

/**
 *
 * @author Filip Gdovin, 410328
 */
public class RabbitMQsender {

    private static final String EXCHANGE_NAME = "logs";
    private final static String QUEUE_NAME = "testQueue";

    public static void publish(String message) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout", true); //fanout ignoruje routing kluce, potom zmenit!
        String queueName = channel.queueDeclare(QUEUE_NAME, true, false, true, null).getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");
        channel.basicPublish(EXCHANGE_NAME, "", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());


        channel.close();
        connection.close();
    }
}