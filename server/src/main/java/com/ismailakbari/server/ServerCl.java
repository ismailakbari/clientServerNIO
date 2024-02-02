package com.ismailakbari.server;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class ServerCl {

    public static void startServer(String outputPath, String PROP_DELIMITER, int PORT) throws IOException {
        // Create a selector
        Selector selector = Selector.open();

        // Create a ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(PORT));
        serverSocketChannel.configureBlocking(false);

        // Register the ServerSocketChannel with the selector for accept operations
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server started on port "+ PORT +" ...");

        Properties properties = new Properties();
        String filename ="";

        while (true) {
            // Select ready channels
            selector.select();

            // Get the selected keys
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();

                if (key.isAcceptable()) {
                    // Accept a new connection
                    acceptConnection(serverSocketChannel, selector);
                } else if (key.isReadable()) {
                    // Read data from the client
                    String data=readDataFromClient(key);
                    if(data != null) {
                        //convert back data to properties
                        String[] entries = data.split(PROP_DELIMITER);           //split the pairs to get key and value
                        for (String entry : entries) {
                            String[] prop = entry.split("=");
                            if (prop.length == 1)
                                filename = prop[0].trim();
                            else
                                properties.put(prop[0].trim(), prop[1].trim());   //add them to the hashmap and trim whitespaces
                        }
                    }
                    else{ //connection closed. so write the file
                        FileOutputStream fileOutputStream = new FileOutputStream(outputPath + "/" + filename);
                        properties.store(fileOutputStream, null);
                    }
                }

                keyIterator.remove();
            }
        }
    }

    private static void acceptConnection(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);

        // Register the SocketChannel with the selector for read operations
        clientChannel.register(selector, SelectionKey.OP_READ);

        System.out.println("Accepted connection from: " + clientChannel.getRemoteAddress());
    }

    private static String readDataFromClient(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            // Connection closed by client
            key.cancel();
            String ccAddr = clientChannel.getRemoteAddress().toString() ;
            clientChannel.close();
            System.out.println("Connection closed by client: " + ccAddr );
            return null;
        }

        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        String message = new String(data);
        System.out.println("Received message from " + clientChannel.getRemoteAddress() + ": " + message.trim());
        return message;
    }

}
