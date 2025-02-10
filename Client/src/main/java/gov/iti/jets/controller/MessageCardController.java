package gov.iti.jets.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import gov.iti.jets.client.Images;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MessageCardController {

    private Stage stage;
    Images images = new Images();

    public void setStage(Stage s) {
        stage = s;

    }

        @FXML
    private ImageView seen;

    @FXML
    private ImageView sent;

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

    public void setSeen(boolean a){
        if(a){
        sent.setVisible(false);
        seen.setVisible(true);
    }else{
        sent.setVisible(true);
        seen.setVisible(false);
    }
    }
    public void setMessageData(String userName, String messageContent, String fileName, String timestamp,
            boolean isSender, boolean isAttachFile) {
        messageTextContainerV.getChildren().clear();
        messageTextFlow.getChildren().clear();
        if (isAttachFile) {
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

                try {

                    FileChooser fil_chooser = new FileChooser();
                    File file = fil_chooser.showSaveDialog(stage);
                    if (file != null) {

                        FileOutputStream fOut = new FileOutputStream(file);
                        byte[] download;
                        download = images.downloadAttachment(fileName);
                        fOut.write(download);
                        fOut.close();
                    }

                } catch (IOException e) {

                    e.printStackTrace();
                }

                System.out.println("Text clicked!");

                // getHostServices().showDocument("http://www.example.com");
            });
            if (messageTextContainerV.getChildren().size() > 1) {
                messageTextContainerV.getChildren().remove(1, messageTextContainerV.getChildren().size());
            }

            hyperlink.wrappingWidthProperty().bind(messageTextFlow.widthProperty());
            messageTextContainerV.getChildren().add(hyperlink);

        } else {
            if (messageTextContainerV.getChildren().size() > 1) {
                messageTextContainerV.getChildren().remove(1, messageTextContainerV.getChildren().size());
            }
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

        displayFormattedContent(messageContent);

        // messageText.setText(messageContent);
        timeLabel.setText(timestamp);
        messageText.wrappingWidthProperty().bind(messageTextFlow.widthProperty());

        messageContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);
        messageContainer.setMaxWidth(300); // max limit
    }

    /*
     * public void setMessageData(String userName, String messageContent, String
     * fileName, String timestamp, boolean isSender, boolean isAttachFile) {
     * nameLabel.setText(userName);
     * timeLabel.setText(timestamp);
     * 
     * messageContainer.getStyleClass().clear();
     * messageContainer.getStyleClass().add(isSender ? "sender-message" :
     * "receiver-message");
     * messageContainerr.setAlignment(isSender ? Pos.CENTER_RIGHT :
     * Pos.CENTER_LEFT);
     * 
     * // Clear old messages and attachments
     * messageTextContainerV.getChildren().clear();
     * messageTextFlow.getChildren().clear();
     * 
     * // Add attachment if present
     * if (isAttachFile && fileName != null && !fileName.isEmpty()) {
     * handleAttachment(fileName);
     * }
     * 
     * displayFormattedContent(messageContent);
     * 
     * messageContainer.setMaxWidth(300);
     * }
     * 
     * private void handleAttachment(String fileName) {
     * Text hyperlink = new Text(fileName);
     * hyperlink.setFont(Font.font("Verdana", 14));
     * hyperlink.setFill(Color.BLUE);
     * hyperlink.setUnderline(true);
     * 
     * hyperlink.setOnMouseEntered(event -> hyperlink.setFill(Color.DARKBLUE));
     * hyperlink.setOnMouseExited(event -> hyperlink.setFill(Color.BLUE));
     * 
     * hyperlink.setOnMouseClicked(event -> downloadAttachment(fileName));
     * 
     * messageTextContainerV.getChildren().add(hyperlink);
     * }
     * 
     * 
     * private void downloadAttachment(String fileName) {
     * FileChooser fileChooser = new FileChooser();
     * File file = fileChooser.showSaveDialog(stage);
     * 
     * if (file != null) {
     * try (FileOutputStream fOut = new FileOutputStream(file)) {
     * byte[] data = images.downloadAttachment(fileName);
     * fOut.write(data);
     * } catch (IOException e) {
     * e.printStackTrace();
     * }
     * }
     * }
     * 
     */

    private boolean isHtml(String content) {
        return content.trim().matches("(?i).*<(html|body|b|i|u|span|strong|em|p|div|br)[^>]*>.*");
    }

    private void displayFormattedContent(String content) {
        messageTextFlow.getChildren().clear();

        if (isHtml(content)) {
            TextFlow formattedContent = HtmlToTextFlowConverter.convertHtmlToTextFlow(content);
            messageTextFlow.getChildren().addAll(formattedContent.getChildren());
        } else {
            Text plainText = new Text(content);
            plainText.setWrappingWidth(280); 
            messageTextFlow.getChildren().add(plainText);
        }
    }

}