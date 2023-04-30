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

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //цикл авторизации
                    while (true) {
                        String msg = in.readUTF();
                        // client -> server /login Bob
                        if(msg.startsWith("/login ")) {
                            String usernameFromLogin = msg.split("\\s")[1];
                            if(server.isNickBusy(usernameFromLogin)) {
                                sendMessage("/login_failed current nickname already in use");
                                continue;
                            }

                            username = usernameFromLogin;
                            sendMessage("/login_ok " + username);
                            server.subscribe(ClientHandler.this);
                            break;
                        }
                    }
                    // цикл общения
                    while (true) {
                        String msg = in.readUTF();
                        server.broadcastMessage(username + ": " + msg);
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    disconnect();
                }
            }
        }).start();

    }

    public void sendMessage(String message) throws IOException{
        out.writeUTF(message);
    }


    private void disconnect() {
        server.unsubscribe(this);
        if(socket != null) {
            try {
                socket.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getUsername() {
        return username;
    }
}
