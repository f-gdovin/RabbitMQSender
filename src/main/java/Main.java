import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Filip Gdovin, 410328 on 21. 1. 2015.
 */
public class Main {

    public static void main(String[] args){

        int sleepTime = 3000; //time between events in ms

        File myFile = new File("C:\\jsonInput.json");
        File myFile2 = new File("C:\\jsonInput2.json");

        if(args.length > 0){ //definovanie vlastneho suboru ako 1. vstupny parameter
            myFile = new File(args[0]);
        }

        try{
            FileReader inputFile = new FileReader(myFile);
            BufferedReader bufferReader = new BufferedReader(inputFile);
            String line;

            //send contents of file
            System.out.println("Sending events from file " + myFile.getAbsolutePath().toString()+ "...");

            while ((line = bufferReader.readLine()) != null){
                Thread.sleep(sleepTime);
                String toSend = addTimestamp(line);
                RabbitMQsender.publish("esperQueue", "logs", toSend);
                System.out.println("Sending '" + toSend + "'");
            }
            bufferReader.close();


            /*FileReader inputFile2 = new FileReader(myFile2);
            BufferedReader bufferReader2 = new BufferedReader(inputFile2);
            String line2;

            //send contents of second file
            System.out.println("Sending events from file " + myFile2.getAbsolutePath().toString()+ "...");

            while ((line2 = bufferReader2.readLine()) != null){
                System.out.println(line2);
                RabbitMQsender.publish("esperQueue2", "logs2", line2);
            }
            bufferReader2.close();*/
        }
        catch(Exception ex){
            Logger.getLogger(Main.class.getName()).log(Level.WARN, "Error while reading file line by line:", ex);
        }
        System.out.println("Everything sent without errors\n");
    }

    private static String addTimestamp(String line) {
        String currTime = LocalDateTime.now().toString();
        String pattern = "generateTimestampHere";
        int index = line.indexOf(pattern);
        String result = line.substring(0, index) + currTime + line.substring(index + pattern.length());
        return result;
    }
}
