package gov.iti.jets.controller;


import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;

import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import gov.iti.jets.dao.UserDAO;
import gov.iti.jets.dto.Gender;
import gov.iti.jets.dto.UserDTO;

public class RegistrationPageController {
    
    private Stage stage;
    private Scene signin;
    private Scene dashScene;
    private UserDAO userDao = new UserDAO();

    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private ToggleGroup gender;
    @FXML
    private TextField emailField;
    @FXML
    private DatePicker dateField;
    @FXML
    private ComboBox<String> countryField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField password2Field;
    @FXML
    private RadioButton male;
    @FXML
    private RadioButton female;
    @FXML
    private Label invalid;





    
    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void setSignIn(Scene s){
        signin = s;
    }

    public void setDashScene(Scene s){
        dashScene = s;
    }

    @FXML
    private void next(){
        UserDTO user = new UserDTO();
        user.setPhone(phoneField.getText());
        user.setName(nameField.getText());
        user.setCountry( countryField.getValue());
        if(gender.getSelectedToggle() ==null){
            System.out.println("select gender");
            return;
        }
        user.setGender((Gender)gender.getSelectedToggle().getUserData());
        System.out.println(gender.getSelectedToggle().getUserData());

        user.setEmail(emailField.getText());
        user.setBirthdate( java.sql.Date.valueOf(dateField.getValue().toString()));
        user.setPassword( passwordField.getText());
        userDao.read(user);
        if(userDao.read(user) !=null){
            invalid.setVisible(true);
            phoneField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(2), new Insets(-2))));
           return;
        }
        user = userDao.create(user);
        if(user ==null){
            System.out.println("err");
        }else{

            stage.setScene(dashScene);
            
        }


    }
    
    @FXML
    private void gotoSingin(){
        stage.setScene(signin);
    }






    @FXML
    private void initialize() {
        male.setUserData(Gender.MALE);
        female.setUserData(Gender.FEMALE);


    }


}
