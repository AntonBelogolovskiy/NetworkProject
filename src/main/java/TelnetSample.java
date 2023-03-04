import org.apache.commons.net.telnet.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Pattern;

public class TelnetSample {
    public static final String PROMPT = "[Ll]ogin:|[Uu]sername:|ssword:|enable:|\\S[>\\]]";
    private TelnetClient telnet;
    private InputStream in;
    private PrintStream out;

    public TelnetSample(String server, int port) {
        try {
            // Connect to the specified server
            telnet = new TelnetClient();
//            telnet.setConnectTimeout(20000);
            telnet.registerSpyStream(new FileOutputStream(server + ".log"));
//            telnet.setReaderThread(true);


            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            System.out.println("Connecting to " + server);
            telnet.connect(server, port);


            TerminalTypeOptionHandler terminalTypeOpt = new TerminalTypeOptionHandler("VT100", false, false, true, false);
            EchoOptionHandler echoOpt = new EchoOptionHandler(true, false, true, false);
            SuppressGAOptionHandler gaOpt = new SuppressGAOptionHandler(true, true, true, true);
            try {
                telnet.addOptionHandler(terminalTypeOpt);
                telnet.addOptionHandler(echoOpt);
                telnet.addOptionHandler(gaOpt);
            } catch (InvalidTelnetOptionException e) {
                System.err.println("Error registering option handlers: " + e.getMessage());
            }

            in = telnet.getInputStream();
            out = new PrintStream(telnet.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readResponse() {
//        System.out.println("TelnetSample.readResponse()");
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }


        String tempString;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            Pattern pattern = Pattern.compile(PROMPT);

            char[] charBuf = new char[1024];
            do {
                //stringBuilder.append(tempString);
//                System.out.println(in.available());

                int bufLength = reader.read(charBuf);

                tempString = String.valueOf(charBuf, 0, bufLength);
                stringBuilder.append(tempString);
//                System.out.printf("&&%s--", tempString);

                if (stringBuilder.toString().contains("---- More ----")) {

                    tempString = stringBuilder.toString().replaceAll("\\s*---- More ----\\p{Cc}.*\\p{Cc}.{1,4}",
                                                                     "\r\n");

                    stringBuilder = new StringBuilder(tempString);

                    out.print(' ');
                    out.flush();

                }


//                tempString = stringBuilder.toString().trim();

//                System.out.println("#s#" + stringBuilder + "#e#");

//                System.out.println("\nPart of reading buffer:\n" + tempString + "\n%%%%%\n");


//                matcher = pattern.matcher(tempString);
//                if (pattern.matcher(stringBuilder.toString()).find()) {
//                    System.out.println("Break");
//                    break;
//                }
            } while (!pattern.matcher(stringBuilder.toString()).find() || in.available() != 0);


        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.print(stringBuilder);
//        System.out.println("==========================================================");

        return stringBuilder.toString();
    }

    public String sendCommand(String command) {

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
                out.write(ch);
                out.flush();
            }

//            System.out.println(command);

//            String output = read2();

//            if (output.trim().isEmpty()) {
//                System.out.println("output empty");
//            } else {
//                System.out.println(output);
//            }

//            System.out.println("==========================================================");

            return readResponse();
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
        long startTime = System.currentTimeMillis();
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

            telnet.sendCommand("display clock\n");
            telnet.sendCommand("display device\n");
            telnet.sendCommand("display version\n");
            telnet.sendCommand("display clock\n");
            telnet.sendCommand("display connection\n");


//            telnet.sendCommand("system-view\n");

            String currentConf = telnet.sendCommand("display current-configuration conf\r\n");
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
