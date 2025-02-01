package gov.iti.jets.dao;

import org.junit.jupiter.api.BeforeEach;

public class AttachementDAOTest extends MockingDBUtiltiy{
    private AttachementDAO attachementDAO;

    @BeforeEach
    void initDAO() throws Exception {
        attachementDAO = new AttachementDAO();
    }
}
