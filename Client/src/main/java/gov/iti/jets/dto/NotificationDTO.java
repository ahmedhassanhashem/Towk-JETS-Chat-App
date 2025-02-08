package gov.iti.jets.dto;

import java.io.Serializable;

public class NotificationDTO implements Serializable {
    
    private int notificationID;
    private int userID;
    private int messageID;


    

    /**
     * @return int return the notificationID
     */
    public int getNotificationID() {
        return notificationID;
    }

    /**
     * @param notificationID the notificationID to set
     */
    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    /**
     * @return int return the userID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * @param userID the userID to set
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * @return int return the messageID
     */
    public int getMessageID() {
        return messageID;
    }

    /**
     * @param messageID the messageID to set
     */
    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

}