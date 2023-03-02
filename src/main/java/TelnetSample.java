import org.apache.commons.net.telnet.*;

import java.io.*;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelnetSample {
    private TelnetClient telnet;
    private InputStream in;
    private PrintStream out;

    public TelnetSample(String server, int port) {
        try {
            // Connect to the specified server
            telnet = new TelnetClient();
            telnet.setConnectTimeout(2000);
            telnet.registerSpyStream(new FileOutputStream(server + ".log"));
            telnet.setReaderThread(true);


            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            System.out.println("Connecting to " + server);
            telnet.connect(server, port);


//            TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler("VT100", false, false, true, false);
//            EchoOptionHandler echoopt = new EchoOptionHandler(true, false, true, false);
//            SuppressGAOptionHandler gaopt = new SuppressGAOptionHandler(true, true, true, true);
//            try {
//                telnet.addOptionHandler(ttopt);
//                telnet.addOptionHandler(echoopt);
//                telnet.addOptionHandler(gaopt);
//            } catch (InvalidTelnetOptionException e) {
//                System.err.println("Error registering option handlers: " + e.getMessage());
//            }

            in = telnet.getInputStream();
            out = new PrintStream(telnet.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readResponse() {
//        System.out.println("TelnetSample.readResponse()");
//        Thread.sleep(500);

        StringBuilder out = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            final String prompt = ".*[Ll]ogin:$|.*[Uu]sername:$|.*ssword:$|.*enable:$|.*[#>]$";

            int b;
            while (!Pattern.compile(prompt).matcher(out.toString().replaceAll("[^\\p{Print}]", "")).matches()) {
                b = reader.read();
                out.append((char) b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.print(out.toString());
//        System.out.println("==========================================================");

        return out.toString();
    }

    public String read2() throws InterruptedException {
//        System.out.println("TelnetSample.read()");

        StringBuffer sb = new StringBuffer();


        try {
            Thread.sleep(200);

            int available = in.available();
//            System.out.println("in.available=" + available);

            for (int index = 0; index < available; index++) {
                char ch = (char) in.read();
                sb.append(ch);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public String sendCommand(String command) {

//        if (Pattern.compile("quit|exit|logout").matcher(command.trim()).matches()) {
        if (command.trim().matches("quit|exit|logout")) {
            System.out.println("disconnecting...");
            try {
                telnet.disconnect();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        try {
            InputStream is = new ByteArrayInputStream(command.getBytes());

            int ch;

            while ((ch = is.read()) != -1) {
//                System.out.print((char) ch);
                out.write(ch);
                out.flush();
            }

//            System.out.println(command);

//            String output = read2();
            String output = readResponse();

//            if (output.trim().isEmpty()) {
//                System.out.println("output empty");
//            } else {
//                System.out.println(output);
//            }

//            System.out.println("==========================================================");

            return output;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void disconnect() {
        try {
            telnet.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 0 && Objects.equals(args[0], "-d"))
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

            String output = telnet.sendCommand("display clock\n");


            telnet.sendCommand("quit\n");

//            System.exit(222);
            //telnet.sendCommand("mail from:xyz@testmail.com");
            //telnet.sendCommand("rcpt to:pk@testmail.com");
            //telnet.sendCommand("quit");

            telnet.disconnect();
            System.out.println("Program id ended..");
            System.out.println(output);

        } catch (RuntimeException e) {
            System.out.println("Connection closed");
        }
    }
}
