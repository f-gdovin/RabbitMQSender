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

    private static String EXCHANGE_NAME = "logs";
    private static String QUEUE_NAME = "esperQueue";

    public static void publish(String queueName, String exchangeName, String message) throws Exception {
        QUEUE_NAME = queueName;
        EXCHANGE_NAME = exchangeName;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout", true); //fanout ignoruje routing kluce, potom zmenit!
        String givenQueueName = channel.queueDeclare(QUEUE_NAME, false, false, false, null).getQueue();
        channel.queueBind(givenQueueName, EXCHANGE_NAME, "");
        channel.basicPublish(EXCHANGE_NAME, "", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());


        channel.close();
        connection.close();
    }
}