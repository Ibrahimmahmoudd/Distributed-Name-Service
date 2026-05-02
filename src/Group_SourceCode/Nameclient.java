package Group_SourceCode;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * NameClient.java
 * ─────────────────────────────────────────────────────────────────
 * A simple interactive CLI client that connects to the NameServer
 * and lets the user REGISTER, RESOLVE, or DEREGISTER services.
 *
 * Can be launched:
 *   - Via Main.java (recommended)
 *   - Directly: java Group_SourceCode.NameClient [host] [port]
 * ─────────────────────────────────────────────────────────────────
 */
public class Nameclient {

    // ── Called by Main.java ─────────────────────────────────────────
    public static void start(String host, int port) {
        run(host, port);
    }

    // ── Direct entry point ──────────────────────────────────────────
    public static void main(String[] args) {
        String host = (args.length >= 1) ? args[0] : "localhost";
        int    port = 1234;
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port argument. Using default 1234.");
            }
        }
        run(host, port);
    }

    // ── Core client loop ────────────────────────────────────────────
    private static void run(String host, int port) {
        try (
            Socket         socket  = new Socket(host, port);
            BufferedReader in      = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter    out     = new PrintWriter(socket.getOutputStream(), true);
            Scanner        scanner = new Scanner(System.in)
        ) {
            System.out.println("[NameClient] Connected to Name Server at " + host + ":" + port);

            while (true) {
                printMenu();

                System.out.print("Enter choice: ");
                String input = scanner.nextLine().trim();

                int choice;
                try {
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input — please enter a number (1-4).\n");
                    continue;
                }

                String command = "";

                switch (choice) {
                    case 1: // REGISTER
                        System.out.print("Service Name: ");
                        String svcRegister = scanner.nextLine().trim();
                        if (svcRegister.isEmpty()) {
                            System.out.println("Service name cannot be empty.\n");
                            continue;
                        }
                        System.out.print("IP Address:   ");
                        String ip = scanner.nextLine().trim();
                        if (ip.isEmpty()) {
                            System.out.println("IP address cannot be empty.\n");
                            continue;
                        }
                        command = "REGISTER " + svcRegister + " " + ip;
                        break;

                    case 2: // RESOLVE
                        System.out.print("Service Name: ");
                        String svcResolve = scanner.nextLine().trim();
                        if (svcResolve.isEmpty()) {
                            System.out.println("Service name cannot be empty.\n");
                            continue;
                        }
                        command = "RESOLVE " + svcResolve;
                        break;

                    case 3: // DEREGISTER
                        System.out.print("Service Name: ");
                        String svcDeregister = scanner.nextLine().trim();
                        if (svcDeregister.isEmpty()) {
                            System.out.println("Service name cannot be empty.\n");
                            continue;
                        }
                        command = "DEREGISTER " + svcDeregister;
                        break;

                    case 4: // EXIT
                        System.out.println("[NameClient] Disconnecting. Goodbye!");
                        return;

                    default:
                        System.out.println("Invalid choice — please enter 1, 2, 3, or 4.\n");
                        continue;
                }

                // Send command to server
                out.println(command);
                System.out.println("[Sent]     " + command);

                // Receive and display server response
                String response = in.readLine();
                System.out.println("[Server]   " + response + "\n");
            }

        } catch (ConnectException ce) {
            System.err.println("[NameClient] Could not connect to server at "
                    + host + ":" + port + ". Is the server running?");
        } catch (IOException e) {
            System.err.println("[NameClient] Connection error: " + e.getMessage());
        }
    }

    private static void printMenu() {
        System.out.println(" ______________________________ ");
        System.out.println("|       Name Service Client    |");
        System.out.println("|______________________________|");
        System.out.println("|  1. REGISTER   service       |");
        System.out.println("|  2. RESOLVE    service       |");
        System.out.println("|  3. DEREGISTER service       |");
        System.out.println("|  4. EXIT                     |");
        System.out.println(" ______________________________ ");
    }
}