package gov.iti.jets.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import gov.iti.jets.config.RMIConfig;
import gov.iti.jets.dao.ChatDAOInterface;
import gov.iti.jets.dao.ContactDAOInterface;
import gov.iti.jets.dao.MessageDAOInterface;
import gov.iti.jets.dao.UserChatDAOInterface;
import gov.iti.jets.dto.UserDTO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;


public class CreateGroupController {

    @FXML
    private VBox borderPane3;

    @FXML
    private TextField groupNameField;
    @FXML
    private Button createGroup;
    @FXML
    private Button choosePhotoButton;
    @FXML
    private ImageView groupImage;
    @FXML
    private ListView<UserDTO> listView;
    @FXML
    private Label alertLabel1;
    @FXML
    private Label alertLabel2;
    @FXML
    private TextField searchField;

    private ChatDAOInterface chatDAO;
    private ContactDAOInterface contactDAO;
    private int createdChatId = -1;
    private byte[] tempImageBytes; 
    private String tempFileName;
    private File tempFile;


    ObservableList<UserDTO> contacts = FXCollections.observableArrayList();
    private List<GroupMemberCellController> cellControllers = new ArrayList<>();
    private FilteredList<UserDTO> filteredContacts;
    private UserDTO userDTO = new UserDTO();
    private Stage stage;
    private Scene settingsScene;
    private Scene dashboardScene;

    public void setSettingsScene(Scene s) {
        settingsScene = s;
    }

    public void setDashboardScene(Scene s) {
        dashboardScene = s;
    }

    public void setStage(Stage s) {
        stage = s;
    }

    public void setUserDTO(UserDTO user) {
        this.userDTO = user;
    }

    
    @FXML
    private void initialize() {
        RMIConfig p = null;
                try { 
                    InputStream inputStream = getClass().getResourceAsStream("/rmi.xml");
                    JAXBContext context = JAXBContext.newInstance(RMIConfig.class);
                    Unmarshaller unmarshaller = context.createUnmarshaller();
                    p = (RMIConfig) unmarshaller.unmarshal(inputStream);
                    inputStream.close();
            // System.out.println(p.getIp() +" " + p.getPort());
        } catch (JAXBException ex) {
            ex.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String ip =p.getIp();
        int port = p.getPort();

        Registry reg;
        try {
            reg = LocateRegistry.getRegistry(ip, port);
            chatDAO = (ChatDAOInterface) reg.lookup("chatDAO");
            contactDAO = (ContactDAOInterface) reg.lookup("contactDAO");

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        filteredContacts = new FilteredList<>(contacts, contact -> true);
        listView.setItems(filteredContacts);  
        //listView.setItems(contacts);
        listView.setSelectionModel(null);
        alertLabel1.setVisible(false); 
        alertLabel2.setVisible(false);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredContacts.setPredicate(userDto -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                String userName = userDto.getName().toLowerCase();
                
                return userName.contains(lowerCaseFilter);
            });
        });
        Circle clip = new Circle();
        clip.setRadius(75); 
        clip.setCenterX(75);
        clip.setCenterY(75);
        groupImage.setClip(clip);
    }

    public void loadContacts() {
        System.out.println("UserDTO phone in CreateGroupController: " + userDTO.getPhone());

        ObservableList<UserDTO> AllContacts = FXCollections.observableArrayList();
        try {
            AllContacts = FXCollections.observableArrayList(contactDAO.findAllContactsACCEPTED(userDTO.getPhone()));
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("DAO returned: " + AllContacts.size() + " accepted requests");

        listView.setCellFactory(new Callback<ListView<UserDTO>, ListCell<UserDTO>>() {
            @Override
            public ListCell<UserDTO> call(ListView<UserDTO> param) {
                return new ListCell<UserDTO>() {
                    private HBox requestCard;
                    private GroupMemberCellController controller;
        
                    { 
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AddContactToGroupCard.fxml"));
                            requestCard = loader.load();
                            controller = loader.getController();
                            cellControllers.add(controller);
                        } catch (IOException e) {
                            e.printStackTrace(); 
                        }
                    }
        
                    @Override
                    protected void updateItem(UserDTO user, boolean empty) {
                        super.updateItem(user, empty);
        
                        if (empty || user == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            controller.setContactData(user.getName(), user.getPhone());
                            setGraphic(requestCard);
                        }
                    }
                };
            }
        });

        contacts.addAll(AllContacts);
    }
    
    @FXML
    private void handleCreateGroup() {
        if (userDTO == null) {
            System.out.println("userDTO is not set!");
            return;
        }

        List<String> participantPhones = new ArrayList<>();
        String creatorPhone = userDTO.getPhone();

        for (GroupMemberCellController controller : cellControllers) {
            if (controller.isSelected()) {
                participantPhones.add(controller.getPhoneNumber());
            }
        }

        if (participantPhones.size() < 1) {
            alertLabel2.setVisible(true);
            return;
        } else {
            alertLabel2.setVisible(false);
        }

        String groupName = groupNameField.getText().trim();

        if (groupName.isEmpty()) {
            alertLabel1.setVisible(true);
            return;
        } else {
            alertLabel1.setVisible(false);
        }
        System.out.println(participantPhones.size());
        System.out.println(groupName);
        System.out.println(creatorPhone);
      try {
            createdChatId = chatDAO.createGroup(userDTO.getPhone(), participantPhones, groupName,tempImageBytes);

            if (createdChatId != -1) {
                System.out.println("Group created successfully. Chat ID: " + createdChatId);
                stage.close();

                if (tempImageBytes != null) {
                    try {
                        chatDAO.updateChatPicture(createdChatId, tempFileName, tempImageBytes);
                        tempImageBytes = null; 
                        tempFileName = null;
                        tempFile = null;
                    } catch (RemoteException ex) {
                        System.err.println("Error uploading image: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }

            } else {
                System.out.println("Failed to create group. Check the logs for details.");
            }
        } catch (RemoteException e) {
            System.err.println("Error occurred while creating the group: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleexitButton() {
        if (stage != null) {
            stage.close();
        } else {
            System.err.println("Stage is not set for CreateGroupController!");
        }
    }

    @FXML
    private void handlechoosePhotoButton() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        File file = chooser.showOpenDialog(stage);

        if (file != null) {
            try {
                tempImageBytes = imageToByteArray(file);
                tempFileName = file.getName();
                tempFile = file;
                Image img = new Image(file.toURI().toString());
                groupImage.setImage(img);

            } catch (IOException e) {
                System.err.println("Error converting image: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private byte[] imageToByteArray(File imgFile) throws FileNotFoundException, IOException{
        try(FileInputStream input = new FileInputStream(imgFile);
            ByteArrayOutputStream output = new ByteArrayOutputStream()){
                byte[] buffer = new byte[1024];
                int length;
                while((length = input.read(buffer)) != -1)
                    output.write(buffer, 0 , length);
                
                return output.toByteArray();
        }
    }
    public void applyRoundedCorners(Region region, double radius) {
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(region.getWidth(), region.getHeight());
        clip.setArcWidth(radius * 2);
        clip.setArcHeight(radius * 2);
        region.setClip(clip);
    
        region.widthProperty().addListener((obs, oldVal, newVal) -> clip.setWidth(newVal.doubleValue()));
        region.heightProperty().addListener((obs, oldVal, newVal) -> clip.setHeight(newVal.doubleValue()));
    }
    
}