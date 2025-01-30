package gov.iti.jets.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Properties;

import gov.iti.jets.client.Images;
import gov.iti.jets.dao.AttachementDAO;
import gov.iti.jets.dao.MessageDAO;
import gov.iti.jets.dao.UserDAOInterface;
import gov.iti.jets.dto.AttachementDTO;
import gov.iti.jets.dto.ChatDTO;
import gov.iti.jets.dto.MessageDTO;
import gov.iti.jets.dto.UserDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MessageChatController {

    
    ObservableList<MessageDTO> chats = FXCollections.observableArrayList();
    private UserDTO userDTO = new UserDTO();
    private ChatDTO chatDTO = new ChatDTO();
    private AttachementDTO attachement = null;
    // private UserDAO userDAO = new UserDAO();
    private UserDAOInterface userDAO;

    private AttachementDAO attachementDAO = new AttachementDAO();
    private MessageDAO messageDAO = new MessageDAO();
    private int chatID;
    private Stage stage;
    // private int attachId = 0;
    // private String fileName;
    Images images = new Images();
    private byte[] upload;
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
        // if(msgContent.length()==0 && attachement==null)return;
        MessageDTO msg = new MessageDTO();
        msg.setMessageContent(msgContent);
        msg.setChatID(chatID);
        msg.setUserID(userDTO.getUserID());
        msg.setMessageDate( Date.valueOf(LocalDate.now()));
        if(attachement !=null){
            msg.setAttachmentID(attachement.getAttachmentID());
            images.uploadAttachment(attachement.getAttachmentTitle(), upload);
            // sendFile();
        }
        // int attachID = msg.getAttachmentID();

        
        chats.add(messageDAO.create(msg));
        // Platform.runLater(() -> {
        //     listView.layout();   
        //     listView.scrollTo(listView.getItems().size() - 1);
        // });

        attachement=null;
        text.setText("");
    }

    @FXML
    private void attach(ActionEvent event){
        FileChooser fil_chooser = new FileChooser();
			File file = fil_chooser.showOpenDialog(stage);
            if(file !=null){
                try (FileInputStream fIn = new FileInputStream(file)) {
                    upload= fIn.readAllBytes();
                    String fileName = file.getName();
                    AttachementDTO attachementDTOCreated = new AttachementDTO();
                    attachementDTOCreated.setAttachmentTitle(fileName); //TODO hashed
                    attachementDTOCreated.setAttachmentSize(file.length());
                    String type = Files.probeContentType(Paths.get(URLEncoder.encode(file.toURI().toString(), "UTF-8")));
                    attachementDTOCreated.setAttachmentType(type);
                    attachement = attachementDAO.createAttachment(attachementDTOCreated);
                    
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
    }


    @FXML
    private void textEnter(ActionEvent event){
        String msgContent = text.getText();
        // if(msgContent.length()==0 && attachement==null)return;
        MessageDTO msg = new MessageDTO();
        msg.setMessageContent(msgContent);
        msg.setChatID(chatID);
        msg.setUserID(userDTO.getUserID());
        msg.setMessageDate( Date.valueOf(LocalDate.now()));
        if(attachement !=null){
            msg.setAttachmentID(attachement.getAttachmentID());
            images.uploadAttachment(attachement.getAttachmentTitle(), upload);
            // sendFile();
        }
        // int attachID = msg.getAttachmentID();

        
        chats.add(messageDAO.create(msg));
        // Platform.runLater(() -> {
        //     listView.layout();   
        //     listView.scrollTo(listView.getItems().size() - 1);
        // });

        attachement=null;
        text.setText("");
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
                    private FXMLLoader loader;
                    private MessageCardController messageCardController;
                    private HBox messageCard;
                    
                    @Override
                    protected void updateItem(MessageDTO chat, boolean empty) {
                        super.updateItem(chat, empty);
        
                        if (empty || chat == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            if (loader == null) {
                                try {
                                    loader = new FXMLLoader(getClass().getResource("/screens/messageCard.fxml"));
                                    messageCard = loader.load();
                                    messageCardController = loader.getController();
                                    messageCardController.setStage(stage);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
        
                            try {
                                messageCardController.setMessageData(
                                    userDAO.read(chat.getUserID()), 
                                    chat.getMessageContent(), 
                                    attachementDAO.getAttachmentTitle(chat.getAttachmentID()), 
                                    chat.getMessageDate().toString(), 
                                    chat.getUserID() != user.getUserID(), 
                                    chat.getAttachmentID() != 0
                                );
                            } catch (RemoteException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
        
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
        Properties props = new Properties();
        
        try (InputStream input = getClass().getResourceAsStream("/rmi.properties")) {
            if (input == null) {
                throw new IOException("Properties file not found");
            }
            props.load(input);
        } catch (IOException ex) {
        }


        String ip = props.getProperty("rmi_ip");
        int port = Integer.parseInt(props.getProperty("rmi_port"));

                   Registry reg;
        try {
            reg = LocateRegistry.getRegistry(ip,port);
            userDAO = (UserDAOInterface) reg.lookup("userDAO");

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}