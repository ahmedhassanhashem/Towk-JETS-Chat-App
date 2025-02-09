package gov.iti.jets.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

public class ExceptionUtility {
    private ExceptionUtility(){}
    public static void alert(){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setHeaderText("Failed to load");
        alert.setTitle("FAILURE");
        alert.setContentText("Connection timeout");
        alert.initStyle(StageStyle.TRANSPARENT);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.showAndWait();
    }
    public static void exception (Exception e){
        System.err.println(e.getMessage());
    }
}
