/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package nameclient;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Nameclient {

    public static void main(String[] args) {
        String host = "localhost"; // same machine
        int port = 1234;           // MUST match your server

        try (
            Socket socket = new Socket(host, port);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in)
        ) {

            System.out.println("Connected to Name Server!");

            while (true) {
                System.out.println("\nCommands:");
                System.out.println("1. REGISTER");
                System.out.println("2. RESOLVE");
                System.out.println("3. DEREGISTER");
                System.out.println("4. EXIT");

                System.out.print("Enter choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                String command = "";

                switch (choice) {
                    case 1:
                        System.out.print("Service Name: ");
                        String service = scanner.nextLine();

                        System.out.print("IP Address: ");
                        String ip = scanner.nextLine();

                        command = "REGISTER " + service + " " + ip;
                        break;

                    case 2:
                        System.out.print("Service Name: ");
                        service = scanner.nextLine();

                        command = "RESOLVE " + service;
                        break;

                    case 3:
                        System.out.print("Service Name: ");
                        service = scanner.nextLine();

                        command = "DEREGISTER " + service;
                        break;

                    case 4:
                        System.out.println("Exiting...");
                        return;

                    default:
                        System.out.println("Invalid option.");
                        continue;
                }

                // Send to server
                out.println(command);

                // Receive response
                String response = in.readLine();
                System.out.println("Server: " + response);
            }

        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }
}
