package gov.iti.jets.controller;

import java.net.URL;
import java.util.ResourceBundle;

import gov.iti.jets.dto.UserDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Amina
 */
public class Group_InfoController implements Initializable {

    @FXML
    private Button exitBtn;
    @FXML
    private ImageView groupImage;
    @FXML
    private Label groupNameLabel;
    @FXML
    private Label creatorLabel;
    @FXML
    private Label memberCountLabel;
    ObservableList<UserDTO> contacts = FXCollections.observableArrayList();
    @FXML
    private ListView<UserDTO> listView;
    @FXML
    private Button addMemberButton;
    @FXML
    private Button exitButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void closeWindow(ActionEvent event) {
    }
    
}
