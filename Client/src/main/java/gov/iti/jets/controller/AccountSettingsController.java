package gov.iti.jets.controller;

import gov.iti.jets.dao.UserDAO;
import gov.iti.jets.dto.UserDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class AccountSettingsController {

    private Stage stage;
    private UserDTO userDTO = new UserDTO();
    private UserDAO userDAO = new UserDAO();
    
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField confirmPassword;

    public void setUserDTO(UserDTO user) {
        this.userDTO = user;

    }
  
    public void setStage(Stage s){
        stage =s;
    }


    @FXML
    private void deleteAccount(){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText("Are you sure about deleting your account!");
        alert.setHeaderText("Delete Account");
        alert.initModality(Modality.APPLICATION_MODAL);

        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);


        //make the user logout
        if (result == ButtonType.OK){
            userDAO.delete(userDTO.getUserID());
            

        }
        
    }

    @FXML
    private void updatePassword() {
        if(!password.getText().equals(confirmPassword.getText())){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText("Incorrect Password");
            alert.setHeaderText("ERROR!!");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }else{
            userDAO.updatePassword(userDTO.getUserID(), password.getText());
            password.clear();
            confirmPassword.clear();
        }

    }
}
