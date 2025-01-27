package gov.iti.jets.controller;

import javafx.scene.layout.Region;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

import gov.iti.jets.dao.AttachementDAO;
import gov.iti.jets.dao.MessageDAO;
import gov.iti.jets.dao.UserDAO;
import gov.iti.jets.dto.ChatDTO;
import gov.iti.jets.dto.MessageDTO;
import gov.iti.jets.dto.UserDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MessageChatController {

    
    ObservableList<MessageDTO> chats = FXCollections.observableArrayList();
    private UserDTO userDTO = new UserDTO();
    private ChatDTO chatDTO = new ChatDTO();
    private UserDAO userDAO = new UserDAO();
    private AttachementDAO attachementDAO = new AttachementDAO();
    private MessageDAO messageDAO = new MessageDAO();
    private int chatID;
    private Stage stage;
    public void setStage(Stage s) {
        stage = s;
    }
    @FXML
    private ListView<MessageDTO> listView;
    @FXML
    private ImageView image;

    @FXML
    private Label name;

    @FXML
    private Label status;

    @FXML
    private TextField text;


    public void setImage(byte[] i) {
        if (i != null) {

            ByteArrayInputStream inputStream = new ByteArrayInputStream(i);
            Image image = new Image(inputStream);
            this.image.setImage(image);
        }
    }

    public void setName(String s) {
        if (s != null)
            name.setText(s);
    }

    public void setStatus(String s) {
        if (s != null)
            status.setText(s);
    }

    @FXML
    private void send(ActionEvent event){
        String msgContent = text.getText();
        if(msgContent.length()==0)return;
        MessageDTO msg = new MessageDTO();
        msg.setMessageContent(msgContent);
        msg.setChatID(chatID);
        msg.setUserID(userDTO.getUserID());
        msg.setMessageDate( Date.valueOf(LocalDate.now()));
 
        // int attachID = msg.getAttachmentID();

        messageDAO.create(msg);
        chats.add(msg);
    }

    @FXML
    private void attach(ActionEvent event){
        
    }
    @FXML
    private void textEnter(ActionEvent event){
        String msgContent = text.getText();
        if(msgContent.length()==0)return;
        MessageDTO msg = new MessageDTO();
        msg.setMessageContent(msgContent);
        msg.setChatID(chatID);
        msg.setUserID(userDTO.getUserID());
        msg.setMessageDate( Date.valueOf(LocalDate.now()));
 
        // int attachID = msg.getAttachmentID();

        messageDAO.create(msg);
        chats.add(msg);
    }

    public void setUserDTO(UserDTO user,int chatID) {
        this.chatID = chatID;
        listView.setItems(chats);
        userDTO = user;
        ObservableList<MessageDTO> chatDTOs = new MessageDAO().findAllMessages(chatID);

        listView.setCellFactory(new Callback<ListView<MessageDTO>, ListCell<MessageDTO>>() {
            @Override
            public ListCell<MessageDTO> call(ListView<MessageDTO> p) {
                return new ListCell<MessageDTO>() {
                    protected void updateItem(MessageDTO chat, boolean empty) {
                        // super.updateItem(item, empty);
                        FXMLLoader messageChattLoader = new FXMLLoader(getClass().getResource("/screens/messageCard.fxml"));
                        HBox messageCard = null;
                        try {
                            messageCard = messageChattLoader.load();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
        
                        MessageCardController messageCardController = messageChattLoader.getController();
                        messageCardController.setStage(stage);
                        // System.out.println(stage);
                        if (chat == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {

                            messageCardController.setMessageData(userDAO.read(chat.getUserID()), chat.getMessageContent(),attachementDAO.getAttachmentTitle(chat.getAttachmentID()), chat.getMessageDate().toString(), chat.getUserID() !=user.getUserID(),chat.getAttachmentID()!=0 );
                            setGraphic(messageCard);

                        }
                    }

                };

            }
        });

        chats.addAll(chatDTOs);
    }
    
    @FXML
    private void initialize() {

           

    }

}