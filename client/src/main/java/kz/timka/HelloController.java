package kz.timka;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    TextField msgField;

    @FXML
    TextArea txtArea;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        while (true) {
                            String msg = in.readUTF();
                            txtArea.appendText(msg + "\n");
                        }
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t1.start();


        } catch (IOException e) {
            throw new RuntimeException("Unable to connect to server localhost 8189");
        }

    }

    public void sendMsg() {
        try {
            out.writeUTF(msgField.getText());
            msgField.clear();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to send msg", ButtonType.OK);
            alert.showAndWait();
        }
    }
}