package kz.timka.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private List<ClientHandler> clients;
    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту 8189");
            while (true) {
                System.out.println("Ждем нового клиента");
                Socket socket = serverSocket.accept(); //blocking
                System.out.println("клиент подключился");
                new ClientHandler(this, socket);

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        System.out.println(clients);
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        System.out.println(clients);
    }

    public void broadcastMessage(String message) throws IOException{
        for(ClientHandler clientHandler : clients) {
            clientHandler.sendMessage(message);
        }
    }

    public boolean isNickBusy(String username) {
        for(ClientHandler clientHandler : clients) {
            if(clientHandler.getUsername().equals(username)) {
                return true;
            }
        }

        return false;
    }
}
