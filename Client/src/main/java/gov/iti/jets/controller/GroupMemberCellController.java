package gov.iti.jets.controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class GroupMemberCellController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private CheckBox selectCheckBox;

    public void setContactData(String name, String phone) {
        nameLabel.setText(name);
        phoneLabel.setText(phone);
    }

    public String getPhoneNumber() {
        return phoneLabel.getText();
    }
    

    public boolean isSelected() {
        return selectCheckBox.isSelected();
    }

     public CheckBox getAcceptButton() {
        return selectCheckBox;
    }

}
