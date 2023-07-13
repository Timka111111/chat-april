package kz.timka.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private List<ClientHandler> clients;
    private AuthenticationProvider authenticationProvider;
    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
        this.authenticationProvider = new DbAuthenticationProvider();
        this.authenticationProvider.init();
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
        } finally {
            this.authenticationProvider.shutdown();
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClientList();
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientList();
    }

    public void broadcastMessage(String message) {
        for(ClientHandler clientHandler : clients) {
            clientHandler.sendMessage(message);
        }
    }

    public boolean isUserOnline(String username) {
        for(ClientHandler clientHandler : clients) {
            if(clientHandler.getUsername().equals(username)) {
                return true;
            }
        }

        return false;
    }

    public void sendPrivateMessage(ClientHandler sender, String receiver, String message) {
        for(ClientHandler clientHandler : clients) {
            if(clientHandler.getUsername().equals(receiver)) {
                clientHandler.sendMessage("От: " + sender.getUsername() + " Сообщение: " + message);
                sender.sendMessage("Пользователю: " + receiver + " Сообщение: " + message);
                return;
            }
        }
        sender.sendMessage("Невозможно отправить сообщение пользователю " + receiver + " Такого пользователя нет в сети");

    }

    public void broadcastClientList() {
        StringBuilder stringBuilder = new StringBuilder("/clients_list ");
        for(ClientHandler c : clients) {
            stringBuilder.append(c.getUsername()).append(" ");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        // /clients_list Bob Alex James
        String clientList = stringBuilder.toString();
        for(ClientHandler clientHandler : clients) {
            clientHandler.sendMessage(clientList);
        }

    }

    public AuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }
}
