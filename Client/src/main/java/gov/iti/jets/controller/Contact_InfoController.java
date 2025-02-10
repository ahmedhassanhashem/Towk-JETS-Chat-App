package gov.iti.jets.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.sound.sampled.SourceDataLine;

import gov.iti.jets.config.RMIConfig;
import gov.iti.jets.dao.ContactDAOInterface;
import gov.iti.jets.dto.UserDTO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Contact_InfoController  {

    @FXML
    private Label nameLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private TextArea bioTextArea;
    @FXML
    private Button blockButton;
    @FXML
    private ImageView blockIcon;
    @FXML
    private ImageView imageinfo;
    @FXML
    private VBox contactContainer;
    @FXML
    private ListView<UserDTO> listView;

    private ContactDAOInterface contactDAO;
    private UserDTO userDTO = new UserDTO();

    public Label getNameLabel(){
        return  nameLabel;
    }

    public Label getphoneLabel(){
        return  phoneLabel;
    }

    public TextArea bio(){
        return  bioTextArea;
    }

    public Label getModeLabel(){
        return  categoryLabel;
    }

    public ImageView getImage(){
        return imageinfo;
    }

   


    public void setUserDTO(UserDTO user) {
       
        this.userDTO = user;
        System.out.println(userDTO.getPhone());

    }
    private UserDTO contactdto =new UserDTO();
    

    public void setContact(UserDTO user){
            contactdto=user;
            System.out.println(contactdto.getPhone());
            updateBlockButtonState();
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
        } catch (JAXBException | IOException ex) {             
            ex.printStackTrace();
        }

        if (p != null) {
            String ip = p.getIp();
            int port = p.getPort();

            try {
                Registry reg = LocateRegistry.getRegistry(ip, port);
                contactDAO = (ContactDAOInterface) reg.lookup("contactDAO");
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
            }
        }
     
                Circle clip = new Circle();
                clip.setRadius(60); 
                clip.setCenterX(60);
                clip.setCenterY(60);
                imageinfo.setClip(clip);
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) contactContainer.getScene().getWindow();
        stage.close();
    }

    
    private void updateBlockButtonState() {
        if (userDTO != null && contactdto != null) {
            try {
                boolean isBlocked = contactDAO.isContactBlocked(userDTO.getPhone(), contactdto.getPhone());
                boolean isBlocker = contactDAO.isUserBlocker(userDTO.getPhone(), contactdto.getPhone());

                Platform.runLater(() -> {
                    if (isBlocked) {
                        if (isBlocker) {
                            blockButton.setText("Unblock");
                            blockButton.setStyle("-fx-background-color: #ff4444;"); // Red for blocked state
                            blockButton.setDisable(false);
                        } else {
                            blockButton.setText("Blocked");
                            blockButton.setStyle("-fx-background-color: #cccccc;"); // Grey for view-only
                            blockButton.setDisable(true);
                        }
                    } else {
                        blockButton.setText("Block");
                        blockButton.setStyle(""); // Default style
                        blockButton.setDisable(false);
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    blockButton.setDisable(true);
                    blockButton.setText("Error");
                });
            }
        }
    }

    @FXML
    private void blockButton(ActionEvent event) {
        if (userDTO == null || contactdto == null) {
            System.out.println("User data is not set");
            return;
        }

        try {
            boolean isCurrentlyBlocked = contactDAO.isContactBlocked(userDTO.getPhone(), contactdto.getPhone());
            boolean isBlocker = contactDAO.isUserBlocker(userDTO.getPhone(), contactdto.getPhone());

            if (isCurrentlyBlocked ) {
                // Handle unblock
                if (contactDAO.unblockContact(userDTO.getPhone(), contactdto.getPhone())) {
                    blockButton.setText("Block");
                    blockButton.setStyle(""); // Reset to default style

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Contact Unblocked");
                    alert.setHeaderText(null);
                    alert.setContentText("Contact has been unblocked successfully.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to unblock contact. Please try again.");
                    alert.showAndWait();
                }
            } else if (!isCurrentlyBlocked) {
                // Handle block
                if (contactDAO.blockContact(userDTO.getPhone(), contactdto.getPhone())) {
                    blockButton.setText("Unblock");
                    blockButton.setStyle("-fx-background-color: #ff4444;"); // Red for blocked state

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Contact Blocked");
                    alert.setHeaderText(null);
                    alert.setContentText("Contact has been blocked successfully.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to block contact. Please try again.");
                    alert.showAndWait();
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while updating contact status.");
            alert.showAndWait();
        }
    }


    
    }
