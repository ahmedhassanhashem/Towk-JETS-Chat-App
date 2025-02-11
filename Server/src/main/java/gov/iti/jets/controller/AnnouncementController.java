package gov.iti.jets.controller;

import java.rmi.RemoteException;

import gov.iti.jets.dao.AnnouncementDAO;
import gov.iti.jets.dto.AnnouncementDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;

public class AnnouncementController {
private  AnnouncementDAO announcementDAO;
   @FXML
    private TextArea announcementContent;
    @FXML
    private TextField announcementTitle;

    @FXML
    private void handleSendAnnouncement(){
        String title = announcementTitle.getText();
        String content = announcementContent.getText();
        if(!title.isBlank() && !content.isBlank()){

            // AnnouncementDAO c;
        AnnouncementDTO announcement = new AnnouncementDTO();
        announcement.setAnnouncementTitle(convertToHtml(title));
        announcement.setAnnouncementContent(convertToHtml(content));
        AnnouncementDTO createdAnnouncement = null;
        try {
            createdAnnouncement = announcementDAO.create(announcement);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
         
        if (createdAnnouncement != null) {
            System.out.println("Announcement created successfully.");
            announcementContent.clear();
            announcementTitle.clear();
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setContentText("Announcement created successfully.");
            alert.setHeaderText("Announced");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        } else 
            System.out.println("Error: Announcement creation failed.");
            
            
        

        }else{
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText("Empty Fields");
            alert.setHeaderText("ERROR!!");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
        

    }

    public void setAnnouncementDAO( AnnouncementDAO announcementDAO){
       this.announcementDAO =  announcementDAO;
    }
    @FXML
    private void initialize() {

    }

    private String convertToHtml(String text) {
        return "<html><body>" + 
               text.replace("\n", "<br/>")
                   .replace(" ", "&nbsp;") + 
               "</body></html>";
    }
}
