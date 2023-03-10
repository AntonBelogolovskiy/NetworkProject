package ru.networking;

import org.apache.commons.net.telnet.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class ClientTelnet implements Runnable {
    private String prompt = "[Ll]ogin:.*\\z|[Uu]sername:.*\\z|ssword:.*\\z|enable:.*\\z|[>\\]]\\z|in unit\\d login\\z";
    private int connectTimeout = 5000;
    private String server;
    private int port;
    private TelnetClient telnet;
    private InputStream in;
    private PrintStream out;

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public ClientTelnet(String server, int port) {
        this.server = server;
        this.port = port;
        telnet = new TelnetClient();
        telnet.setReaderThread(true);

        //-------------
        TerminalTypeOptionHandler terminalTypeOpt = new TerminalTypeOptionHandler("VT100", false,
                                                                                  false, true,
                                                                                  false);
        EchoOptionHandler echoOpt = new EchoOptionHandler(false, false,
                                                          false, false);
        SuppressGAOptionHandler gaOpt = new SuppressGAOptionHandler(true, true,
                                                                    true, true);
        try {
            telnet.addOptionHandler(terminalTypeOpt);
            telnet.addOptionHandler(echoOpt);
            telnet.addOptionHandler(gaOpt);
        } catch (InvalidTelnetOptionException | IOException e) {
            System.err.println("Error registering option handlers: " + e.getMessage());
        }
        //-------------
    }
    public void connect() {
        try {
            // Connect to the specified server
            telnet.setConnectTimeout(connectTimeout);

//             for debug
//            telnet.registerSpyStream(new FileOutputStream(server + ".log"));

//            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//            System.out.println("Connecting to " + server);

            telnet.connect(server, port);
            in = new BufferedInputStream(telnet.getInputStream());
            out = new PrintStream(telnet.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String readResponse() {
        String tempString;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            Thread.sleep(200);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            Pattern pattern = Pattern.compile(prompt);

            char[] charBuf = new char[1024];
            while (true) {
                Thread.sleep(100);

                int bufLength = reader.read(charBuf);
                tempString = String.valueOf(charBuf, 0, bufLength);
                stringBuilder.append(tempString);
                if (stringBuilder.toString().contains("---- More ----")) {
                    tempString = stringBuilder.toString().replaceAll("\\s*---- More ----", "\r\n");
                    stringBuilder = new StringBuilder(tempString);
                    out.print(" ");
                    out.flush();
                }

                if (pattern.matcher(stringBuilder.toString()).find() && in.available() == 0) break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        String res = stringBuilder.toString().replaceAll("\u001b.*\u001b.{1,4}", "");
        //System.out.print(res);

        return res;
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
            out.print(command);
            out.flush();
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

    @Override
    public void run() {
        try {
            connect();
            readResponse();
            sendCommand("mgrconf\n");
            sendCommand("12345\n");
            sendCommand("undo terminal monitor\n");
            sendCommand("display clock\n");
            sendCommand("screen-length 0 temporary\n");
            String currentConf = sendCommand("display current-configuration\n");
            currentConf = currentConf.substring(currentConf.indexOf("\n") + 1, currentConf.lastIndexOf("\n") - 1);

            try (PrintWriter fileWriter = new PrintWriter(server + ".conf")) {
                fileWriter.println(currentConf);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            disconnect();
        } catch (RuntimeException e) {
            System.out.println("Connection closed");
        }
    }
}
