package gov.iti.jets.controller;

import javafx.scene.layout.Region;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MessageCardController {

    private Stage stage;
    public void setStage(Stage s) {
        stage = s;

    }

    @FXML
    private HBox messageContainerr;

    @FXML
    private VBox messageContainer;

    @FXML
    private VBox messageTextContainerV;

    @FXML
    private Label nameLabel;

    @FXML
    private TextFlow messageTextFlow;

    @FXML
    private Text messageText;

    @FXML
    private Label timeLabel;

    public void setMessageData(String userName, String messageContent,String fileName, String timestamp, boolean isSender,boolean isAttachFile) {
    if(isAttachFile){
        Text hyperlink = new Text(fileName);
        hyperlink.setFont(Font.font("Verdana", 14));
        hyperlink.setFill(Color.BLUE);
        hyperlink.setUnderline(true);
        hyperlink.setOnMouseEntered(event -> {
            hyperlink.setFill(Color.DARKBLUE); 
            hyperlink.getScene().setCursor(javafx.scene.Cursor.HAND);
        });
        

        hyperlink.setOnMouseExited(event -> {
            hyperlink.setFill(Color.BLUE); 
            hyperlink.getScene().setCursor(javafx.scene.Cursor.DEFAULT);
        });

        hyperlink.setOnMouseClicked(event -> {
            FileChooser fil_chooser = new FileChooser();
			File file = fil_chooser.showSaveDialog(stage);
            try (FileOutputStream fOut = new FileOutputStream(file)) {
                
            } catch (IOException e) {
                
                e.printStackTrace();
            }

            System.out.println("Text clicked!");
            
            // getHostServices().showDocument("http://www.example.com");
        });
        messageTextContainerV.getChildren().add(hyperlink);
    }
    if (isSender) {
        // nameLabel.setVisible(false); 
        messageContainer.getStyleClass().add("sender-message"); 
        messageContainerr.setAlignment(javafx.geometry.Pos.CENTER_RIGHT); 
    } else {
        messageContainer.getStyleClass().add("receiver-message"); 
        messageContainerr.setAlignment(javafx.geometry.Pos.CENTER_LEFT); 
    }
    nameLabel.setText(userName);

    messageText.setText(messageContent);
    timeLabel.setText(timestamp);
    messageText.wrappingWidthProperty().bind(messageTextFlow.widthProperty());

    messageContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);
    messageContainer.setMaxWidth(300); //max limit
}

}