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
    private String DELIMITER ;
    private String IP;
    private int PORT;

    public DirWatcher(String directoryPath, String REGEX, String DELIMITER, String IP, int PORT) throws Exception {
        this.IP = IP;
        this.PORT = PORT ;
        this.REGEX = REGEX ;
        this.DELIMITER = DELIMITER ;
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
                    String filePath = directory.toString() + "/" + changedPath.toString();
                    Map<String, String> filteredMap = PropReader.readProps(filePath, REGEX) ;
                    //Relay2Server.relayToServer(filteredMap, this.IP, this.PORT);

                    //Start the client and send the Map to the server
                    ClientCl.startClient(filteredMap, DELIMITER, IP, PORT, filePath, changedPath.toString());
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

}
