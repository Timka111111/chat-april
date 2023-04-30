package kz.timka;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.*;
import java.net.*;


public class Controller {

    @FXML
    TextField msgField, usernameField;

    @FXML
    TextArea txtArea;

    @FXML
    HBox msgPanel,loginPanel;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String username;

    public void setUsername(String username) {
        this.username = username;
        if(username != null) {
            loginPanel.setVisible(false);
            loginPanel.setManaged(false);
            msgPanel.setVisible(true);
            msgPanel.setManaged(true);

        } else {
            loginPanel.setVisible(true);
            loginPanel.setManaged(true);
            msgPanel.setVisible(false);
            msgPanel.setManaged(false);

        }
    }
    public void login() {
        if(socket == null || socket.isClosed()) {
            connect();
        }

        if(usernameField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Имя пользователя не должно быть пустым" , ButtonType.OK);
            alert.showAndWait();
            return;
        }

        try {
            out.writeUTF("/login " + usernameField.getText());
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        // цикл авторизации
                        while (true) {
                            String msg = in.readUTF();
                            if(msg.startsWith("/")) {
                                // client -> server /login Bob
                                // server -> client /login_ok Bob
                                // /login_ok Bob
                                if(msg.startsWith("/login_ok ")) {
                                    setUsername(msg.split("\\s")[1]);
                                    break;
                                }
                                if(msg.startsWith("/login_failed ")) {
                                    // server -> client /login_failed this nickname already in use
                                    String cause = msg.split("\\s", 2)[1];
                                    txtArea.appendText(cause + "\n");
                                }
                            }
                        }
                        // цикл общения с сервером
                        while (true) {
                            String msg = in.readUTF();
                            txtArea.appendText(msg + "\n");
                        }
                    }catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        disconnect();
                    }
                }
            });
            t1.start();


        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Невозможно подключиться к серверу", ButtonType.OK);
            alert.showAndWait();
        }
    }


    public void sendMsg() {
        try {
            out.writeUTF(msgField.getText());
            msgField.clear();
            msgField.requestFocus();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to send msg", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void disconnect() {
        setUsername(null);
        if(socket != null) {
            try {
                socket.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}