package com.company;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static ExecutorService pool = Executors.newFixedThreadPool(5); //Thread pool for preallocate

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(8080));
        System.out.println("Listening to 8080");

        ArrayList<Socket> clientList = new ArrayList<>();
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.printf("Client connected %s:%d\n", clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
            Thread thread = new Thread(new ClientHandler(clientSocket, clientList));
            thread.start();
            clientList.add(clientSocket);
            pool.execute(thread);

        }
    }
}


class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ArrayList<Socket> clientList;

    public ClientHandler(Socket clientSocket, ArrayList<Socket> clientList) {
        this.clientSocket = clientSocket;
        this.clientList = clientList;
    }

    @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(clientSocket.getInputStream());
            while (scanner.hasNextLine()) {
                String message = scanner.nextLine();
                System.out.printf("GOT from user message: %s\n", message);
                for (Socket client : clientList) {
                    System.out.printf("send to user port %s message: %s\n", client.getPort(), message);
                    message = "user port : " + clientSocket.getPort() + " say :" + message;
                    client.getOutputStream().write((message + "\n").getBytes());
                    client.getOutputStream().flush();
                }
                clientSocket.getOutputStream().flush();
            }
            clientSocket.close();
        } catch (Exception e) {
            // do nothing
        }
    }
}