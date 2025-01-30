package gov.iti.jets.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import gov.iti.jets.dao.UserDAO;
import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.dto.UserMode;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class ProfileSettingsController {

    private UserDTO userDTO = new UserDTO();
    private UserDAO userDAO = new UserDAO();
    private Stage stage;

    @FXML
    TextField name;
    @FXML
    TextArea bio;
    @FXML
    ComboBox<String> userMode;
    @FXML
    ImageView image;

    public void setUserDTO(UserDTO user) {
        this.userDTO = user;
        if(userDTO.getUserPicture() != null)
            image.setImage(new Image(new ByteArrayInputStream(userDTO.getUserPicture())));

    }
    public void setStage(Stage s){
        stage =s;
    }


    @FXML
    private void updateUser() {
        if (name.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setContentText( "Name cannot be empty.");
            alert.showAndWait();
            return;
        }

        String bioField = (!bio.getText().isBlank()) ? bio.getText() : "Hi im using towk!";
        UserMode userModeList = (userMode.getValue() != null) ? UserMode.valueOf(userMode.getValue()) : UserMode.AVAILABLE;
        
        int rowsUpdated = userDAO.update(userDTO.getUserID(), name.getText(), bioField, userModeList);

        if (rowsUpdated > 0) {
            System.out.println("Success Profile updated successfully!");
        } else {
            System.out.println("Error Failed to update profile.");
        }
        name.clear();
        bio.clear();
        userMode.getSelectionModel().clearSelection();
    }

    @FXML
    private void updateImage(){
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        
        File file = chooser.showOpenDialog(stage);
        
        if(file != null){
            byte[] imageBytes;
            try {
                imageBytes = imageToByteArray(file);
                Image img = new Image(file.toURI().toString());
                image.setImage(img);
                userDAO.updatePicture(userDTO.getUserID(),file.getName() ,imageBytes);
            } catch (IOException e) { e.printStackTrace();}
            
            
        }
    }

    private byte[] imageToByteArray(File imgFile) throws FileNotFoundException, IOException{
        try(FileInputStream input = new FileInputStream(imgFile);
            ByteArrayOutputStream output = new ByteArrayOutputStream()){
                byte[] buffer = new byte[1024];
                int length;
                while((length = input.read(buffer)) != -1)
                    output.write(buffer, 0 , length);
                
                return output.toByteArray();
        }
    }

 
    @FXML
    private void initialize(){
        
    }
}
