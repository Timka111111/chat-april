package kz.timka;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.*;
import java.net.*;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    @FXML
    TextField msgField, loginField;

    @FXML
    TextArea txtArea;

    @FXML
    HBox msgPanel, loginPanel;

    @FXML
    ListView clientsList;

    @FXML
    PasswordField passwordField;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String username;

    public void setUsername(String username) {
        this.username = username;
        boolean usernameIsNull = username == null;
        loginPanel.setVisible(usernameIsNull);
        loginPanel.setManaged(usernameIsNull);
        msgPanel.setVisible(!usernameIsNull);
        msgPanel.setManaged(!usernameIsNull);
        clientsList.setVisible(!usernameIsNull);
        clientsList.setManaged(!usernameIsNull);

    }

    public void login() {

        if (loginField.getText().isEmpty()) {
            showErrorAlert("Имя пользователя не должно быть пустым");
            return;
        }

        if (passwordField.getText().isEmpty()) {
            showErrorAlert("Пароль не должен быть пустым");
            return;
        }

        if (socket == null || socket.isClosed()) {
            connect();
        }

        try {
            out.writeUTF("/login " + loginField.getText() + " " + passwordField.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {

                try {
                    // цикл авторизации
                    while (true) {
                        String msg = in.readUTF();
                        if (msg.startsWith("/")) {
                            // client -> server /login Bob
                            // server -> client /login_ok Bob
                            // /login_ok Bob
                            if (msg.startsWith("/login_ok ")) {
                                setUsername(msg.split("\\s")[1]);
                                break;
                            }
                            if (msg.startsWith("/login_failed ")) {
                                // server -> client /login_failed this nickname already in use
                                String cause = msg.split("\\s", 2)[1];
                                txtArea.appendText(cause + "\n");
                            }
                        }
                    }
                    // цикл общения с сервером
                    while (true) {
                        String msg = in.readUTF();
                        if (msg.startsWith("/")) {
                            if (msg.startsWith("/clients_list ")) {
                                String[] tokens = msg.split("\\s");

                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        clientsList.getItems().clear();
                                        for (int i = 1; i < tokens.length; i++) {
                                            clientsList.getItems().add(tokens[i]);
                                        }
                                    }
                                });

                            }
                            continue;
                        }

                        txtArea.appendText(msg + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    disconnect();
                }

            }).start();


        } catch (IOException e) {
            showErrorAlert("Невозможно подключиться к серверу");
        }
    }


    public void sendMsg() {
        try {
            out.writeUTF(msgField.getText());
            msgField.clear();
            msgField.requestFocus();
        } catch (IOException e) {
            showErrorAlert("Unable to send msg");
        }
    }

    private void disconnect() {
        setUsername(null);
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.setTitle("chat-april");
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUsername(null);
    }
}