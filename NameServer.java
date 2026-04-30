package Group_SourceCode;

import java.net.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
/**
 *
 * @author IbrahimAhmedBadr
 */
public class NameServer {
    // ConcurrentHashMap is used to ensure thread safety when multiple clients
    static ConcurrentHashMap<String, String> registry = new ConcurrentHashMap<>();
    public static void main(String args[]) {
        int port = 1234;
        ServerSocket serverSocket = null;

        try {
            // Register service on port 1234
            serverSocket = new ServerSocket(port);
            System.out.println("Name Service Server started on port " + port);

            // Server in Loop: Always up
            while (true) {
                // Wait and accept a connection
                Socket clientSocket = serverSocket.accept(); 
                System.out.println("Connection request accepted from: " + clientSocket.getInetAddress());

                // Multithreaded Server: For Serving Multiple Clients Concurrently
                ClientHandler handler = new ClientHandler(clientSocket);
                
                // Spin up a new worker thread so the server doesn't block
                Thread workerThread = new Thread(handler);
                workerThread.start();
            }

        } catch (IOException ioe) {
            // Handle IOExceptions that occur when opening the socket
            System.out.println("IOException: " + ioe);
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                System.out.println("Error closing server socket: " + e.getMessage());
            }
        }
    }
}

