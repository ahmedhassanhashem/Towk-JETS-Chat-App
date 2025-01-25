package gov.iti.jets.controller;
import javafx.scene.layout.Region;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class MessageCardController {

    @FXML
    private HBox messageContainerr;

    @FXML
    private VBox messageContainer;

    @FXML
    private Label nameLabel;

    @FXML
    private TextFlow messageTextFlow;

    @FXML
    private Text messageText;

    @FXML
    private Label timeLabel;

    public void setMessageData(String userName, String messageContent, String timestamp, boolean isSender) {
    if (isSender) {
        nameLabel.setVisible(false); 
        messageContainer.getStyleClass().add("sender-message"); 
        messageContainerr.setAlignment(javafx.geometry.Pos.CENTER_RIGHT); 
    } else {
        nameLabel.setText(userName);
        messageContainer.getStyleClass().add("receiver-message"); 
        messageContainerr.setAlignment(javafx.geometry.Pos.CENTER_LEFT); 
    }

    messageText.setText(messageContent);

    messageText.wrappingWidthProperty().bind(messageTextFlow.widthProperty());

    messageContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);
    messageContainer.setMaxWidth(300); //max limit
}

}