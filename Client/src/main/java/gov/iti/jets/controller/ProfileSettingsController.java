package gov.iti.jets.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

import gov.iti.jets.dao.UserDAOInterface;
import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.dto.UserMode;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class ProfileSettingsController {

    private UserDTO userDTO = new UserDTO();
    // private UserDAO userDAO = new UserDAO();
        private UserDAOInterface userDAO;
        File file;
    private Stage stage;

    @FXML
    TextField name;
    @FXML
    TextArea bio;
    @FXML
    ComboBox<String> userMode;
    @FXML
    ImageView image;

    public void setUserDTO(UserDTO user) {
        this.userDTO = user;
        if(userDTO.getUserPicture() != null)
            image.setImage(new Image(new ByteArrayInputStream(userDTO.getUserPicture())));

    }
    public void setStage(Stage s){
        stage =s;
    }


    @FXML
    private void updateUser() {
        // if (name.getText().isBlank()) {
        //     Alert alert = new Alert(Alert.AlertType.ERROR);
        //     alert.setTitle("Validation Error");
        //     alert.setContentText( "Name cannot be empty.");
        //     alert.showAndWait();
        //     return;
        // }

        String bioField = (!bio.getText().isBlank()) ? bio.getText() : null;
        UserMode userModeList = (userMode.getValue() != null) ? UserMode.valueOf(userMode.getValue()) : null;
        // String bioField  = bio.getText();
        String nmae =(!name.getText().isBlank()) ? name.getText():null;

        int rowsUpdated=0;
        try {
            if(file != null){
                byte[] imageBytes;
                    imageBytes = imageToByteArray(file);
                    userDTO.setUserPicture(imageBytes);
                    userDAO.updatePicture(userDTO.getUserID(),file.getName() ,imageBytes);

                
                
            }
            rowsUpdated = userDAO.update(userDTO.getUserID(), nmae, bioField, userModeList);
            if(nmae != null)userDTO.setName(nmae);
        } catch (RemoteException  e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

        // if (rowsUpdated > 0) {
        //     System.out.println("Success Profile updated successfully!");
        // } else {
        //     System.out.println("Error Failed to update profile.");
        // }
        name.clear();
        bio.clear();
        userMode.getSelectionModel().clearSelection();
    }

    @FXML
    private void updateImage(){
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        
        file = chooser.showOpenDialog(stage);
        
        if(file != null){
            byte[] imageBytes;
            try {
                imageBytes = imageToByteArray(file);
                Image img = new Image(file.toURI().toString());
                image.setImage(img);
            } catch (IOException e) { e.printStackTrace();}
            
            
        }
    }

    private byte[] imageToByteArray(File imgFile) throws FileNotFoundException, IOException{
        try(FileInputStream input = new FileInputStream(imgFile);
            ByteArrayOutputStream output = new ByteArrayOutputStream()){
                byte[] buffer = new byte[1024];
                int length;
                while((length = input.read(buffer)) != -1)
                    output.write(buffer, 0 , length);
                
                return output.toByteArray();
        }
    }

 
    @FXML
    private void initialize(){
                Properties props = new Properties();
        
        try (InputStream input = getClass().getResourceAsStream("/rmi.properties")) {
            if (input == null) {
                throw new IOException("Properties file not found");
            }
            props.load(input);
        } catch (IOException ex) {
        }


        String ip = props.getProperty("rmi_ip");
        int port = Integer.parseInt(props.getProperty("rmi_port"));
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
        Circle clip = new Circle();
        clip.setRadius(25); 
        clip.setCenterX(25);
        clip.setCenterY(25);
        image.setClip(clip);
    }
}
