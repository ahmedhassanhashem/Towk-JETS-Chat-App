package gov.iti.jets.dto;

import java.io.Serializable;

public class MessageDTO implements Serializable{

    private int messsageID;
    private String messageContent;
    private int chatID;
    private int userID;
    private java.sql.Date messageDate;  // sql date not util date
    private int attachmentID;

    
    public MessageDTO(){}

    public MessageDTO(String messageContent, int chatID, int userID, java.sql.Date messageDate) {
        this.messageContent = messageContent;
        this.chatID = chatID;
        this.userID = userID;
        this.messageDate = messageDate;
        this.attachmentID = 0;
    }

    public MessageDTO(String messageContent, int chatID, int userID, java.sql.Date messageDate, int attachmentID) {
        this.messageContent = messageContent;
        this.chatID = chatID;
        this.userID = userID;
        this.messageDate = messageDate;
        this.attachmentID = attachmentID;
    }

    /**
     * @return int return the messsageID
     */
    public int getMesssageID() {
        return messsageID;
    }

    /**
     * @param messsageID the messsageID to set
     */
    public void setMesssageID(int messsageID) {
        this.messsageID = messsageID;
    }

    /**
     * @return String return the messageContent
     */
    public String getMessageContent() {
        return messageContent;
    }

    /**
     * @param messageContent the messageContent to set
     */
    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    /**
     * @return int return the chatID
     */
    public int getChatID() {
        return chatID;
    }

    /**
     * @param chatID the chatID to set
     */
    public void setChatID(int chatID) {
        this.chatID = chatID;
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
     * @return int return the attachmentID
     */
    public int getAttachmentID() {
        return attachmentID;
    }

    /**
     * @param attachmentID the attachmentID to set
     */
    public void setAttachmentID(int attachmentID) {
        this.attachmentID = attachmentID;
    }

    public java.sql.Date getMessageDate(){
        return messageDate;
    }

    public void setMessageDate(java.sql.Date date){
        this.messageDate = date;
    }

    @Override
    public String toString() {
        return "MessageDTO [messsageID=" + messsageID + ", messageContent=" + messageContent + ", chatID=" + chatID
                + ", userID=" + userID + ", messageDate=" + messageDate + ", attachmentID=" + attachmentID + "]";
    }



    

}
