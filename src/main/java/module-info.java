module ni.edu.uam.prueba_corte_2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens ni.edu.uam.prueba_corte_2 to javafx.fxml;
    exports ni.edu.uam.prueba_corte_2;
    exports ni.edu.uam.prueba_corte_2.Application;
    opens ni.edu.uam.prueba_corte_2.Application to javafx.fxml;
    exports ni.edu.uam.prueba_corte_2.Controller;
    opens ni.edu.uam.prueba_corte_2.Controller to javafx.fxml;
}