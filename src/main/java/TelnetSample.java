import org.apache.commons.net.telnet.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Pattern;

public class TelnetSample {
    public static final String PROMPT = "[Ll]ogin:|[Uu]sername:|ssword:|enable:|\\S[>\\]]";
    public static final int CONNECT_TIMEOUT = 5000;
    private TelnetClient telnet;
    private InputStream in;
    private PrintStream out;

    public TelnetSample(String server, int port) {
        try {
            // Connect to the specified server
            telnet = new TelnetClient();
            telnet.setConnectTimeout(CONNECT_TIMEOUT);
            telnet.registerSpyStream(new FileOutputStream(server + ".log"));


            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            System.out.println("Connecting to " + server);
            telnet.connect(server, port);


            TerminalTypeOptionHandler terminalTypeOpt = new TerminalTypeOptionHandler("VT100", false, false, true,
                                                                                      false);
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
        String tempString;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            Pattern pattern = Pattern.compile(PROMPT);

            char[] charBuf = new char[1024];
            do {
                int bufLength = reader.read(charBuf);
                tempString = String.valueOf(charBuf, 0, bufLength);
                stringBuilder.append(tempString);
                if (stringBuilder.toString().contains("---- More ----")) {
                    tempString = stringBuilder.toString().replaceAll("\\s*---- More ----\\p{Cc}.*\\p{Cc}.{1,4}",
                                                                     "\n");
                    stringBuilder = new StringBuilder(tempString);
                    out.print(' ');
                    out.flush();
                }
            } while (!pattern.matcher(stringBuilder.toString()).find() || in.available() != 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.print(stringBuilder);
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
}
