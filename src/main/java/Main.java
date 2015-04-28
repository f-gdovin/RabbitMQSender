import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Filip Gdovin
 * @version 21. 1. 2015
 */
public class Main {

    public static void main(String[] args){

        int sleepTime = 3000; //time between events in ms for first stream

        File myFile = loadFileFromResources("jsonInput.json");
        File myFile2 = loadFileFromResources("jsonInput2.json");

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
            String line2 = null; //bufferReader2.readLine();  //null to disable second stream

            do {
//                Thread.sleep(sleepTime);
                if(line != null) {
                    String toSend = addTimestamp(line);
                    RabbitMQsender.publish("inputQueue", toSend); //will be dropped till queue is declared (so, declare)
                    System.out.println("Sending '" + toSend + "' from file " + myFile.getAbsolutePath());
                    line = bufferReader.readLine();
                }

                if(line2 != null) {
                    RabbitMQsender.publish("inputQueue2", line2); //will be dropped till queue is declared (after that it will be rejected as malformed (that file doesn't contain timestamp))
                    System.out.println("Sending '" + line2 + "' from file " + myFile2.getAbsolutePath());
                    line2 = bufferReader2.readLine();
                }
            } while ((line != null) || (line2 != null));
            bufferReader.close();
            bufferReader2.close();
        }
        catch(Exception ex){
            System.out.println("Error while reading file line by line: " + ex.getMessage() + ex.getCause());
            return;
        }
        System.out.println("Everything sent without errors\n");
    }

    private static File loadFileFromResources(String fileName) throws NullPointerException{
        return new File(RabbitMQsender.class.getClassLoader().getResource(fileName).getFile());
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
}