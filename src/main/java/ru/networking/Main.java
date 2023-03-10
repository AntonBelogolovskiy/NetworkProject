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

            new Thread(new ClientTelnet("10.16.32.10",port)).start();
            new Thread(new ClientTelnet("10.16.32.21",port)).start();
            new Thread(new ClientTelnet("83.167.99.65",port)).start();
            new Thread(new ClientTelnet("10.16.33.206",port)).start();
            new Thread(new ClientTelnet("10.16.33.217",port)).start();
            new Thread(new ClientTelnet("10.16.34.185",port)).start();

            System.out.println("Program is ended..");

        } catch (RuntimeException e) {
            System.out.println("Connection closed");
        }
    }
}
