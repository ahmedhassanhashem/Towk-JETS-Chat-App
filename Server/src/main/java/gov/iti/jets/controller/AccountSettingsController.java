package gov.iti.jets.controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import gov.iti.jets.config.RMIConfig;
import gov.iti.jets.dao.AdminDAO;
import gov.iti.jets.dao.UserDAO;
import gov.iti.jets.dao.UserDAOInterface;
import gov.iti.jets.dto.UserDTO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AccountSettingsController {

    private Stage stage;
    private UserDTO userDTO = new UserDTO();
    private AdminDAO userDAO;

    @FXML
    private PasswordField password;
    @FXML
    private PasswordField confirmPassword;
    @FXML
    private Label invalid2;

    public void setUserDTO(UserDTO user) {
        this.userDTO = user;

    }

    public void setStage(Stage s) {
        stage = s;
    }

    @FXML
    private void deleteAccount() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText("Are you sure about deleting your account!");
        alert.setHeaderText("Delete Account");
        alert.initModality(Modality.APPLICATION_MODAL);

        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

        // make the user logout
        if (result == ButtonType.OK) {

                userDAO.delete(userDTO.getUserID());

            try {
                int width = 640, height = 480;
                FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/screens/Login.fxml"));
                GridPane rootLogin = loginLoader.load();
                LoginController loginController = loginLoader.getController();
                Scene loginScene = new Scene(rootLogin, width, height);
                loginController.setStage(stage);
                stage.setScene(loginScene);
                stage.show();
            } catch (IOException ex) {
            }

        }

    }

    @FXML
    private void updatePassword() {
        if (!password.getText().equals(confirmPassword.getText())) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText("Incorrect Password");
            alert.setHeaderText("ERROR!!");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        } else {

                userDAO.updatePassword(userDTO.getUserID(), password.getText());

            password.clear();
            confirmPassword.clear();
            int width = 640, height = 480;

            FXMLLoader serveLoader = new FXMLLoader(getClass().getResource("/screens/Server.fxml"));
            GridPane server;
            try {
                server = serveLoader.load();
                ServerController serverController = serveLoader.getController();
                serverController.setStage(stage);
                Scene serverScene = new Scene(server, width + 200, height + 20);
                Platform.runLater(() -> stage.setScene(serverScene));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    @FXML
    private void initialize() {

            userDAO = new AdminDAO();

        confirmPassword.setOnKeyReleased((e) -> {
            if (!password.getText().equals(confirmPassword.getText())) {
                invalid2.setVisible(true);
                confirmPassword
                        .setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(3),
                                new BorderWidths(2), new Insets(-2))));
            } else {
                invalid2.setVisible(false);
                confirmPassword.setBorder(null);
            }
        });
        password.setOnKeyReleased((e) -> {
            if (!password.getText().equals(confirmPassword.getText())) {
                invalid2.setVisible(true);
                confirmPassword
                        .setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(3),
                                new BorderWidths(2), new Insets(-2))));
            } else {
                invalid2.setVisible(false);
                confirmPassword.setBorder(null);
            }
        });
    }
}
