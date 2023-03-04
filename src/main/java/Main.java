import java.io.*;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length > 0 && Objects.equals(args[0], "-d"))

            System.setOut(new PrintStream(new FileOutputStream("debug.log")));
        try {

//            String server = "10.16.32.21";
            String server = "10.16.32.251";
//            String server = "10.16.32.10";
            int port = 23;

            ClientTelnet telnet = new ClientTelnet(server, port);
            telnet.readResponse();


            telnet.sendCommand("mgrconf\n");

            telnet.sendCommand("12345\n");

//            telnet.sendCommand("en\n");
            telnet.sendCommand("undo terminal monitor\n");
//            telnet.sendCommand("screen-length 0 temporary\n");
            String currentConf = telnet.sendCommand("display current-configuration\n");

            telnet.sendCommand("system-view\n");
            telnet.sendCommand("display clock\n");
            telnet.sendCommand("display device\n");
            telnet.sendCommand("display version\n");
            telnet.sendCommand("display clock\n");
            telnet.sendCommand("display connection\n");


//             for view ESC sequences in String
//            System.out.println(StringEscapeUtils.escapeJava(currentConf));

            currentConf = currentConf.substring(currentConf.indexOf("\n") + 1, currentConf.lastIndexOf("\n") - 1);

            try (PrintWriter fileWriter = new PrintWriter(server + ".conf")) {
                fileWriter.println(currentConf);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


//            telnet.sendCommand("quit\n");

            telnet.disconnect();
            System.out.println("Program is ended..");
//            System.out.println(System.currentTimeMillis() - startTime);

        } catch (RuntimeException e) {
            System.out.println("Connection closed");
        }
    }
}
