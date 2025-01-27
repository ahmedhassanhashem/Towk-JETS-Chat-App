package gov.iti.jets.controller;

import gov.iti.jets.dao.AnnouncementDAO;
import gov.iti.jets.dto.AnnouncementDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;

public class AnnouncementController {

   @FXML
    private TextArea announcementContent;
    @FXML
    private TextField announcementTitle;

    @FXML
    private void handleSendAnnouncement(){
        String title = announcementTitle.getText();
        String content = announcementContent.getText();
        if(!title.isBlank() && !content.isBlank()){

            AnnouncementDAO announcementDAO = new AnnouncementDAO();
        AnnouncementDTO announcement = new AnnouncementDTO();
        announcement.setAnnouncementTitle(title);
        announcement.setAnnouncementContent(content);
        AnnouncementDTO createdAnnouncement = announcementDAO.create(announcement);
         
        if (createdAnnouncement != null) {
            System.out.println("Announcement created successfully.");
            announcementContent.clear();
            announcementTitle.clear();
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setContentText("Announcement created successfully.");
            alert.setHeaderText("Announced");
            alert.showAndWait();
            alert.initModality(Modality.APPLICATION_MODAL);
        } else 
            System.out.println("Error: Announcement creation failed.");
            
            
        

        }else{
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText("Empty Fields");
            alert.setHeaderText("ERROR!!");
            alert.showAndWait();
            alert.initModality(Modality.APPLICATION_MODAL);
        }
        

    }
}
