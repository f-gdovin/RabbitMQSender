/**
 * @author Filip Gdovin
 * @version 21. 1. 2015
 */
public class Main {

    public static void main(String[] args) {

        RabbitMQsender firstSender = new RabbitMQsender();
        firstSender.send(args);
    }
}