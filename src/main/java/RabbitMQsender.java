/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Filip Gdovin, 410328
 */
public class RabbitMQsender {

    private static final Logger logger = LogManager.getLogger(RabbitMQsender.class);

    public void send(String[] args) {

        if(args.length != 4) {
            logger.warn("Bad number of arguments, Sender needs String hostURL, String queueName, String fileName, int sleepTime");
            return;
        }

        String hostURL = args[0];
        String queueName = args[1];
        String fileName = args[2];
        int sleepTime = Integer.parseInt(args[3]);
        File myFile = new File(fileName);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostURL);

        try{
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            //send contents of file
            FileReader inputFile = new FileReader(myFile);
            BufferedReader bufferReader = new BufferedReader(inputFile);
            String line = bufferReader.readLine();

            do {
                Thread.sleep(sleepTime);
                if(line != null) {
//                    String toSend = addTimestamp(line);
                    this.publish(channel, queueName, line); //will be dropped till queue is declared (so, declare)
                    if(logger.isDebugEnabled()) {
                        logger.debug("Sending '" + line + "' from file " + myFile.getAbsolutePath());
                    }
                    line = bufferReader.readLine();
                }
            } while (line != null);
            bufferReader.close();
            channel.close();
            connection.close();
        }
        catch(Exception ex){
            logger.error("Error while reading file line by line: " + ex.getMessage());
            return;
        }
        logger.info("Everything sent without errors\n");
    }

    private static String addTimestamp(String line) {
        final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        ZonedDateTime localTime = LocalDateTime.now().atZone(ZoneId.of("+02:00"));
        String currTimeString = localTime.format(formatter);
        String pattern = "generateTimestampHere";
        int index = line.indexOf(pattern);
        if(index < 0) {
            return line;
        }
        return line.substring(0, index) + currTimeString + line.substring(index + pattern.length());
    }

    private void publish(Channel channel, String routingKey, String message) throws Exception {
        channel.basicPublish("esperExchange", routingKey, null, message.getBytes());
    }
}