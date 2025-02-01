package gov.iti.jets.dao;

import org.junit.jupiter.api.BeforeEach;

public class AnnoucementDAOTest extends MockingDBUtiltiy{
private AnnoucementDAOTest annoucementDAOTest;

    @BeforeEach
    void initDAO() throws Exception {
        annoucementDAOTest = new AnnoucementDAOTest();
    }
}
