package Group_SourceCode;

import java.net.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NameServer.java
 * ─────────────────────────────────────────────────────────────────
 * The multi-threaded Name Service registry.
 *
 * - Listens on port 1234 by default.
 * - Maintains a shared ConcurrentHashMap<ServiceName, IPAddress>.
 * - Spawns a new ClientHandler thread for every accepted connection,
 *   so multiple clients can register/resolve/deregister concurrently.
 * - Supports graceful shutdown by typing "shutdown" in the server console.
 * ─────────────────────────────────────────────────────────────────
 */
public class NameServer {

    /** Shared registry — key: service name, value: IP address. */
    static ConcurrentHashMap<String, String> registry = new ConcurrentHashMap<>();

    static final int PORT = 1234;

    // ── Entry point (also called from Main) ────────────────────────
    public static void main(String[] args) {

        // Background thread: watch for "shutdown" typed in server console
        Thread shutdownListener = new Thread(() -> {
            try (BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {
                String line;
                while ((line = console.readLine()) != null) {
                    if (line.trim().equalsIgnoreCase("shutdown")) {
                        System.out.println("[NameServer] Shutdown command received. Exiting.");
                        System.exit(0);
                    }
                }
            } catch (IOException ignored) {}
        });
        shutdownListener.setDaemon(true); // Don't prevent JVM exit
        shutdownListener.start();

        // ── Main accept loop ────────────────────────────────────────
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[NameServer] Name Service started on port " + PORT);
            System.out.println("[NameServer] Type 'shutdown' to stop the server.\n");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[NameServer] New connection from: "
                        + clientSocket.getInetAddress().getHostAddress());

                // Spin up a dedicated worker thread per client
                Thread workerThread = new Thread(new ClientHandler(clientSocket));
                workerThread.setDaemon(true);
                workerThread.start();
            }

        } catch (IOException ioe) {
            System.err.println("[NameServer] IOException: " + ioe.getMessage());
        }
    }
}