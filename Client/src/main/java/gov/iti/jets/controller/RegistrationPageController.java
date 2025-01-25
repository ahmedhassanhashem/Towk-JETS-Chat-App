package gov.iti.jets.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

import java.io.IOException;

import gov.iti.jets.dao.UserDAO;
import gov.iti.jets.dto.Gender;
import gov.iti.jets.dto.UserDTO;

public class RegistrationPageController {

    private Stage stage;
    private Scene signin;
    private Scene dashScene;
    private UserDAO userDao = new UserDAO();
    private DashboardController dashController;
    private Scene loginScene;

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

    public void setLoginsScene(Scene l) {
        loginScene = l;
    }

    public void setStage(Stage stage) {
        this.stage = stage;

    }

    @FXML
    private void next() {
        UserDTO user = new UserDTO();
        user.setPhone(phoneField.getText());
        user.setName(nameField.getText());
        user.setCountry(countryField.getValue());
        if (gender.getSelectedToggle() == null) {
            System.out.println("select gender");
            return;
        }
        user.setGender((Gender) gender.getSelectedToggle().getUserData());
        System.out.println(gender.getSelectedToggle().getUserData());

        user.setEmail(emailField.getText());
        user.setBirthdate(java.sql.Date.valueOf(dateField.getValue().toString()));
        user.setPassword(passwordField.getText());
        userDao.read(user);
        if (userDao.read(user) != null) {
            invalid.setVisible(true);
            phoneField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(3),
                    new BorderWidths(2), new Insets(-2))));
            return;
        }
        user = userDao.create(user);
        if (user == null) {
            System.out.println("err");
        } else {
            FXMLLoader dashLoader = new FXMLLoader(getClass().getResource("/screens/base.fxml"));
            BorderPane dashBoard = null;
            try {
                dashBoard = dashLoader.load();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            dashController = dashLoader.getController();

            dashScene = new Scene(dashBoard, 600 + 200, 480 + 100);
            dashController.setStage(stage);
            dashController.setDashScene(dashScene);
            dashController.setLoginsScene(loginScene);
            dashController.setUserDTO(user);
            
            stage.setScene(dashScene);

        }

    }

    @FXML
    private void initialize() {
        male.setUserData(Gender.MALE);
        female.setUserData(Gender.FEMALE);

    }

}
