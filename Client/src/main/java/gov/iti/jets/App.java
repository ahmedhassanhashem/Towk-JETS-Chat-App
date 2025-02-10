package gov.iti.jets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import gov.iti.jets.client.ClientImplNot;
import gov.iti.jets.client.ClientInt;
import gov.iti.jets.controller.LoginPController;
import gov.iti.jets.controller.entreeController;
import gov.iti.jets.dto.UserDTO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {
    public static ArrayList<ClientInt> clientImpls = new ArrayList<>() ;
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

        // File XMLfile = new File("C:/.chatLogged/user.xml");
        String userHome = System.getProperty("user.home");
            File XMLfile = new File(userHome + "/.chatLogged/user.xml");
        if (XMLfile.exists() && XMLfile.length() > 0) {
            try {
                JAXBContext context = JAXBContext.newInstance(UserDTO.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                UserDTO user = (UserDTO) unmarshaller.unmarshal(XMLfile);
                // File picfile = new File("C:/.chatLogged/user.pic");
                File picfile = new File(userHome + "/.chatLogged/user.pic");
                if(picfile.exists()){
                    FileInputStream fIn = new FileInputStream(picfile);
                    user.setUserPicture(fIn.readAllBytes());
                    fIn.close();
                }
                FXMLLoader dashLoader2 = new FXMLLoader(getClass().getResource("/screens/loginP.fxml"));
                VBox dashBoard2 = dashLoader2.load();

                LoginPController dashController2 = dashLoader2.getController();

                dashScene = new Scene(dashBoard2, width+200, height+100);
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
        stage.setOnCloseRequest((e)->{
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch();
    }
@Override
public void stop() throws Exception {
    for (ClientInt clientInt : clientImpls) {
        
        if (clientInt != null) {
            try {
                
                // Unexport the object forcibly
                UnicastRemoteObject.unexportObject(clientInt, true);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // for (Thread t : Thread.getAllStackTraces().keySet()) {
    //     System.out.println("Thread: " + t.getName() + " | State: " + t.getState());
    // }
    
    System.exit(0);
    Platform.exit();
        super.stop();
}

}
