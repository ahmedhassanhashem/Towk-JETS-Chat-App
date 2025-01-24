package gov.iti.jets.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;




import java.io.*;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        // if(checkUser(phoneField.getText())){
        //     System.out.println("User already there!");
        //     return;
        // }
        // if(password2Field.equals(passwordField) &&insertRecord( phoneField.getText(), nameField.getText(), countryField.getValue(), genderField.getValue()
        // , emailField.getText(), dateField.getValue(), passwordField.getText(), true, "Online", "Available"))
        // stage.setScene(dashScene);
        // else{
        //     System.out.println("Not correct");
        // }
        // System.out.println(countryField.getValue());

    }
    
    @FXML
    private void gotoSingin(){
        stage.setScene(signin);
    }

    // @FXML
    // private ListView<FlowPane> list;

    // @FXML
    // private TextField txtF;

    // private TreeItem<FlowPane> allroot;
    // @FXML
    // private void onEnter(ActionEvent event){
       
    // }





    @FXML
    private void initialize() {
        male.setUserData(Gender.MALE);
        female.setUserData(Gender.FEMALE);
        // Statement stmt;
        // ResultSet re;
        // try {
        // stmt =
        // con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
        // re = stmt.executeQuery("SELECT * FROM employee");
        // } catch (SQLException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

    }
    /* 
        private Boolean insertRecord(String phone, String name, String country, String gender, String email, LocalDate birthdate, String password,
            Boolean firstLogin, String userStatus, String userMode)
             {
                if(phone.length() !=11 || name.length() ==0 || password.length() ==0 || gender.length()==0 
                || birthdate ==null ||gender.length()==0 || country.length()==0)return false;
        String sql2 = "INSERT INTO `User` (`phone`, `name` , `country`, `gender`, `email`, `birthdate`,`password`, `firstLogin`, `userStatus` , `userMode`) VALUES(?,?,?,?,?,?,?,?,?,?)";
        try {
        PreparedStatement preparedStatement = con.prepareStatement(sql2);
            java.sql.Date birthdate2= java.sql.Date.valueOf(birthdate.toString());
            preparedStatement.setString(1, phone);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, country);
            preparedStatement.setString(4, gender);
            preparedStatement.setString(5, email);
            preparedStatement.setDate(6, birthdate2);
            preparedStatement.setString(7, password);
            preparedStatement.setBoolean(8, firstLogin);
            preparedStatement.setString(9, userStatus);
            preparedStatement.setString(10, userMode);
            preparedStatement.executeUpdate();
            System.out.println("Record inserted successfully.");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

    */

}
