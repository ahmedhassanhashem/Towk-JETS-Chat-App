/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package gov.iti.jets.controller;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import gov.iti.jets.dao.AnnouncementDAOInterface;
import gov.iti.jets.dto.AnnouncementDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author Amina
 */
public class AnnouncementCardController implements Initializable {

    private AnnouncementDTO announcement;
    @FXML
    private HBox DynamicHBox;
    @FXML
    private Label LabelAnnouncement;
    @FXML
    private Text TextAnnouncemet;
    // @FXML
    // private Button buttonAnnuncement;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    
    }

      public void setAnnouncementData(AnnouncementDTO announcement) {
    
          this.announcement = announcement;
        LabelAnnouncement.setText(announcement.getAnnouncementTitle());
        TextAnnouncemet.setText(announcement.getAnnouncementContent());
    }

    // @FXML
    // private void deleteAnnouncement(ActionEvent event) {

    // }
    
    
}
