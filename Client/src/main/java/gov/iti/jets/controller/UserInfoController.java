package gov.iti.jets.controller;

import java.io.*;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import gov.iti.jets.dao.UserDAO;
import gov.iti.jets.dto.UserDTO;

public class UserInfoController {
    
    @FXML
    private BorderPane borderPane3;

    @FXML
    private Text userName, userEmail, userGender, dateOfBirth, userCountry, userMode, userNameH;

    @FXML
    private ImageView userImage;

    @FXML
    private Button exitBtn;

    private double xOffset = 0;
    private double yOffset = 0;

    private UserDTO userDTO = new UserDTO();
    private UserDAO userDAO = new UserDAO();

 
    public void setUserDTO(UserDTO user) {
        // if (user == null) {
        //     System.out.println("Error: Trying to set a null UserDTO");
        //     return;
        // }
        this.userDTO = user;
        //System.out.println("UserInfoController userDTO set: " + user.getPhone());
    }
    
    public void applyRoundedCorners(Region region, double radius) {
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(region.getWidth(), region.getHeight());
        clip.setArcWidth(radius * 2);
        clip.setArcHeight(radius * 2);
        region.setClip(clip);
    
        region.widthProperty().addListener((obs, oldVal, newVal) -> clip.setWidth(newVal.doubleValue()));
        region.heightProperty().addListener((obs, oldVal, newVal) -> clip.setHeight(newVal.doubleValue()));
    }
    @FXML
    public void initialize() {
        Circle clip = new Circle();
        clip.setRadius(60); 
        clip.setCenterX(60);
        clip.setCenterY(60);
        userImage.setClip(clip);
    }

    public void loadUserData() {

        UserDTO retrievedUser = userDAO.read(userDTO);

        if (retrievedUser != null) {
            String fullName = retrievedUser.getName();
            String firstName = extractFirstName(fullName);
            userName.setText(firstName);
            userNameH.setText(fullName);
            userEmail.setText(retrievedUser.getEmail());
            userGender.setText(retrievedUser.getGender().toString());
            dateOfBirth.setText(retrievedUser.getBirthdate().toString());
            userCountry.setText(retrievedUser.getCountry());
            userMode.setText(retrievedUser.getUserMode() != null ? retrievedUser.getUserMode().toString() : "Unknown");

            if(userDTO.getUserPicture() != null)
               userImage.setImage(new Image(new ByteArrayInputStream(userDTO.getUserPicture())));
        } else {
            System.out.println("User not found!");
        }
    }
    

    public void makeWindowDraggable(Stage stage) {
        borderPane3.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        borderPane3.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) borderPane3.getScene().getWindow();
        stage.close();
    }

    private String extractFirstName(String fullName) {
        String[] names = fullName.split("\\s+"); 
    
        if (names.length > 0) {
            return names[0].trim(); 
        } else {
            return ""; 
        }
    }
}