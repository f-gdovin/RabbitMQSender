import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

/**
 * Created by Filip Gdovin, 410328 on 21. 1. 2015.
 */
public class Main {

    public static void main(String[] args){

        Map schema;

        File myFile = new File("C:\\jsonInput.json");

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
                System.out.println(line);
                RabbitMQsender.publish(line);
            }
            bufferReader.close();
        }
        catch(Exception ex){
            Logger.getLogger(Main.class.getName()).log(Level.WARN, "Error while reading file line by line:", ex);
        }
        System.out.println("Everything sent without errors\n");
    }
}
