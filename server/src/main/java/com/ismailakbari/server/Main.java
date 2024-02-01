package com.ismailakbari.server;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        String outputPath ;// "/Users/ismail/IdeaProjects/Mine/clientServer/outputDir";
        int PORT ; //12345 ;
        Map<String, String> config = new HashMap<>();

        try {
            //Read the server config file
            config = ConfigReader.readProps(args[0]);
            outputPath = config.get("outputPath");
            PORT = Integer.parseInt(config.get("PORT"));

            ServerCl.startServer(PORT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
