import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.telnet.*;
import org.apache.commons.text.StringEscapeUtils;

import java.io.*;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
//            telnet.setConnectTimeout(20000);
            telnet.registerSpyStream(new FileOutputStream(server + ".log"));
//            telnet.setReaderThread(true);


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
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }


        String tempString = "";
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

//            final String prompt = ".*[Ll]ogin:$|.*[Uu]sername:$|.*ssword:$|.*enable:$|.*[#>]$";
//            final String prompt = ".*[Ll]ogin:$|.*[Uu]sername:$|.*ssword:$|.*enable:$|.*[>\\]].*";
            final String prompt = "[Ll]ogin:|[Uu]sername:|ssword:|enable:|\\S[>\\]]";

            Pattern pattern = Pattern.compile(prompt);
//            Matcher matcher = pattern.matcher(tempString);

            int b;
            char[] charBuf = new char[1024];
//            while (!pattern.matcher(stringBuilder.toString()).find()) {
            while (true) {
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

//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }

                }

                if (pattern.matcher(stringBuilder.toString()).find() && in.available() == 0) {
//                    System.out.println("SB:" + stringBuilder + "...");
//                    System.out.println("Break");
                    break;
                }



//                tempString = stringBuilder.toString().trim();

//                System.out.println("#s#" + stringBuilder + "#e#");

//                System.out.println("\nPart of reading buffer:\n" + tempString + "\n%%%%%\n");


//                matcher = pattern.matcher(tempString);
//                if (pattern.matcher(stringBuilder.toString()).find()) {
//                    System.out.println("Break");
//                    break;
//                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.print(stringBuilder.toString());
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

            try (PrintWriter fileWriter = new PrintWriter("conf1.txt")) {
                fileWriter.print(currentConf);
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
