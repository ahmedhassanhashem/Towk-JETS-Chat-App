package gov.iti.jets.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.ImageView;

public class CreateGroupController {

    public class CreateGroupController {
        @FXML
        private ListView<HBox> listView;
        @FXML
        private TextField groupNameField;
        @FXML
        private ImageView photoImageView;
        @FXML
        private Button choosePhotoButton;
        @FXML
        private Button createGroupButton;

        private final ObservableList<HBox> selectedContacts = FXCollections.observableArrayList();
        private final ContactDAO contactDAO = new ContactDAO();
        private final ChatDAO chatDAO = new ChatDAO();
        private final UserChatDAO userChatDAO = new UserChatDAO();
        private Image selectedGroupPhoto;

        @FXML
        private void initialize() {
            listView.setItems(selectedContacts);
            loadContactCells();

            choosePhotoButton.setOnAction(event -> handleChoosePhoto());
            createGroupButton.setOnAction(event -> handleCreateGroup());
        }

        private void loadContactCells() {
            List<ContactDTO> allContacts = contactDAO.getAllContacts();
            for (ContactDTO contact : allContacts) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AddContactToGroupCard.fxml"));
                try {
                    HBox contactCell = loader.load();
                    ContactCellController controller = loader.getController();
                    controller.setContactData(contact.getName(), contact.getPhone());
                    controller.getCheckBox()
                            .setOnAction(event -> handleContactSelection(contact, controller.getCheckBox()));
                    selectedContacts.add(contactCell);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleContactSelection(ContactDTO contact, CheckBox checkBox) {
            if (checkBox.isSelected()) {
                selectedContacts.add(createContactCell(contact));
            } else {
                selectedContacts.removeIf(cell -> hasContact(cell, contact));
            }
        }

        private HBox createContactCell(ContactDTO contact) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AddContactToGroupCard.fxml"));
            try {
                HBox contactCell = loader.load();
                ContactCellController controller = loader.getController();
                controller.setContactData(contact.getName(), contact.getPhone());
                controller.getCheckBox().setSelected(true);
                return contactCell;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        private boolean hasContact(HBox cell, ContactDTO contact) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AddContactToGroupCard.fxml"));
            try {
                ContactCellController controller = loader.getController(cell);
                return controller.getContact().equals(contact);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        private void handleChoosePhoto() {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Group Photo");
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                selectedGroupPhoto = new Image(selectedFile.toURI().toString());
                photoImageView.setImage(selectedGroupPhoto);
            }
        }

        private void handleCreateGroup() {
            String groupName = groupNameField.getText();
            if (!groupName.isEmpty()) {
                List<ContactDTO> members = getSelectedMembers();
                int chatID = chatDAO.createChat(groupName, selectedGroupPhoto);
                for (ContactDTO member : members) {
                    UserChatDAO.addMember(chatID, member.getId());
                }
            }
        }

        private List<ContactDTO> getSelectedMembers() {
            List<ContactDTO> members = new ArrayList<>();
            for (HBox cell : selectedContacts) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AddContactToGroupCard.fxml"));
                try {
                    ContactCellController controller = loader.getController(cell);
                    members.add(controller.getContact());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return members;
        }
    }

    /*
     * @FXML
     * private void initialize() {
     * List<ContactDTO> contactDTOs = contactDAO.getAllContacts();
     * 
     * for (ContactDTO dto : contactDTOs) {
     * try {
     * FXMLLoader loader = new
     * FXMLLoader(getClass().getResource("/screens/AddContactToGroupCard.fxml"));
     * HBox contactCell = loader.load();
     * 
     * ContactCellController controller = loader.getController();
     * controller.setContactData(dto.getName(), dto.getPhone());
     * 
     * contacts.add(contactCell);
     * } catch (IOException e) {
     * e.printStackTrace();
     * }
     * }
     * 
     * listView.setItems(contacts);
     * }
     */

}
