package gov.iti.jets.chatbot;
public class BotService {
    private static BotService instance;
    private boolean botServiceEnabled = false;
    private BotService() {}
    public static BotService getInstance() {
        if (instance == null) {
            instance = new BotService();
        }
        return instance;
    }
    public boolean getBotServiceStatus() {
        return botServiceEnabled;
    }
    public void setBotServiceStatus(boolean enabled) {
        this.botServiceEnabled = enabled;
    }
}