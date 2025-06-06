package gov.iti.jets.dto;

import java.io.Serializable;

public class ContactDTO implements Serializable{
        private int id;
        private int senderID;
        private int receiverID;
        private RequestStatus requestStatus;


        public ContactDTO(){}


         public ContactDTO(int senderID, int receiverID, RequestStatus requestStatus) {
            this.senderID = senderID;
            this.receiverID = receiverID;
            this.requestStatus = requestStatus;
        }

        /**
     * @return int return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return int return the senderID
     */
    public int getSenderID() {
        return senderID;
    }

    /**
     * @param senderID the senderID to set
     */
    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    /**
     * @return int return the receiverID
     */
    public int getReceiverID() {
        return receiverID;
    }

    /**
     * @param receiverID the receiverID to set
     */
    public void setReceiverID(int receiverID) {
        this.receiverID = receiverID;
    }

    /**
     * @return RequestStatus return the requestStatus
     */
    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    /**
     * @param requestStatus the requestStatus to set
     */
    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }
    
}


