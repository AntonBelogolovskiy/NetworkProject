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

            String promptHuawei = "[Ll]ogin:.*\\z|[Uu]sername:.*\\z|ssword:.*\\z|enable:.*\\z|[>\\]]\\Z|in unit\\d login\\z";
            String promptRaisecom = "[Ll]ogin:.*\\z|[Uu]sername:.*\\z|ssword:.*\\z|enable:.*\\z|[>#]\\Z|in unit\\d login\\z";

            String[] commandsHuawei = new String[]{
                    "mgrconf\n",
                    "12345\n",
                    "undo terminal monitor\n",
                    "display clock\n",
                    "screen-length 0 temporary\n",
                    "display current-configuration\n"
            };

            String[] commandsRaisecom = new String[]{
                    "mgrconf\n",
                    "12345\n",
                    "en\n",
                    "cegth\n",
                    "show clock\n",
                    "terminal page-break disable\n",
                    "show running-config\n"
            };

            ClientTelnet client1 = new ClientTelnet("10.16.32.21",23,promptHuawei); //H3C S3100-26
            ClientTelnet client2 = new ClientTelnet("10.16.0.3",23,promptRaisecom); //H3C S3100-26

            client1.setCommands(commandsHuawei);
//            client1.start();
            client2.setCommands(commandsRaisecom);
            client2.start();

//            new Thread(new ClientTelnet("10.16.32.10", port, promptHuawei)).start();
//            new Thread(new ClientTelnet("10.16.32.21", port, promptHuawei)).start();
//            new Thread(new ClientTelnet("83.167.99.65", port, promptHuawei)).start();
//            new Thread(new ClientTelnet("10.16.33.206", port, promptHuawei)).start();
//            new Thread(new ClientTelnet("10.16.33.217", port, promptHuawei)).start();
//            new Thread(new ClientTelnet("10.16.34.185", port, promptHuawei)).start();

            System.out.println("Program is ended..");

        } catch (RuntimeException e) {
            System.out.println("Connection closed");
        }
    }
}
