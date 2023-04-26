package kz.timka.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class App
{
    public static void main( String[] args ) throws Exception {

        try(ServerSocket serverSocket = new ServerSocket(8189)) {
            System.out.println("Сервер запущен на порту 8189. Ожидаем клиентов");
            Socket socket = serverSocket.accept(); //blocking
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            System.out.println("клиент подключился");

            String msg;
            while (true) {
                msg = in.readUTF();
                System.out.print(msg + "\n");
                out.writeUTF("ECHO: " + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
