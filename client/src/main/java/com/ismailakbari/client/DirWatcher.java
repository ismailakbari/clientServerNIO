package com.ismailakbari.client;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.*;
import java.util.Map;

public class DirWatcher {

    private WatchService watchService;
    private Path directory;
    private String REGEX ;
    private String IP;
    private int PORT;

    public DirWatcher(String directoryPath, String REGEX, String IP, int PORT) throws Exception {
        this.IP = IP;
        this.PORT = PORT ;
        this.REGEX = REGEX ;

        this.watchService = FileSystems.getDefault().newWatchService();
        this.directory = Paths.get(directoryPath);

        // Register the directory for ENTRY_CREATE, ENTRY_DELETE, and ENTRY_MODIFY events
        this.directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                //StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        System.out.println("Monitoring directory: " + directory);
    }

    public void start() {
        try {
            while (true) {
                WatchKey key = watchService.take(); // Wait for an event to occur

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path changedPath = (Path) event.context();

                    System.out.println("Event type: " + kind + ", File affected: " + changedPath);

                    //Read the properties file into a Map
                    Map<String, String> filteredMap = PropReader.readProps(directory.toString() + "/" + changedPath.toString(), changedPath.toString(), REGEX) ;
                    //Relay2Server.relayToServer(filteredMap, this.IP, this.PORT);

                    //Start the client and send the Map to the server
                    startClient(filteredMap, IP, PORT);

                    //delete the File
                    //deleteFile( directory.toString() + "/" + changedPath.toString() );
                }

                boolean valid = key.reset();
                if (!valid) {
                    System.out.println("WatchKey no longer valid. Exiting monitoring.");
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void startClient(Map<String, String> filteredMap, String IP, int PORT) throws IOException {
        // Create a SocketChannel
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        // Connect to the server
        socketChannel.connect(new InetSocketAddress(IP, PORT));

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
    }

    public void deleteFile(String filePath) {
        // Create a File object representing the file to be deleted
        File fileToDelete = new File(filePath);

        // Check if the file exists before attempting deletion
        if (fileToDelete.exists()) {
            // Attempt to delete the file
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
