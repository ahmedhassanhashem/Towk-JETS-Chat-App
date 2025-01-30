package gov.iti.jets.dto;

import java.io.Serializable;

public class AnnouncementDTO implements Serializable {
    
    private int announcementID;
    private String announcementTitle;
    private String announcementContent;


    /**
     * @return int return the announcementID
     */
    public int getAnnouncementID() {
        return announcementID;
    }

    /**
     * @param announcementID the announcementID to set
     */
    public void setAnnouncementID(int announcementID) {
        this.announcementID = announcementID;
    }

    /**
     * @return String return the announcementTitle
     */
    public String getAnnouncementTitle() {
        return announcementTitle;
    }

    /**
     * @param announcementTitle the announcementTitle to set
     */
    public void setAnnouncementTitle(String announcementTitle) {
        this.announcementTitle = announcementTitle;
    }

    /**
     * @return String return the announcementContent
     */
    public String getAnnouncementContent() {
        return announcementContent;
    }

    /**
     * @param announcementContent the announcementContent to set
     */
    public void setAnnouncementContent(String announcementContent) {
        this.announcementContent = announcementContent;
    }

}