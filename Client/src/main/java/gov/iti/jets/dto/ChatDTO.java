package gov.iti.jets.dto;

public class ChatDTO {

    private int chatID;
    private ChatType chatType;
    private String chatName;
    private byte[] chatPicture;

    
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
     * @return ChatType return the chatType
     */
    public ChatType getChatType() {
        return chatType;
    }

    /**
     * @param chatType the chatType to set
     */
    public void setChatType(ChatType chatType) {
        this.chatType = chatType;
    }

    /**
     * @return String return the chatName
     */
    public String getChatName() {
        return chatName;
    }

    /**
     * @param chatName the chatName to set
     */
    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    /**
     * @return byte[] return the chatPicture
     */
    public byte[] getChatPicture() {
        return chatPicture;
    }

    /**
     * @param chatPicture the chatPicture to set
     */
    public void setChatPicture(byte[] chatPicture) {
        this.chatPicture = chatPicture;
    }


}