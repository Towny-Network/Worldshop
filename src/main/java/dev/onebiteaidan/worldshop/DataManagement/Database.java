package dev.onebiteaidan.worldshop.DataManagement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface Database {

   void connect() throws SQLException;
   boolean isConnected();
   void disconnect();
   Connection getConnection();
   ResultSet query(String query);
   void update(String update);
   void insert(String insertion);
   void delete(String deletion);
   void run(String command);

}
