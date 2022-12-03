package dev.onebiteaidan.worldshop.DataManagement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface Database {

   void connect() throws SQLException;
   boolean isConnected();
   void disconnect();
   ResultSet query(String query);
   void update(String update);


}
