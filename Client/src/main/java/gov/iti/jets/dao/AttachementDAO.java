package gov.iti.jets.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AttachementDAO {
    DatabaseConnectionManager meh;
    public AttachementDAO(){
         meh = DatabaseConnectionManager.getInstance();
    }
        public String getAttachmentTitle(int id) { 
        if(id <1)return "";
        String sql = "Select attachmentTitle From Attachment where attachmentID = ?";
        ResultSet re;
        try (Connection con = meh.getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(sql);){
            
            preparedStatement.setInt(1, id);
            re = preparedStatement.executeQuery();
            re.next();
            return re.getString(1);
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return null;

    }
}
