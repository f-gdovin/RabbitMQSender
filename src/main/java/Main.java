import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Filip Gdovin, 410328 on 21. 1. 2015.
 */
public class Main {

    public static void main(String[] args){

        //useless in one thread
        int sleepTime = 3000; //time between events in ms for first stream
        int sleepTime2 = 1000; //time between events in ms for second stream

        File myFile = new File("C:\\jsonInput.json");
        File myFile2 = new File("C:\\jsonInput2.json");

        if(args.length > 0){
            myFile = new File(args[0]);
        }

        try{
            //send contents of file
            FileReader inputFile = new FileReader(myFile);
            BufferedReader bufferReader = new BufferedReader(inputFile);
            String line = bufferReader.readLine();

            //send contents of second file
            FileReader inputFile2 = new FileReader(myFile2);
            BufferedReader bufferReader2 = new BufferedReader(inputFile2);
            String line2 = bufferReader2.readLine(); //for second stream

            do {
//                Thread.sleep(sleepTime);
                if(line != null) {
                    String toSend = addTimestamp(line);
                    RabbitMQsender.publish("esperQueue", "logs", toSend);
                    System.out.println("Sending '" + toSend + "' from file " + myFile.getAbsolutePath().toString());
                    line = bufferReader.readLine();
                }

                if(line2 != null) {
                    RabbitMQsender.publish("esperQueue2", "logs2", line2);
                    System.out.println("Sending '" + line2 + "' from file " + myFile2.getAbsolutePath().toString());
                    line2 = bufferReader2.readLine();
                }
            } while ((line != null) || (line2 != null));
            bufferReader.close();
            bufferReader2.close();
        }
        catch(Exception ex){
            Logger.getLogger(Main.class.getName()).log(Level.WARN, "Error while reading file line by line:", ex);
        }
        System.out.println("Everything sent without errors\n");
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
        String result = line.substring(0, index) + currTimeString + line.substring(index + pattern.length());
        return result;
    }
}