package ru.networking;

import java.io.*;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length > 0 && Objects.equals(args[0], "-d"))
            System.setOut(new PrintStream(new FileOutputStream("debug.log")));
        try {
            String server = "10.16.32.10";
            int port = 23;
            String prompt = "[Ll]ogin:.*\\z|[Uu]sername:.*\\z|ssword:.*\\z|enable:.*\\z|[>\\]]\\z|in unit\\d login\\z";
            String[] commands = new String[]{
                    "mgrconf\n",
                    "12345\n",
                    "undo terminal monitor\n",
                    "display clock\n",
                    "screen-length 0 temporary\n",
                    "display current-configuration\n"
            };

            ClientTelnet client1 = new ClientTelnet("10.16.32.10",23,prompt);
            client1.setCommands(commands);
            new Thread(client1).start();

//            new Thread(new ClientTelnet("10.16.32.10", port, prompt)).start();
//            new Thread(new ClientTelnet("10.16.32.21", port, prompt)).start();
//            new Thread(new ClientTelnet("83.167.99.65", port, prompt)).start();
//            new Thread(new ClientTelnet("10.16.33.206", port, prompt)).start();
//            new Thread(new ClientTelnet("10.16.33.217", port, prompt)).start();
//            new Thread(new ClientTelnet("10.16.34.185", port, prompt)).start();

            System.out.println("Program is ended..");

        } catch (RuntimeException e) {
            System.out.println("Connection closed");
        }
    }
}
