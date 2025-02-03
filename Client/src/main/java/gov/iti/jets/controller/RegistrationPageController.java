package gov.iti.jets.controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.iti.jets.config.RMIConfig;
import gov.iti.jets.dao.UserDAOInterface;
import gov.iti.jets.dto.Gender;
import gov.iti.jets.dto.UserDTO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
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
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RegistrationPageController {

    private Stage stage;
    private Scene signin;
    private Scene dashScene;
    // private UserDAO userDao = new UserDAO();
    private UserDAOInterface userDAO;

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

    @FXML
    private Label invalid2;

    public void setLoginsScene(Scene l) {
        loginScene = l;
    }

    public void setStage(Stage stage) {
        this.stage = stage;

    }

    @FXML
    private void next() {
        UserDTO user = new UserDTO();
        if (phoneField.getText() == null || phoneField.getText() == "") {
            showWarn("Empty phone Number");
            return;
        }
        if (!phoneField.getText().matches("^\\d[\\d]{9}\\d$")) {
            showWarn("Phone Number must be a number DUH!");
            return;
        }
        if (phoneField.getText().length() != 11) {
            showWarn("Phone Number Length must be 11");
            return;
        }
        user.setPhone(phoneField.getText());

        if (nameField.getText() == null || nameField.getText().length() == 0) {
            showWarn("Empty name !");
            return;
        }
        user.setName(nameField.getText());

        if (countryField.getValue() == null || countryField.getValue().length() == 0) {
            showWarn("Country can't be empty !");
            return;
        }

        user.setCountry(countryField.getValue());
        if (gender.getSelectedToggle() == null) {
            // System.out.println("select gender");
            showWarn("select gender");
            return;
        }
        user.setGender((Gender) gender.getSelectedToggle().getUserData());
        //System.out.println(gender.getSelectedToggle().getUserData());

        if(!(emailField.getText() == null) && !emailField.getText().isEmpty()){
            if(validate(emailField.getText()))
            user.setEmail(emailField.getText());
            else{
                showWarn("Enter a valid email!");
                return;
            }
        }else{
            user.setEmail(emailField.getText());

        }
        if (dateField.getValue() != null) {
            user.setBirthdate(java.sql.Date.valueOf(dateField.getValue().toString()));
            if(java.sql.Date.valueOf(dateField.getValue().toString()).after(java.sql.Date.valueOf(LocalDate.now().toString()))){
                showWarn("Date can't be in the future");
                return;
            }
        }else{
            showWarn("Empty birthdate !");
            return;
        }
        String pass = passwordField.getText();
        if (pass == null || pass.length() == 0) {
            showWarn("Empty Password!");
            return;
        }
        if (pass.length() < 8) {
            showWarn("Password length must be 8 or more !");
            return;
        }
        if (!Pattern.compile("\\W").matcher(pass).find()) {
            showWarn("Password must contain at least one symbol");
            return;
        }
        if (!Pattern.compile("[a-z]").matcher(pass).find()) {
            showWarn("Password must contain at least one small character");
            return;
        }
        if (!Pattern.compile("[A-Z]").matcher(pass).find()) {
            showWarn("Password must contain at least one Capital character");
            return;
        }
        if (!Pattern.compile("\\d").matcher(pass).find()) {
            showWarn("Password must contain at least one digit");
            return;
        }
        if (!passwordField.getText().equals(password2Field.getText())) {
            showWarn("Not matching passwords!");
            return;
        }
        user.setPassword(passwordField.getText());
        UserDTO ret = null;
        try {
            ret = userDAO.read(user);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (ret != null) {
            invalid.setVisible(true);
            phoneField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(3),
                    new BorderWidths(2), new Insets(-2))));
            return;
        }
        try {
            user = userDAO.create(user);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (user == null) {
            System.out.println("err");
            showWarn("Unknown error!");
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
            dashController.setDashboardController(dashController);
            stage.setScene(dashScene);
            try {
                JAXBContext context = JAXBContext.newInstance(UserDTO.class);
                Marshaller marshaller = context.createMarshaller(); 
                
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); 
                
                
                File XMLfile = new File("C:/.chatLogged/user.xml");

                File parentDir = XMLfile.getParentFile();
                if (!parentDir.exists()) {
                    parentDir.mkdirs();
                }
                if(!XMLfile.exists())
                {
                    XMLfile.createNewFile();
                }

                
                marshaller.marshal(user, XMLfile); 
            } catch (JAXBException ex) {
                ex.printStackTrace();
            } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

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
            ex.printStackTrace();
        } catch (URISyntaxException e1) {
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
        male.setUserData(Gender.MALE);
        female.setUserData(Gender.FEMALE);

        phoneField.setOnKeyPressed((e) -> {

            invalid.setVisible(false);
            phoneField.setBorder(null);
        });
        password2Field.setOnKeyReleased((e) -> {
            if (!passwordField.getText().equals(password2Field.getText())) {
                invalid2.setVisible(true);
                password2Field.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(3),
                        new BorderWidths(2), new Insets(-2))));
            } else {
                invalid2.setVisible(false);
                password2Field.setBorder(null);
            }
        });
        passwordField.setOnKeyReleased((e) -> {
            if (!passwordField.getText().equals(password2Field.getText())) {
                invalid2.setVisible(true);
                password2Field.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(3),
                        new BorderWidths(2), new Insets(-2))));
            } else {
                invalid2.setVisible(false);
                password2Field.setBorder(null);
            }
        });

    }

    private void showWarn(String msg) {
        Label secondLabel = new Label(msg);
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(secondLabel);

        Scene secondScene = new Scene(secondaryLayout, 230, 100);

        Stage newWindow = new Stage();
        newWindow.initOwner(stage);
        newWindow.initModality(Modality.APPLICATION_MODAL);
        newWindow.setTitle("Error!");
        newWindow.setScene(secondScene);

        newWindow.setX(stage.getX() + 200);
        newWindow.setY(stage.getY() + 100);

        newWindow.show();
    }
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches();
}

}
