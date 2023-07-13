package kz.timka.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;


    public ClientHandler(Server server, Socket socket) throws IOException {
        this.socket = socket;
        this.server = server;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());

        new Thread(() -> {

            try {
                //цикл авторизации
                while (true) {
                    String msg = in.readUTF();
                    // client -> server /login Bob bob100
                    if (msg.startsWith("/login ")) {
                        String[] tokens = msg.split("\\s+");
                        if(tokens.length != 3) {
                            sendMessage("/login_failed Введите имя пользователя и пароль");
                            continue;
                        }

                        String login = tokens[1];
                        String password = tokens[2];

                        String username = server.getAuthenticationProvider()
                                .getNicknameByLoginAndPassword(login, password);

                        if(username == null) {
                            sendMessage("/login_failed Введен некорректный логин/пароль");
                            continue;
                        }

                        if (server.isUserOnline(username)) {
                            sendMessage("/login_failed Учетная запись уже используются");
                            continue;
                        }

                        this.username = username;
                        sendMessage("/login_ok " + username);
                        server.subscribe(ClientHandler.this);
                        break;
                    }
                }
                // цикл общения
                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith("/")) {
                        executeCommand(msg);
                        continue;
                    }
                    server.broadcastMessage(username + ": " + msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }

        }).start();

    }

    private void executeCommand(String cmd) {
        // /p Bob Hello, Bob!
        if (cmd.startsWith("/p ")) {
            String[] tokens = cmd.split("\\s+", 3);
            if(tokens.length != 3) {
                sendMessage("Server: Введена некорректная команда");
                return;
            }
            server.sendPrivateMessage(this, tokens[1], tokens[2]);
            return;
        }
        if (cmd.startsWith("/change_nick ")) {

            String[] tokens = cmd.split("\\s+");
            if(tokens.length != 2) {
                sendMessage("Server: Введена некорректная команда");
                return;
            }
            String newNickname = tokens[1];
            if(server.getAuthenticationProvider().icNickBusy(newNickname)) {
                sendMessage("Server: Такой никнейм уже занят");
                return;
            }

            server.getAuthenticationProvider().changeNick(username, newNickname);
            username = newNickname;
            sendMessage("Server: Вы изменили ник на " + username);
            server.broadcastClientList();


        }

    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
            disconnect();
        }
    }


    private void disconnect() {
        server.unsubscribe(this);
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getUsername() {
        return username;
    }
}
