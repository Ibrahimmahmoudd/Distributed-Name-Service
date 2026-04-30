package Group_SourceCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                String response = processCommand(inputLine);
                out.println(response);
            }
        } catch (IOException e) {
            System.err.println("Client handler exception: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Failed to close socket: " + e.getMessage());
            }
        }
    }

    private String processCommand(String commandLine) {
        String[] tokens = commandLine.trim().split("\\s+");
        if (tokens.length == 0) return "ERROR: Invalid Command";

        String command = tokens[0].toUpperCase();

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
                return "ERROR: Unknown Command";
        }
    }

    // Handling register operation and ip conflicts 
     
    private String handleRegister(String serviceName, String ipAddress) {
        synchronized (NameServer.registry) {
            // iterate through the registry to check if the IP is already there
            for (java.util.Map.Entry<String, String> entry : NameServer.registry.entrySet()) {
                if (entry.getValue().equals(ipAddress)) {
                    // If the IP is found, check if it belongs to a different service name
                    if (!entry.getKey().equals(serviceName)) {
                        return "ERROR: IP Already Registered to another service (" + entry.getKey() + ")";
                    }
                }
            }
            
            // register or update the service name and its associated IP
            NameServer.registry.put(serviceName, ipAddress);
            
            return "SUCCESS: Registered " + serviceName + " at " + ipAddress;
        }
    }

   // resolve operation handling
    private String handleResolve(String serviceName) {
        String ipAddress = NameServer.registry.get(serviceName);
        if (ipAddress != null) {
            return ipAddress; 
        } else {
            return "ERROR: Not Found";
        }
    }

    // handling deregister operation
    private String handleDeregister(String serviceName) {
        if (NameServer.registry.remove(serviceName) != null) {
            return "SUCCESS: Deregistered " + serviceName;
        } else {
            return "ERROR: Service not found for deregistration";
        }
    }
}
