package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception {
        Socket clientSocket = new Socket();
        System.out.println("Listening");
        clientSocket.connect(new InetSocketAddress("127.0.0.1", 8080));
        System.out.printf("Connected from port %d\n", clientSocket.getLocalPort());
        ServerConnection serverConnection = new ServerConnection(clientSocket);
        (new Thread(serverConnection)).start();

        Scanner userInput = new Scanner(System.in);
        while (true) {

            System.out.println("Enter Command: \n");
            String command = userInput.nextLine();
            if (command.equalsIgnoreCase("exit")) break;

            clientSocket.getOutputStream().write((command + "\n").getBytes());
            clientSocket.getOutputStream().flush();
        }
        clientSocket.close();
    }


}

class ServerConnection implements Runnable {
    private Socket serverSocket;
    private BufferedReader in;
    private PrintWriter out;

    public ServerConnection(Socket s) {
        serverSocket = s;
        try {
            in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            out = new PrintWriter(serverSocket.getOutputStream(), true);
        } catch (IOException e) {
        }
    }

    @Override
    public void run() {
        String msg = null;
        try {
            while (true) {
                msg = in.readLine();
                System.out.println("Message " + msg + "\n>");
            }
        } catch (Exception e) {
        }
    }
}
