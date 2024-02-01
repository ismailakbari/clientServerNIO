package com.ismailakbari.client;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;

public class ClientCl {


    public static void startClient(Map<String, String> filteredMap, String IP, int PORT, String filePath) throws IOException {

        // Create a SocketChannel
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        // Connect to the server
        socketChannel.connect(new InetSocketAddress(IP, PORT) ) ;

        // Wait for the connection to be established
        while (!socketChannel.finishConnect()) {
            // You can do other tasks here while waiting
        }

        System.out.println("Connected to server.");

        // Send a message to the server
        for (Map.Entry<String,String> entry : filteredMap.entrySet()) {
            String prop = entry.getKey() + "=" + entry.getValue() ;
            ByteBuffer buffer = ByteBuffer.wrap(prop.getBytes());
            socketChannel.write(buffer);
            System.out.println("Message sent to server: " + prop);
        }
        // Close the channel
        socketChannel.close();

        //delete the File
        deleteFile( filePath );
    }

    private static void deleteFile(String filePath) {
        File fileToDelete = new File(filePath);
        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
                System.out.println("File deleted successfully: " + filePath);
            } else {
                System.out.println("Failed to delete the file: " + filePath);
            }
        } else {
            System.out.println("File does not exist: " + filePath);
        }
    }
}
