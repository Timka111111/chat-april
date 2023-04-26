module kz.timka {
    requires javafx.controls;
    requires javafx.fxml;


    opens kz.timka to javafx.fxml;
    exports kz.timka;
}