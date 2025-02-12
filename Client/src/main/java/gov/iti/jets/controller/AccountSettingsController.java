package gov.iti.jets.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import gov.iti.jets.config.RMIConfig;
import gov.iti.jets.dao.UserDAOInterface;
import gov.iti.jets.dto.UserDTO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AccountSettingsController {

    private Stage stage;
    private UserDTO userDTO = new UserDTO();
    private UserDAOInterface userDAO;

    @FXML
    private PasswordField password;
    @FXML
    private PasswordField confirmPassword;

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

        //make the user logout
        if (result == ButtonType.OK) {
            try {
                userDAO.delete(userDTO.getUserID());
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }catch (NullPointerException er){
                ExceptionUtility.alert();
            }
            try {
                Stage stage1 = new Stage();
                int width = 640, height = 480;
                stage1.setMinHeight(height);
                stage1.setMinWidth(width);
                FXMLLoader dashLoader = new FXMLLoader(getClass().getResource("/screens/entreeBase.fxml"));
                GridPane dashBoard = dashLoader.load();
                entreeController dashController = dashLoader.getController();
                var dashScene = new Scene(dashBoard, width, height);
                dashController.setMyScene(dashScene);
                dashController.setStage(stage1);
                stage1.setScene(dashScene);
                stage1.show();
            } catch (IOException ex) {
            }
            stage.close();

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
            try {
                userDAO.updatePassword(userDTO.getUserID(), password.getText());
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }catch (NullPointerException er){
                ExceptionUtility.alert();
            }
            password.clear();
            confirmPassword.clear();
        }

    }

    @FXML
    private void initialize() {
        RMIConfig p = null;
                try { 
            InputStream inputStream = getClass().getResourceAsStream("/rmi.xml");
            JAXBContext context = JAXBContext.newInstance(RMIConfig.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            p = (RMIConfig) unmarshaller.unmarshal(inputStream);
            inputStream.close();
            // System.out.println(p.getIp() +" " + p.getPort());
        } catch (JAXBException ex) {
            ex.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String ip =p.getIp();
        int port = p.getPort();
        
        Registry reg;
        try {
            reg = LocateRegistry.getRegistry(ip, port);
            userDAO = (UserDAOInterface) reg.lookup("userDAO");

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
