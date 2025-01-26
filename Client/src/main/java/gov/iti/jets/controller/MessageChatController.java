package gov.iti.jets.controller;

import javafx.scene.layout.Region;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import gov.iti.jets.dao.MessageDAO;
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
import javafx.util.Callback;

public class MessageChatController {

    
    ObservableList<MessageDTO> chats = FXCollections.observableArrayList();
    private UserDTO userDTO = new UserDTO();
    private ChatDTO chatDTO = new ChatDTO();

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

    }

    @FXML
    private void textEnter(ActionEvent event){

    }

    public void setUserDTO(UserDTO user,int chatID) {
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

                        if (chat == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {

                            messageCardController.setMessageData(String.valueOf(chat.getUserID()), chat.getMessageContent(), chat.getMessageDate().toString(), chat.getUserID() ==user.getUserID() );
                            setGraphic(messageCard);
                            // contactCard.setOnMouseClicked((e) -> {
                            //     try {
                            //         final FXMLLoader messageLoader = new FXMLLoader(
                            //                 getClass().getResource("/screens/messageChat.fxml"));
                            //         final BorderPane message = messageLoader.load();
                            //         // MessageChatController messageController = chatLoader.getController();
                            //         // messageController.setImage(user.getUserPicture());
                            //         // messageController.setName(user.getName());
                            //         // messageController.setStatus(user.getUserStatus().toString());
                                    
                            //         // borderPane.setCenter(chat);
                            //     } catch (IOException e1) {
                            //         // TODO Auto-generated catch block
                            //         // e1.printStackTrace();
                            //     }
                            // });
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