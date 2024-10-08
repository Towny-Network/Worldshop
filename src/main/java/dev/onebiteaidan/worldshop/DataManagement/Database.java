package dev.onebiteaidan.worldshop.DataManagement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public interface Database {

   void connect() throws SQLException;
   boolean isConnected();
   void disconnect();
   Connection getConnection();
   // Queries need to have the connection passed in because a ResultSet cannot be accessed after the database connection is closed.
   ResultSet query(String query, Object[] arguments, int[] types, Connection connection);
   void update(String update, Object[] arguments, int[] types);
   void run (String command);
   void createTradesTable();
   void createPickupsTable();
   void createPlayersTable();

}
