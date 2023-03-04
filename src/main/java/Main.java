import java.io.*;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length > 0 && Objects.equals(args[0], "-d"))
            System.setOut(new PrintStream(new FileOutputStream("debug.log")));
        try {
//            TelnetSample telnet = new TelnetSample("10.254.247.42", 23);
//            TelnetSample telnet = new TelnetSample("mail.ext.ru", 25);
            TelnetSample telnet = new TelnetSample("10.16.32.21", 23);
            telnet.readResponse();


            telnet.sendCommand("mgrconf\n");

            telnet.sendCommand("12345\n");

//            telnet.sendCommand("en\n");
            telnet.sendCommand("undo terminal monitor\n");
            telnet.sendCommand("system-view\n");

            telnet.sendCommand("display clock\n");
            telnet.sendCommand("display device\n");
            telnet.sendCommand("display version\n");
            telnet.sendCommand("display clock\n");
            telnet.sendCommand("display connection\n");


//            telnet.sendCommand("system-view\n");
            long startTime = System.currentTimeMillis();
            String currentConf = telnet.sendCommand("display current-configuration\n");
//            telnet.sendCommand("display cur conf\n");

//            String currentConf = telnet.sendCommand("display mac-address\n");

//            telnet.sendCommand("display cur\r\n");


//             for view ESC sequences in String
//            System.out.println(StringEscapeUtils.escapeJava(currentConf));


//            System.out.println(currentConf);

//            telnet.sendCommand("display device\r\n");
//            System.out.println(StringEscapeUtils.escapeJava(telnet.sendCommand("display version\r\n")));

//            System.out.println(currentConf.substring(currentConf.indexOf("\n"),currentConf.lastIndexOf("\n")));
            currentConf = currentConf.substring(currentConf.indexOf("\n")+1, currentConf.lastIndexOf("\n")-1);

            try (PrintWriter fileWriter = new PrintWriter("conf1.txt")) {
                fileWriter.println(currentConf);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


//            telnet.sendCommand("quit\n");

            telnet.disconnect();
            System.out.println("Program is ended..");
            System.out.println(System.currentTimeMillis() - startTime);

        } catch (RuntimeException e) {
            System.out.println("Connection closed");
        }
    }
}
