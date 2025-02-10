package gov.iti.jets.controller;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;
import java.io.*;
import java.net.URISyntaxException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import gov.iti.jets.config.RMIConfig;
// import gov.iti.jets.dao.UserDAO;
import gov.iti.jets.dao.UserDAOInterface;
import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.dto.UserStatus;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class LoginPageController {

    private Stage stage;
    private Scene signup;
    private Scene dashScene;
    // private UserDAO userDao = new UserDAO();
    private UserDAOInterface userDAO;

    private DashboardController dashController;
    private Scene loginScene;

    @FXML
    private Button loginButton;

    @FXML
    private Label invalid;

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField passwordField;

    public void setLoginsScene(Scene l) {
        loginScene = l;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        var w = stage.widthProperty().multiply(0.3);
        phoneField.prefWidthProperty().bind(w);
        passwordField.prefWidthProperty().bind(w);

        // System.out.println(stage);
    }

    @FXML
    private void signIn(ActionEvent event) {

        UserDTO user = new UserDTO();
        user.setPhone(phoneField.getText());
        user.setPassword(passwordField.getText());
        try {
            user = userDAO.read(user);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch(NullPointerException e){
            ExceptionUtility.alert();
        }
        if (user != null) {

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
            // System.out.println(dashScene);
            dashController.setUserDTO(user);
            dashController.setStage(stage);
            dashController.setDashScene(dashScene);
            dashController.setDashboardController(dashController);
            stage.setScene(dashScene);
            try {
                userDAO.changeStatus(user.getUserID(),UserStatus.ONLINE.toString());
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
                        try {
                JAXBContext context = JAXBContext.newInstance(UserDTO.class);
                Marshaller marshaller = context.createMarshaller(); 
                
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); 
                
                
                File XMLfile = new File("C:/.chatLogged/user.xml");
                File picfile = new File("C:/.chatLogged/user.pic");
                
                File parentDir = XMLfile.getParentFile();
                if (!parentDir.exists()) {
                    parentDir.mkdirs();
                }
                if(!XMLfile.exists())
                {
                    XMLfile.createNewFile();
                }
                if(!picfile.exists()){
                    picfile.createNewFile();
                }
                byte[] picture = user.getUserPicture();
if (picture != null && picture.length > 0) {

    FileOutputStream fOut = new FileOutputStream(picfile);
    if(picfile.getTotalSpace()>1)
    fOut.write(user.getUserPicture());
    fOut.flush();
    fOut.close();
}
                
                marshaller.marshal(user, XMLfile); 
            } catch (JAXBException ex) {
                ex.printStackTrace();
            } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

        } else {
            Platform.runLater(() -> {
                invalid.setVisible(true);
                phoneField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(3),
                        new BorderWidths(2), new Insets(-2))));
                passwordField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID,
                        new CornerRadii(3), new BorderWidths(2), new Insets(-2))));
                System.out.println("Wrong user/pass");
            });
        }

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
        phoneField.setOnKeyPressed((e) -> {

            invalid.setVisible(false);
            phoneField.setBorder(null);
            passwordField.setBorder(null);
        });
        passwordField.setOnKeyPressed((e) -> {
            invalid.setVisible(false);
            phoneField.setBorder(null);
            passwordField.setBorder(null);
        });

        loginButton.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null)
                setSaveAccelerator(loginButton);
        });


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
        // System.out.println(dashScene);

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
}
