package Group_SourceCode;

import java.util.Scanner;

/**
 * Main.java
 * ─────────────────────────────────────────────────────────────────
 * Entry point for the Distributed Name Service application.
 *
 * Run instructions (from the project root):
 *   Compile:
 *       javac src/Group_SourceCode/*.java -d bin
 *
 *   Launch (interactive CLI):
 *       java -cp bin Group_SourceCode.Main
 *
 *   Or launch directly in a specific mode:
 *       java -cp bin Group_SourceCode.Main server
 *       java -cp bin Group_SourceCode.Main client
 *       java -cp bin Group_SourceCode.Main client <host> <port>
 *
 * ─────────────────────────────────────────────────────────────────
 */
public class Main {

    // ── Default connection parameters ──────────────────────────────
    private static final String DEFAULT_HOST = "localhost";
    private static final int    DEFAULT_PORT = 1234;

    public static void main(String[] args) {

        // ── If arguments are provided, skip the menu ───────────────
        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "server":
                    launchServer();
                    return;

                case "client":
                    String host = (args.length >= 2) ? args[1] : DEFAULT_HOST;
                    int    port = DEFAULT_PORT;
                    if (args.length >= 3) {
                        try {
                            port = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid port '" + args[2] + "'. Using default: " + DEFAULT_PORT);
                        }
                    }
                    launchClient(host, port);
                    return;

                default:
                    System.err.println("Unknown argument: " + args[0]);
                    printUsage();
                    System.exit(1);
            }
        }

        // ── Interactive startup menu ────────────────────────────────
        printBanner();

        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        while (choice != 1 && choice != 2) {
            System.out.println(" _________________________________ ");
            System.out.println("|   Distributed Name Service      |");
            System.out.println("|_________________________________|");
            System.out.println("|  1. Start as SERVER             |");
            System.out.println("|  2. Start as CLIENT             |");
            System.out.println("|  3. Exit                        |");
            System.out.println(" _________________________________ ");
            System.out.print("Enter choice: ");

            String line = scanner.nextLine().trim();
            try {
                choice = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter 1, 2, or 3.\n");
                continue;
            }

            switch (choice) {
                case 1:
                    launchServer();
                    break;

                case 2:
                    // Ask for host/port or use defaults
                    System.out.print("Server host [" + DEFAULT_HOST + "]: ");
                    String hostInput = scanner.nextLine().trim();
                    String host = hostInput.isEmpty() ? DEFAULT_HOST : hostInput;

                    System.out.print("Server port [" + DEFAULT_PORT + "]: ");
                    String portInput = scanner.nextLine().trim();
                    int port = DEFAULT_PORT;
                    if (!portInput.isEmpty()) {
                        try {
                            port = Integer.parseInt(portInput);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid port. Using default: " + DEFAULT_PORT);
                        }
                    }
                    launchClient(host, port);
                    break;

                case 3:
                    System.out.println("Goodbye.");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid option. Please enter 1, 2, or 3.\n");
                    choice = -1; // reset to keep the loop going
            }
        }

        scanner.close();
    }

    // ── Delegates to NameServer ─────────────────────────────────────
    private static void launchServer() {
        System.out.println("\n[Main] Starting Name Service Server...\n");
        NameServer.main(new String[]{});
    }

    // ── Delegates to NameClient ─────────────────────────────────────
    private static void launchClient(String host, int port) {
        System.out.println("\n[Main] Connecting to Name Service at " + host + ":" + port + "...\n");
        Nameclient.start(host, port);
    }

    // ── Helpers ─────────────────────────────────────────────────────
    private static void printBanner() {
        System.out.println("===========================================");
        System.out.println("          Distributed Name Service         ");
        System.out.println(" CSE352 - Parallel and Distributed Systems ");
        System.out.println("===========================================");
        System.out.println();
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java -cp bin Group_SourceCode.Main              (interactive menu)");
        System.out.println("  java -cp bin Group_SourceCode.Main server");
        System.out.println("  java -cp bin Group_SourceCode.Main client [host] [port]");
    }
}
