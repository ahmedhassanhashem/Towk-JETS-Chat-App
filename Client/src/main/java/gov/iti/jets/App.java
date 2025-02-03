package gov.iti.jets;

import java.io.File;
import java.io.IOException;

import gov.iti.jets.controller.LoginPController;
import gov.iti.jets.controller.entreeController;
import gov.iti.jets.dto.UserDTO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // stage.setTitle("Browsar");

        int width = 640, height = 480;
        FXMLLoader dashLoader = new FXMLLoader(getClass().getResource("/screens/entreeBase.fxml"));
        GridPane dashBoard = dashLoader.load();
        entreeController dashController = dashLoader.getController();

        var dashScene = new Scene(dashBoard, width, height);
        dashController.setMyScene(dashScene);
        dashController.setStage(stage);

        File XMLfile = new File("C:/.chatLogged/user.xml");
        if (XMLfile.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(UserDTO.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                UserDTO user = (UserDTO) unmarshaller.unmarshal(XMLfile);
                FXMLLoader dashLoader2 = new FXMLLoader(getClass().getResource("/screens/loginP.fxml"));
                VBox dashBoard2 = dashLoader2.load();

                LoginPController dashController2 = dashLoader2.getController();

                dashScene = new Scene(dashBoard2, width, height);
                dashController2.setStage(stage);
                String fullName = user.getName();
                String[] names = fullName.split("\\s+");
                String firstName = "";
                if (names.length > 0) {
                    firstName = names[0].trim();
                }

                dashController2.setNameField(firstName + "!");
                if (user.getUserPicture() != null) {
                    dashController2.setProfileImage(user.getUserPicture());
                }
                user.setPassword(null);
                dashController2.setUdto(user);
            } catch (JAXBException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        stage.setScene(dashScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
