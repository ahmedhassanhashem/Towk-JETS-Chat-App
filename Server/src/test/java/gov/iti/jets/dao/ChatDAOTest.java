package gov.iti.jets.dao;

import org.junit.jupiter.api.BeforeEach;

public class ChatDAOTest extends MockingDBUtiltiy{
    private ChatDAO chatDAO;

    @BeforeEach
    void initDAO() throws Exception {
        chatDAO = new ChatDAO();
    }
}
