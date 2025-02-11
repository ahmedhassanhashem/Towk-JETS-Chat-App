package gov.iti.jets.controller;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;

public class EmojiDialogController {

    @FXML
    private FlowPane emojiContainer;
    @FXML
    private TextField searchField;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private HBox categoryBar;
    
    private MessageChatController parentController;
    private Stage dialogStage;
    private String currentCategory = "all";
    

    private final Map<String, Set<String>> categoryGroups = new HashMap<>() {{
        put("smileys", new HashSet<>(Arrays.asList(
            "grinning", "smiley", "smile", "grin", "laughing", "joy", "rofl",
            "happy", "wink", "blush", "innocent", "heart", "love", "kiss",
            "angry", "rage", "cry", "sob", "sleepy", "tired", "cool"
        )));
        
        put("animals", new HashSet<>(Arrays.asList(
            "dog", "cat", "mouse", "hamster", "rabbit", "fox", "bear",
            "panda", "koala", "tiger", "lion", "cow", "pig", "frog",
            "monkey", "chicken", "penguin", "bird", "duck", "eagle"
        )));
        
        put("food", new HashSet<>(Arrays.asList(
            "apple", "banana", "orange", "lemon", "watermelon",
            "grapes", "strawberry", "pizza", "hamburger", "sandwich",
            "hotdog", "fries", "sushi", "bento", "curry", "rice",
            "icecream", "cake", "cookie", "chocolate", "candy"
        )));
        
        put("activities", new HashSet<>(Arrays.asList(
            "soccer", "basketball", "football", "baseball", "tennis",
            "volleyball", "rugby", "golf", "trophy", "medal", "sport",
            "running", "swimming", "surfing", "skiing", "biking"
        )));
        
        put("objects", new HashSet<>(Arrays.asList(
            "phone", "computer", "laptop", "keyboard", "printer",
            "camera", "video", "tv", "radio", "speaker", "bell",
            "book", "pencil", "pen", "paperclip", "scissors",
            "key", "lock", "hammer", "wrench", "gear"
        )));
    }};
    
    @FXML
    private void initialize() {
        setupCategoryBar();
        setupSearchField();
        setupScrollPane();
        displayInitialEmojis();
    }
    
    private void setupCategoryBar() {
        Button allButton = createCategoryButton("All");
        categoryBar.getChildren().add(allButton);
        
        Arrays.asList("Smileys", "Animals", "Food", "Activities", "Objects")
              .forEach(category -> {
                  Button categoryButton = createCategoryButton(category);
                  categoryBar.getChildren().add(categoryButton);
              });
        
        allButton.getStyleClass().add("selected-category");
    }
    
    private void setupSearchField() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> 
            updateEmojiDisplay(currentCategory, newValue.toLowerCase()));
    }
    
    private void setupScrollPane() {
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent;");
        emojiContainer.setStyle("""
            -fx-background-color: white;
            -fx-padding: 10px;
            -fx-hgap: 5px;
            -fx-vgap: 5px;
        """);
    }
    
    private Button createCategoryButton(String category) {
        Button button = new Button(category);
        button.getStyleClass().add("category-button");
        
        String baseStyle = """
            -fx-background-color: transparent;
            -fx-padding: 8px 16px;
            -fx-font-size: 14px;
            -fx-border-radius: 20px;
            -fx-background-radius: 20px;
            -fx-cursor: hand;
            """;
        
        button.setStyle(baseStyle);
        
        button.setOnAction(e -> {
            currentCategory = category.toLowerCase();
            updateEmojiDisplay(currentCategory, searchField.getText().toLowerCase());
            
            categoryBar.getChildren().forEach(node -> {
                if (node instanceof Button) {
                    node.setStyle(baseStyle);
                }
            });
            button.setStyle(baseStyle + "-fx-background-color: #e0e0e0;");
        });
        
        
        button.setOnMouseEntered(e -> {
            if (!currentCategory.equals(category.toLowerCase())) {
                button.setStyle(baseStyle + "-fx-background-color: #f5f5f5;");
            }
        });
        
        button.setOnMouseExited(e -> {
            if (!currentCategory.equals(category.toLowerCase())) {
                button.setStyle(baseStyle);
            }
        });
        
        return button;
    }
    
    private void updateEmojiDisplay(String category, String searchTerm) {
        emojiContainer.getChildren().clear();
        Collection<Emoji> emojis = EmojiManager.getAll();
        
        List<Emoji> filteredEmojis = emojis.stream()
            .filter(emoji -> shouldShowEmoji(emoji, category))
            .filter(emoji -> matchesSearchTerm(emoji, searchTerm))
            .sorted(Comparator.comparing(Emoji::getUnicode))
            .toList();
            
        filteredEmojis.forEach(emoji -> emojiContainer.getChildren().add(createEmojiButton(emoji)));
    }

    private boolean matchesSearchTerm(Emoji emoji, String searchTerm) {
        if (searchTerm.isEmpty()) return true;
        
        String description = emoji.getDescription().toLowerCase();
        boolean matchesDescription = description.contains(searchTerm);
        boolean matchesAliases = emoji.getAliases().stream()
            .anyMatch(alias -> alias.toLowerCase().contains(searchTerm));
        boolean matchesTags = emoji.getTags().stream()
            .anyMatch(tag -> tag.toLowerCase().contains(searchTerm));
            
        return matchesDescription || matchesAliases || matchesTags;
    }
    
    private boolean shouldShowEmoji(Emoji emoji, String category) {
        if (category.equals("all")) {
            return !containsUnwantedElements(emoji);
        }
        
        Set<String> categoryTags = categoryGroups.get(category.toLowerCase());
        if (categoryTags == null) return false;
        
        return !containsUnwantedElements(emoji) && (
            categoryTags.stream().anyMatch(tag -> 
                emoji.getDescription().toLowerCase().contains(tag) ||
                emoji.getAliases().stream().anyMatch(alias -> alias.toLowerCase().contains(tag)) ||
                emoji.getTags().stream().anyMatch(emojiTag -> emojiTag.toLowerCase().contains(tag))
            )
        );
    }
    
    private boolean containsUnwantedElements(Emoji emoji) {
        String desc = emoji.getDescription().toLowerCase();
        return desc.contains("flag") || 
               desc.contains("keycap") || 
               desc.contains("symbol") ||
               desc.contains("button") ||
               desc.contains("sign");
    }

    private Button createEmojiButton(Emoji emoji) {
        Button button = new Button(emoji.getUnicode());
        String baseStyle = """
            -fx-background-color: transparent;
            -fx-font-size: 28px;
            -fx-padding: 10px;
            -fx-cursor: hand;
            -fx-min-width: 50px;
            -fx-min-height: 50px;
            -fx-font-family: 'Segoe UI Emoji', 'Noto Color Emoji', 'Apple Color Emoji', 'Segoe UI Symbol';
            -fx-alignment: center;
            -fx-background-radius: 8px;
            """;
        
        button.setStyle(baseStyle);
        

        button.setOnMouseEntered(e -> 
            button.setStyle(baseStyle + "-fx-background-color: #f0f0f0;"));
        button.setOnMouseExited(e -> 
            button.setStyle(baseStyle));
        
        String tooltipText = emoji.getDescription() + 
            "\nAliases: " + String.join(", ", emoji.getAliases());
        
        Tooltip tooltip = new Tooltip(tooltipText);
        tooltip.setStyle("""
            -fx-font-size: 12px;
            -fx-padding: 8px;
            """);
        button.setTooltip(tooltip);

        button.setOnAction(e -> insertEmoji(emoji.getUnicode()));
        return button;
    }
    
    private void displayInitialEmojis() {
        updateEmojiDisplay("all", "");
    }
    
    public void setParentController(MessageChatController parentController, Stage dialogStage) {
        this.parentController = parentController;
        this.dialogStage = dialogStage;
    }
    
    private void insertEmoji(String emoji) {
        if (parentController != null) {
            parentController.insertEmoji(emoji);
            dialogStage.close();
        }
    }
}