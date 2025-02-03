package gov.iti.jets.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

import gov.iti.jets.config.RMIConfig;
// import gov.iti.jets.dao.UserDAO;
import gov.iti.jets.dao.UserDAOInterface;
import gov.iti.jets.dto.UserDTO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class LoginPController {

    private UserDTO udto;
    private Stage stage;
    // private UserDAO userDao = new UserDAO();
    private UserDAOInterface userDAO;

    @FXML
    private Label errorLabel;

    @FXML
    private Label loginNewUser;

    @FXML
    private Label nameField;

    @FXML
    private TextField passwordField;

    @FXML
    private ImageView profileImage;

    @FXML
    private Button loginButton;

    @FXML
    void loginWithPassword(ActionEvent event) {
        udto.setPassword(passwordField.getText());
        UserDTO udto2=null;
        try {
            udto2 = userDAO.read(udto);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (udto2 != null) {

            FXMLLoader dashLoader = new FXMLLoader(getClass().getResource("/screens/base.fxml"));
            BorderPane dashBoard = null;
            try {
                dashBoard = dashLoader.load();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            DashboardController dashController = dashLoader.getController();
            dashController.setStage(stage);

            Scene dashScene = new Scene(dashBoard, 600 + 200, 480 + 100);
            // System.out.println(dashScene);
            dashController.setUserDTO(udto2);
            dashController.setDashScene(dashScene);
            stage.setScene(dashScene);

        } else {
            Platform.runLater(() -> {
                errorLabel.setVisible(true);
                passwordField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID,
                        new CornerRadii(3), new BorderWidths(2), new Insets(-2))));
                System.out.println("Wrong pass");
            });
        }

    }

    @FXML
    private void initialize() {
        RMIConfig p = null;
                try { 
            File XMLfile = new File(getClass().getResource("/rmi.xml").toURI()); 
            JAXBContext context = JAXBContext.newInstance(RMIConfig.class);
            Unmarshaller unmarshaller = context.createUnmarshaller(); 
            p = (RMIConfig) unmarshaller.unmarshal(XMLfile);
            // System.out.println(p.getIp() +" " + p.getPort());
        } catch (JAXBException ex) {
            System.out.println(ex.getMessage());
        } catch (URISyntaxException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String ip =p.getIp();
        int port = p.getPort();

                Registry reg;
        try {
            reg = LocateRegistry.getRegistry(ip,port);
            userDAO = (UserDAOInterface) reg.lookup("userDAO");

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        passwordField.setOnKeyPressed((e) -> {

            errorLabel.setVisible(false);
            passwordField.setBorder(null);
        });
        loginButton.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                setSaveAccelerator(loginButton);
            }
        });
        loginNewUser.setOnMouseEntered(event -> {
            loginNewUser.setTextFill(Color.DARKBLUE);
            
            loginNewUser.getScene().setCursor(javafx.scene.Cursor.HAND);
        });

        loginNewUser.setOnMouseExited(event -> {
            loginNewUser.setTextFill(Color.BLUE);
            loginNewUser.getScene().setCursor(javafx.scene.Cursor.DEFAULT);
        });

        loginNewUser.setOnMouseClicked(event -> {
            try {
                Stage stage1 = new Stage();
                int width = 640,height = 480;
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
        });

        Circle clip = new Circle();
        clip.setRadius(100); 
        clip.setCenterX(100);
        clip.setCenterY(100);
        profileImage.setClip(clip);
    }

    public void setProfileImage(byte[] imageData) {
        ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
        Image image = new Image(bis);
        this.profileImage.setImage(image);

    }

    public void setNameField(String nameField) {
        this.nameField.setText(nameField);
    }

    private void setSaveAccelerator(Button button) {
        if (button == null) {
            System.out.println("Button is null! ");
        }
        Scene scene = button.getScene();
        if (scene == null) {
            throw new IllegalArgumentException(
                    "setSaveAccelerator must be called when a button is attached to a scene");
        }

        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.ENTER),
                new Runnable() {
            @FXML
            public void run() {

                button.fire();
            }
        });
    }

    public void setUdto(UserDTO udto) {
        this.udto = udto;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
