package gov.iti.jets.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

public class FormattingDialogController {
    @FXML
    private HTMLEditor htmlEditor;
    @FXML
    private Button applyButton;

    private MessageChatController parentController;
    private Stage dialogStage;
    //private boolean isNewMessage = true; 
    
    @FXML
    private void initialize() {
        htmlEditor.setStyle("-fx-font: 14px \"System\";");
    }
    public void setParentController(MessageChatController parentController, Stage dialogStage, String currentText) {
        this.parentController = parentController;
        this.dialogStage = dialogStage;
        htmlEditor.setHtmlText(currentText);  
    }
 /*public void setParentController(MessageChatController controller, Stage stage, boolean isNew) {
        this.parentController = controller;
        //this.dialogStage = stage;
        this.isNewMessage = isNew; // Set the flag
/* 
        String currentText = controller.getCurrentMessageContent(); // Use the new method
        if (currentText != null && !currentText.isEmpty()) {
            if (isNewMessage) {
                // New message: Convert plain text to basic HTML for initial editing
                if (!currentText.trim().toLowerCase().startsWith("<html>")) {
                    currentText = "<html><body>" + escapeHtml(currentText) + "</body></html>";
                }
            }
            htmlEditor.setHtmlText(currentText);
        }
            */
    //}*/

    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#039;");
    }
    @FXML
    private void applyFormatting() {
        String formattedText = htmlEditor.getHtmlText();
        parentController.applyFormattedText(formattedText);  
        dialogStage.close();
    }
}
