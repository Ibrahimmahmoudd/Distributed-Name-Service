package Group_SourceCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * ClientHandler.java
 * ─────────────────────────────────────────────────────────────────
 * Handles one connected client on its own thread.
 *
 * Supported protocol commands:
 *   REGISTER   <ServiceName> <IPAddress>
 *   RESOLVE    <ServiceName>
 *   DEREGISTER <ServiceName>
 *
 * Thread safety:
 *   - REGISTER and DEREGISTER are wrapped in synchronized blocks on
 *     the shared registry to prevent race conditions.
 *   - RESOLVE uses ConcurrentHashMap's built-in atomic get(), which
 *     is safe without additional synchronization.
 * ─────────────────────────────────────────────────────────────────
 */
public class ClientHandler implements Runnable {

    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter    out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("[ClientHandler] Received: " + inputLine);
                String response = processCommand(inputLine);
                out.println(response);
            }
        } catch (IOException e) {
            System.err.println("[ClientHandler] Exception: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("[ClientHandler] Failed to close socket: " + e.getMessage());
            }
        }
    }

    // ── Command dispatcher ──────────────────────────────────────────
    private String processCommand(String commandLine) {
        if (commandLine == null || commandLine.trim().isEmpty()) {
            return "ERROR: Empty command";
        }

        String[] tokens  = commandLine.trim().split("\\s+");
        String   command = tokens[0].toUpperCase();

        switch (command) {
            case "REGISTER":
                if (tokens.length != 3) {
                    return "ERROR: Usage: REGISTER <ServiceName> <IPAddress>";
                }
                return handleRegister(tokens[1], tokens[2]);

            case "RESOLVE":
                if (tokens.length != 2) {
                    return "ERROR: Usage: RESOLVE <ServiceName>";
                }
                return handleResolve(tokens[1]);

            case "DEREGISTER":
                if (tokens.length != 2) {
                    return "ERROR: Usage: DEREGISTER <ServiceName>";
                }
                return handleDeregister(tokens[1]);

            default:
                return "ERROR: Unknown command '" + tokens[0] + "'";
        }
    }

    // ── REGISTER ────────────────────────────────────────────────────
    /**
     * Registers a service name → IP mapping.
     *
     * Rules enforced:
     *   1. No two DIFFERENT service names may share the same IP address.
     *   2. Re-registering the same name with the same IP is allowed (idempotent).
     *   3. Re-registering the same name with a NEW IP updates the entry.
     *
     * The entire check-then-put is synchronized on the registry to
     * eliminate TOCTOU (time-of-check / time-of-use) race conditions.
     */
    private String handleRegister(String serviceName, String ipAddress) {
        synchronized (NameServer.registry) {
            for (java.util.Map.Entry<String, String> entry : NameServer.registry.entrySet()) {
                if (entry.getValue().equals(ipAddress) && !entry.getKey().equals(serviceName)) {
                    return "ERROR: IP " + ipAddress
                            + " is already registered to service '" + entry.getKey() + "'";
                }
            }
            NameServer.registry.put(serviceName, ipAddress);
            return "SUCCESS: Registered '" + serviceName + "' at " + ipAddress;
        }
    }

    // ── RESOLVE ─────────────────────────────────────────────────────
    /**
     * Looks up the IP address for the given service name.
     * ConcurrentHashMap.get() is atomic, so no extra synchronization needed.
     */
    private String handleResolve(String serviceName) {
        String ipAddress = NameServer.registry.get(serviceName);
        if (ipAddress != null) {
            return ipAddress;
        }
        return "ERROR: Service '" + serviceName + "' not found";
    }

    // ── DEREGISTER ──────────────────────────────────────────────────
    /**
     * Removes a service from the registry.
     * Synchronized to prevent a concurrent REGISTER from interleaving
     * during the check-and-remove.
     */
    private String handleDeregister(String serviceName) {
        synchronized (NameServer.registry) {
            if (NameServer.registry.remove(serviceName) != null) {
                return "SUCCESS: Deregistered '" + serviceName + "'";
            }
            return "ERROR: Service '" + serviceName + "' not found for deregistration";
        }
    }
}