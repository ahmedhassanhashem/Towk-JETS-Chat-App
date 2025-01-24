package gov.iti.jets.dto;

public class AttachementDTO {

    private int attachmentID;
    private String attachmentTitle;
    private String attachmentType;
    private int attachmentSize;


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

    /**
     * @return String return the attachmentTitle
     */
    public String getAttachmentTitle() {
        return attachmentTitle;
    }

    /**
     * @param attachmentTitle the attachmentTitle to set
     */
    public void setAttachmentTitle(String attachmentTitle) {
        this.attachmentTitle = attachmentTitle;
    }

    /**
     * @return String return the attachmentType
     */
    public String getAttachmentType() {
        return attachmentType;
    }

    /**
     * @param attachmentType the attachmentType to set
     */
    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    /**
     * @return int return the attachmentSize
     */
    public int getAttachmentSize() {
        return attachmentSize;
    }

    /**
     * @param attachmentSize the attachmentSize to set
     */
    public void setAttachmentSize(int attachmentSize) {
        this.attachmentSize = attachmentSize;
    }

}
