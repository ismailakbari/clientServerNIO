package com.ismailakbari.client;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        String watchPath ;// "/Users/ismail/IdeaProjects/Mine/clientServer/watchDir";
        String REGEX ; //"^[a-zA-z].*?";
        String IP ; // "localhost" ;
        int PORT ; //12345 ;

        Map<String, String> config = new HashMap<>();
        try {
            //Read the config file
            config = ConfigReader.readProps(args[0]);
            watchPath = config.get("watchPath");
            REGEX = config.get("REGEX");
            IP = config.get("IP");
            PORT = Integer.parseInt(config.get("PORT"));

            //Watch the directory for file changes
            DirWatcher directoryWatcher = new DirWatcher(watchPath, REGEX, IP, PORT);
            directoryWatcher.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
